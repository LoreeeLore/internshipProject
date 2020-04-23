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
public class AnswerControllerIntegrationTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AnswerService answerService;

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
    public void cleanup() {
        answerService.findAllAnswers().forEach(t -> answerService.deleteAnswer(t.getIdAnswer()));
        questionService.findAll().forEach(t -> questionService.delete(t.getIdQuestion()));
    }

    @Test
    public void findAllAnswers() throws Exception {
        int idQuestion = addQuestion();
        int idAnswer = addAnswer(idQuestion);

        mockMvc.perform(get("/answers/")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$[0].idAnswer").value(idAnswer))
                .andExpect(jsonPath("$[0].idQuestion").value(idQuestion))
                .andExpect(jsonPath("$[0].correct").value(false))
                .andExpect(jsonPath("$[0].text").value("Asia"));

        List<Answer> result = answerService.findAllAnswers();
        assertEquals(1, result.size());
    }

    @Test
    public void findAnswerByIdValid() throws Exception {
        int idQuestion = addQuestion();
        int idAnswer = addAnswer(idQuestion);

        mockMvc.perform(get(String.format("/answers/%s", idAnswer))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.idAnswer").value(idAnswer))
                .andExpect(jsonPath("$.idQuestion").value(idQuestion))
                .andExpect(jsonPath("$.correct").value(false))
                .andExpect(jsonPath("$.text").value("Asia"));
    }

    @Test
    public void findAnswerByIdInvalid() throws Exception {
        mockMvc.perform(get(String.format("/answers/%s", 1))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void insertAnswer() throws Exception {
        int idQuestion = addQuestion();
        Answer answer = new Answer(idQuestion, false, "Europe");

        mockMvc.perform(post("/answers/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(answer))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        List<Answer> result = answerService.findAllAnswers();
        assertEquals(1, result.size());

        assertEquals(answer.getIdQuestion(), result.get(0).getIdQuestion());
        assertEquals(answer.isCorrect(), result.get(0).isCorrect());
        assertEquals(answer.getText(), result.get(0).getText());
    }

    @Test
    public void insertAnswerTooManyAnswers() throws Exception {
        int idQuestion = addQuestion();

        for (int i = 0; i < 8; i++) {
            answerService.insertAnswer(new Answer(idQuestion, false, "Germany"));
        }

        Answer answer = new Answer(idQuestion, false, "Europe");

        mockMvc.perform(post("/answers/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(answer))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateAnswerValidId() throws Exception {
        int idQuestion = addQuestion();
        int idAnswer = addAnswer(idQuestion);
        Answer answer = new Answer(idAnswer, idQuestion, false, "America");

        mockMvc.perform(put(String.format("/answers/%s", idAnswer))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(answer))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        List<Answer> result = answerService.findAllAnswers();

        assertEquals(1, result.size());
        assertEquals(answer, result.get(0));
    }

    @Test
    public void updateAnswerInvalidId() throws Exception {
        Answer answer = new Answer(1, 2, false, "America");

        mockMvc.perform(put(String.format("/answers/%s", answer.getIdAnswer()))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(answer))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteAnswerValidId() throws Exception {
        int idQuestion = addQuestion();
        int idAnswer = addAnswer(idQuestion);

        mockMvc.perform(delete(String.format("/answers/%s", idAnswer))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(answerService.findAllAnswers().isEmpty());
    }

    @Test
    public void deleteAnswerInvalidId() throws Exception {
        mockMvc.perform(delete(String.format("/answers/%s", 1))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    private int addAnswer(int idQuestion) {
        Answer answer = new Answer();
        answer.setIdQuestion(idQuestion);
        answer.setCorrect(false);
        answer.setText("Asia");

        return answerService.insertAnswer(answer);
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
