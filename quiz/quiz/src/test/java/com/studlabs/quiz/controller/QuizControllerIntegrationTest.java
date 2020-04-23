package com.studlabs.quiz.controller;

import com.fasterxml.jackson.databind.*;
import com.studlabs.quiz.configuration.*;
import com.studlabs.quiz.model.*;
import com.studlabs.quiz.service.*;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;
import org.springframework.test.context.web.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.result.*;
import org.springframework.test.web.servlet.setup.*;
import org.springframework.web.client.*;
import org.springframework.web.context.*;

import java.util.*;
import java.util.stream.*;

import static com.studlabs.quiz.util.TokenProvider.*;
import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
@WebAppConfiguration
public class QuizControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionQuizService questionQuizService;

    @Autowired
    private QuizAccessService quizAccessService;

    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));

        quizService.findAll().forEach(t -> quizService.delete(t.getIdQuiz()));
        quizAccessService.findAll().forEach(t -> quizAccessService.deleteUserFromQuiz(t.getIdUser(), t.getIdQuiz()));
        questionService.findAll().forEach(t -> questionService.delete(t.getIdQuestion()));
    }

    @Test
    public void testAddQuiz() throws Exception {
        Quiz quiz = new Quiz(1, "english", QuizDifficulty.HARD, 4.0,60, true, true);

        mockMvc.perform(post("/quiz/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        List<Quiz> result = quizService.findAll();

        assertFalse(result.isEmpty());
        assertEquals(quiz.getCategory(), result.get(0).getCategory());
        assertEquals(quiz.getDifficulty(), result.get(0).getDifficulty());
        assertEquals(quiz.getCompletionRate(), result.get(0).getCompletionRate());
        assertEquals(quiz.isPublic(), result.get(0).isPublic());
        assertEquals(quiz.isRandom(), result.get(0).isRandom());
    }
/*
    @Test
    public void testGenerateQuizValidNumberOfQuestions() throws Exception {
        int noOfQuestions=0;
        for (int i = 0; i < 5; i++) {
            Question question = new Question();
            question.setCategory("football");

            questionService.insert(question);
            noOfQuestions++;
        }

                mockMvc.perform(post("/quiz/generate?category=football&numberOfQuestions="+noOfQuestions+"&timeInMinutes=0")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        List<Quiz> quizzes = quizService.findAll();
        List<QuestionQuiz> questionQuizList = questionQuizService.findAll();

        assertEquals(quizzes.size(), 1);
        assertEquals(quizzes.get(0).getCategory(), "football");
        assertEquals(questionQuizList.size(), noOfQuestions);
    }


 */
    @Test
    public void testGenerateQuizInvalidNumberOfQuestions() throws Exception {
        mockMvc.perform(post("/quiz/generate?category=all&numberOfQuestions=2")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testFindQuizByIdValid() throws Exception {
        int idQuiz = addQuiz();

        mockMvc.perform(get(String.format("/quiz/%s", idQuiz))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idQuiz").value(idQuiz))
                .andExpect(jsonPath("$.category").value("english"))
                .andExpect(jsonPath("$.difficulty").value("HARD"))
                .andExpect(jsonPath("$.completionRate").value(4.0))
                .andExpect(jsonPath("$.public").value(true))
                .andExpect(jsonPath("$.random").value(true));
    }

    @Test
    public void testFindQuizByIdInvalid() throws Exception {
        mockMvc.perform(get(String.format("/quiz/%s", 1))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testListAllQuizzes() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0,60, true, true);
        quizService.insert(quiz);

        mockMvc.perform(get("/quiz/all?difficulties=" + "&categories=")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("english"))
                .andExpect(jsonPath("$[0].difficulty").value("HARD"))
                .andExpect(jsonPath("$[0].completionRate").value(4.0))
                .andExpect(jsonPath("$[0].public").value(true))
                .andExpect(jsonPath("$[0].random").value(true));

        List<Quiz> result = quizService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    public void testUpdateQuizValidId() throws Exception {
        int idQuiz = addQuiz();
        Quiz quiz = quizService.findById(idQuiz);

        mockMvc.perform(put("/quiz/" + quiz.getIdQuiz())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(quiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        List<Quiz> result = quizService.findAll();
        assertEquals(1, result.size());
        assertEquals(quiz, result.get(0));
    }

    @Test
    public void testUpdateQuizInvalidId() throws Exception {
        Quiz quiz = new Quiz();

        mockMvc.perform(put("/quiz/" + quiz.getIdQuiz())
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(quiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDeleteQuizValidId() throws Exception {
        int idQuiz = addQuiz();

        mockMvc.perform(delete(String.format("/quiz/%s", idQuiz))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(quizService.findAll().isEmpty());
    }

    @Test
    public void testDeleteQuizInvalidId() throws Exception {
        mockMvc.perform(delete(String.format("/quiz/%s", 1))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    private int addQuiz() {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0, 60, true, true);
        return quizService.insert(quiz);
    }

    @Test
    public void testFilterQuizzesByDifficultyWhenListsAreNotEmpty() throws Exception {
        Quiz quiz1 = new Quiz("math", QuizDifficulty.HARD, 4.0, 60,true, true);
        Quiz quiz2 = new Quiz("english", QuizDifficulty.EASY, 4.0,60, true, true);

        quizService.insert(quiz1);
        quizService.insert(quiz2);

        List<QuizDifficulty> difficulties = new ArrayList<>();
        difficulties.add(QuizDifficulty.HARD);
        difficulties.add((QuizDifficulty.MODERATE));
        List<String> categories = new ArrayList<>();
        categories.add("math");

        mockMvc.perform(get("/quiz/all?difficulties=" + difficulties.stream().map(Enum::toString).collect(Collectors.joining(",")) + "&categories=" + categories)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        List<Quiz> filteredQuizzes = quizService.filterQuizzesByFields(difficulties, categories);

        assertEquals(filteredQuizzes.size(), 1);

    }

    @Test
    public void testFilterQuizzesByDifficultyWhenListsAreEmpty() throws Exception {

        List<QuizDifficulty> difficulties = new ArrayList<>();
        List<String> categories = new ArrayList<>();

        mockMvc.perform(get("/quiz/all?difficulties=&categories=")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(quizService.filterQuizzesByFields(difficulties, categories).isEmpty());

    }

    @Test
    public void testFilterQuizzesByDifficultyWhenOneListIsEmpty() throws Exception {

        List<QuizDifficulty> difficulties = new ArrayList<>();
        difficulties.add(QuizDifficulty.HARD);
        List<String> categories = new ArrayList<>();

        mockMvc.perform(get("/quiz/all?difficulties=" + difficulties.stream().map(Enum::toString).collect(Collectors.joining(",")) + "&categories=")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(quizService.filterQuizzesByFields(difficulties, categories).isEmpty());

    }

    @Test
    public void testSetPublicQuizWhenExist() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0,120, true, true);
        int idQuiz = quizService.insert(quiz);

        mockMvc.perform(put(String.format("/quiz/public/%d?isPublic=false", idQuiz))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Quiz result = quizService.findById(idQuiz);
        assertFalse(result.isPublic());
    }

    @Test
    public void testSetPublicQuizWhenDoesNotExist() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0,30, true, true);

        mockMvc.perform(put(String.format("/quiz/public/%d?isPublic=false", quiz.getIdQuiz()))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testBrowseAvailableQuizzesWhenListIsEmpty() throws Exception {

        mockMvc.perform(get("/quiz/available")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(quizService.findAll().isEmpty());

    }

    @Test
    public void testBrowseAvailableQuizzesWhenListIsNotEmpty() throws Exception {

        Quiz quiz1 = new Quiz(1, "english", QuizDifficulty.HARD, 5.0, 120,true, false);
        Quiz quiz2 = new Quiz(2, "english", QuizDifficulty.HARD, 6.0, 120,true, false);

        quizService.insert(quiz1);
        quizService.insert(quiz2);

        mockMvc.perform(get("/quiz/available")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertEquals(quizService.findAll().size(), 2);
        assertEquals(quizService.browseAllQuizzes("5").size(), 2);

    }
}
