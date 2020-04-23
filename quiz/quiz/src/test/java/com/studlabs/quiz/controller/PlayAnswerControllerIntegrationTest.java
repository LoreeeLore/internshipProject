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

import java.util.*;

import static com.studlabs.quiz.util.TokenProvider.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
@WebAppConfiguration
public class PlayAnswerControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private PlayQuizService playQuizService;

    @Autowired
    private PlayQuestionService playQuestionService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionQuizService questionQuizService;

    @Autowired
    private PlayAnswerService playAnswerService;

    @Autowired
    private RestTemplate restTemplate;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    public void testInsertPlayAnswerValid() throws Exception {
        int idQuiz = quizService.insert(new Quiz());

        int idQuestion = 0;
        Answer answer;

        for (int i = 0; i < 5; i++) {
            idQuestion = questionService.insert(new Question());
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);
        }

        int idPlayQuiz = playQuizService.insert("admin", idQuiz);

        PlayQuestion playQuestion = new PlayQuestion(idQuestion, idPlayQuiz);

        int idPlayQuestion = playQuestionService.insert(playQuestion);

        mockMvc.perform(post("/playAnswer/" + idPlayQuestion + "?answerIds=&textAnswer=")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testInsertPlayAnswerInvalid() throws Exception {
        mockMvc.perform(post("/playAnswer/" + 80 + "?answerIds=&textAnswer=")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testFindPlayAnswerByIdPlayQuestionValid() throws Exception {
        int idQuiz = quizService.insert(new Quiz());
        int idQuestion = 0;
        Answer answer;

        for (int i = 0; i < 5; i++) {
            idQuestion = questionService.insert(new Question());
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);
        }

        int idPlayQuiz = playQuizService.insert("1", idQuiz);
        playQuizService.findById(idPlayQuiz).setRate(7);

        PlayQuestion playQuestion = new PlayQuestion(idQuestion, idPlayQuiz);

        int id = answerService.insertAnswer(new Answer(idQuestion, true, null));

        List<Integer> answerIds = new ArrayList<>();
        answerIds.add(id);

        int idPlayQuestion = playQuestionService.insert(playQuestion);

        playAnswerService.insert(answerIds, idPlayQuestion, null);

        mockMvc.perform(get("/playAnswer/" + idPlayQuestion)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.idPlayQuestion").value(idPlayQuestion));
    }

    @Test
    public void testFindPlayAnswerQuizByIdPlayQuestionInvalid() throws Exception {
        mockMvc.perform(get("/playAnswer/800")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }
}
