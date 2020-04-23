package com.studlabs.quiz.controller;

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

import static com.studlabs.quiz.util.TokenProvider.*;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
@WebAppConfiguration
public class QuestionQuizControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private QuestionQuizService questionQuizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private RestTemplate restTemplate;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @After
    public void cleanup() {
        questionQuizService.findAll().forEach(t -> questionQuizService.deleteQuestionFromQuiz(t.getIdQuiz(), t.getIdQuestion()));
        questionService.findAll().forEach(t -> questionService.delete(t.getIdQuestion()));
        quizService.findAll().forEach(t -> quizService.delete(t.getIdQuiz()));
    }

    @Test
    public void assignQuestionToQuizValidIds() throws Exception {
        int idQuestion = questionService.insert(new Question());
        int idQuiz = quizService.insert(new Quiz("", QuizDifficulty.MODERATE, 0,120, true, false));

        mockMvc.perform(post("/quizQuestion/" + idQuiz + "/" + idQuestion)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        Assert.assertEquals(questionQuizService.findAll().get(0), new QuestionQuiz(idQuestion, idQuiz));
    }

    @Test
    public void assignQuestionToQuizTooManyQuestions() throws Exception {
        int idQuiz = quizService.insert(new Quiz("", QuizDifficulty.MODERATE, 0,120, true, false));
        int idQuestion = questionService.insert(new Question());

        for (int i = 0; i < 100; i++) {
            questionQuizService.assignQuestionToQuiz(idQuiz, questionService.insert(new Question()));
        }

        mockMvc.perform(post("/quizQuestion/" + idQuiz + "/" + idQuestion)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void assignQuestionToQuizInvalidIds() throws Exception {
        int idQuestion = questionService.insert(new Question());
        int idQuiz = quizService.insert(new Quiz("", QuizDifficulty.MODERATE, 0,120, true, false));

        questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);

        mockMvc.perform(post("/quizQuestion/" + idQuiz + "/" + idQuestion)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteQuestionFromQuizValidIds() throws Exception {
        int idQuestion = questionService.insert(new Question());
        int idQuiz = quizService.insert(new Quiz("", QuizDifficulty.MODERATE, 0, 120,true, false));

        questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);

        mockMvc.perform(delete("/quizQuestion/" + idQuiz + "/" + idQuestion)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(questionQuizService.findAll().isEmpty());
    }

    @Test
    public void deleteQuestionFromQuizInvalidIds() throws Exception {
        mockMvc.perform(delete("/quizQuestion/1/2")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }
}
