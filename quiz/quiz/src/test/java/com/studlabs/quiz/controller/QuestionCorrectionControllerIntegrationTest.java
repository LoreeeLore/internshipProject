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

import static com.studlabs.quiz.util.TokenProvider.*;
import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
public class QuestionCorrectionControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private QuestionCorrectionService questionCorrectionService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private WebApplicationContext wec;

    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wec).build();

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @After
    public void cleanUp() {
        questionCorrectionService.findAll().forEach(t -> questionCorrectionService.deleteQuestionCorrection(t.getIdUser(), t.getIdQuestion()));
        questionService.findAll().forEach(t -> questionService.delete(t.getIdQuestion()));
    }

    @Test
    public void findAll() throws Exception {
        int idQuestion = addQuestion();
        QuestionCorrection questionCorrection = new QuestionCorrection("1", idQuestion, "?");
        questionCorrectionService.insertQuestionCorrection(questionCorrection);

        List<QuestionCorrection> result = questionCorrectionService.findAll();
        assertEquals(1, result.size());

        mockMvc.perform(get("/correctQuestions/")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$[0].idUser").value(1))
                .andExpect(jsonPath("$[0].idQuestion").value(idQuestion))
                .andExpect(jsonPath("$[0].correctionText").value("?"));
    }

    @Test
    public void testInsertQuestionCorrectionWhenCreated() throws Exception {
        int idQuestion = addQuestion();
        QuestionCorrection questionCorrection = new QuestionCorrection("1", idQuestion, "?");

        mockMvc.perform(post("/correctQuestions")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionCorrection))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        List<QuestionCorrection> result = questionCorrectionService.findAll();
        assertEquals(1, result.size());

        assertEquals(questionCorrection.getIdUser(), result.get(0).getIdUser());
        assertEquals(questionCorrection.getIdQuestion(), result.get(0).getIdQuestion());
        assertEquals(questionCorrection.getCorrectionText(), result.get(0).getCorrectionText());
    }

    @Test
    public void testInsertQuestionCorrectionWhenAlreadyExisting() throws Exception {
        int idQuestion = addQuestion();
        QuestionCorrection questionCorrection = new QuestionCorrection("1", idQuestion, "?");
        questionCorrectionService.insertQuestionCorrection(questionCorrection);

        List<QuestionCorrection> result = questionCorrectionService.findAll();
        assertEquals(1, result.size());

        mockMvc.perform(post("/correctQuestions")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionCorrection))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The object already exists"))
                .andDo(MockMvcResultHandlers.print());

        result = questionCorrectionService.findAll();
        assertEquals(1, result.size());
    }

    @Test
    public void testUpdateQuestionCorrectionWhenOk() throws Exception {
        int idQuestion = addQuestion();
        QuestionCorrection questionCorrection = new QuestionCorrection("admin", idQuestion, "???");
        questionCorrectionService.insertQuestionCorrection(questionCorrection);
        questionCorrection.setCorrectionText("?");

        mockMvc.perform(put(String.format("/correctQuestions/%s", idQuestion))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionCorrection))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        List<QuestionCorrection> result = questionCorrectionService.findAll();
        assertEquals(1, result.size());
        assertEquals(questionCorrection, result.get(0));
    }

    @Test
    public void testUpdateQuestionCorrectionWhenNotFound() throws Exception {
        int idQuestion = addQuestion();
        QuestionCorrection questionCorrection = new QuestionCorrection("1", idQuestion, "???");

        mockMvc.perform(put(String.format("/correctQuestions/%s", idQuestion))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionCorrection))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Couldn't find the object you are trying to update"))
                .andDo(MockMvcResultHandlers.print());

        List<QuestionCorrection> result = questionCorrectionService.findAll();
        assertEquals(0, result.size());
    }

    @Test
    public void testDeleteQuestionCorrectionWhenOk() throws Exception {
        int idQuestion = addQuestion();
        QuestionCorrection questionCorrection = new QuestionCorrection("admin", idQuestion, "???");
        questionCorrectionService.insertQuestionCorrection(questionCorrection);

        mockMvc.perform(delete(String.format("/correctQuestions/%s", idQuestion))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(questionCorrectionService.findAll().isEmpty());
    }

    @Test
    public void testDeleteQuestionCorrectionWhenNotFound() throws Exception {
        int idQuestion = addQuestion();

        mockMvc.perform(delete(String.format("/correctQuestions/%s", idQuestion))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Couldn't find the object you are trying to delete"))
                .andDo(MockMvcResultHandlers.print());
    }

    private int addQuestion() {
        Question question = new Question();
        question.setDescription("");
        question.setImage(null);
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setDeprecated(false);

        questionService.insert(question);

        List<Question> questions = questionService.findAll();
        return questions.get(questions.size() - 1).getIdQuestion();
    }
}
