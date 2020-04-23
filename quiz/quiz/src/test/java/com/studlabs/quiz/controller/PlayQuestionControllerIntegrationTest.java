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

import static com.studlabs.quiz.util.TokenProvider.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
@WebAppConfiguration
public class PlayQuestionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PlayQuestionService playQuestionService;

    @Autowired
    private PlayQuizService playQuizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionQuizService questionQuizService;

    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    public void testFindPlayQuestionByIdValid() throws Exception {
        int idQuiz = quizService.insert(new Quiz());

        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdUser("1");
        playQuiz.setIdQuiz(idQuiz);

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

        int idPlayQuiz = playQuizService.insert(playQuiz.getIdUser(), playQuiz.getIdQuiz());

        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdQuestion(idQuestion);
        playQuestion.setIdPlayQuiz(idPlayQuiz);

        int idPlayQuestion = playQuestionService.insert(playQuestion);

        mockMvc.perform(get("/playQuestions/" + idPlayQuestion)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.idQuestion").value(idQuestion))
                .andExpect(jsonPath("$.idPlayQuiz").value(idPlayQuiz));
    }

    @Test
    public void testFindPlayQuestionByIdInvalid() throws Exception {
        mockMvc.perform(get("/playQuestions/800")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }
    /*
    @Test
    public void updatePlayQuestionValidId() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setPublic(true);

        int id = quizService.insert(quiz);

        assertTrue(quizService.exists(id));

        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdUser("admin");
        playQuiz.setIdQuiz(id);

        int idQuestion = 0;
        Answer answer;

        for (int i = 0; i < 5; i++) {
            idQuestion = questionService.insert(new Question());
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(id, idQuestion);
        }

        int idPlayQuiz = playQuizService.insert(playQuiz.getIdUser(), playQuiz.getIdQuiz());

        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdQuestion(idQuestion);
        playQuestion.setIdPlayQuiz(idPlayQuiz);

        int idPlayQuestion = playQuestionService.insert(playQuestion);

        mockMvc.perform(put("/playQuestions/" + idPlayQuestion)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }


     */
    @Test
    public void testUpdatePlayQuestionInvalidId() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setPublic(false);

        int id = quizService.insert(quiz);

        assertTrue(quizService.exists(id));

        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdUser("1");
        playQuiz.setIdQuiz(id);

        int idQuestion;
        Answer answer;

        for (int i = 0; i < 5; i++) {
            idQuestion = questionService.insert(new Question());
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(id, idQuestion);
        }

        int idPlayQuiz = playQuizService.insert(playQuiz.getIdUser(), playQuiz.getIdQuiz());

        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdPlayQuiz(idPlayQuiz);

        mockMvc.perform(put("/playQuestions/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(playQuestion))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testFindAllQuestionsForQuiz() throws Exception {
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

        playQuestionService.insert(new PlayQuestion(idQuestion, idPlayQuiz, null, null, true));

        mockMvc.perform(get("/playQuestions/findall/" + idPlayQuiz)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idPlayQuiz").value(idPlayQuiz));
    }
}
