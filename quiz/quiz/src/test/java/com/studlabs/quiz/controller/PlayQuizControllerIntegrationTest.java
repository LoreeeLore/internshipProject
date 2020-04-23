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

import java.time.*;
import java.util.*;

import static com.studlabs.quiz.util.TokenProvider.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
@WebAppConfiguration
public class PlayQuizControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PlayQuizService playQuizService;

    @Autowired
    private QuizAccessService quizAccessService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionQuizService questionQuizService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private PlayQuestionService playQuestionService;

    @Autowired
    private PlayAnswerService playAnswerService;

    @Autowired
    private RestTemplate restTemplate;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @Test
    public void testInsertPlayQuizPublicAccess() throws Exception {
        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdUser("1");

        Quiz quiz = new Quiz();
        quiz.setPublic(true);

        int idQuiz = quizService.insert(quiz);
        int idQuestion;
        Answer answer;

        for (int i = 0; i < 5; i++) {
            idQuestion = questionService.insert(new Question());
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);
        }

        mockMvc.perform(post("/playQuiz/" + idQuiz)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(playQuiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testInsertPlayQuizPrivateAccess() throws Exception {
        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdUser("admin");

        int idQuiz = quizService.insert(new Quiz());
        quizAccessService.assignQuizToUser(playQuiz.getIdUser(), idQuiz);

        int idQuestion;
        Answer answer;

        for (int i = 0; i < 5; i++) {
            idQuestion = questionService.insert(new Question());
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);
        }

        mockMvc.perform(post("/playQuiz/" + idQuiz)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(playQuiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testInsertPlayQuizInvalidQuiz() throws Exception {
        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdUser("admin");

        mockMvc.perform(post("/playQuiz/" + 5000)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(playQuiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testInsertPlayQuizInvalidAccess() throws Exception {
        PlayQuiz playQuiz = new PlayQuiz();
        playQuiz.setIdUser("admin");

        int idQuiz = quizService.insert(new Quiz());

        mockMvc.perform(post("/playQuiz/" + idQuiz)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(playQuiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void testFindPlayQuizByIdValid() throws Exception {
        int idQuiz = quizService.insert(new Quiz());
        int idQuestion;
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

        mockMvc.perform(get("/playQuiz/played/" + idPlayQuiz)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.idUser").value("admin"))
                .andExpect(jsonPath("$.idQuiz").value(idQuiz));
    }

    @Test
    public void testFindPlayQuizByIdInvalid() throws Exception {
        mockMvc.perform(get("/playQuiz/played/800")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testFindByUserIdAndQuizId() throws Exception {
        int idQuiz = quizService.insert(new Quiz());
        int idQuestion;
        Answer answer;

        for (int i = 0; i < 5; i++) {
            idQuestion = questionService.insert(new Question());
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);
        }

        playQuizService.insert("admin", idQuiz);

        mockMvc.perform(get("/playQuiz/userPlayed/" + idQuiz)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUser").value("admin"))
                .andExpect(jsonPath("$[0].idQuiz").value(idQuiz));
    }

    @Test
    public void testGetAllPlayQuizzesForUser() throws Exception {
        int idQuiz = quizService.insert(new Quiz());
        int idQuestion;
        Answer answer;

        for (int i = 0; i < 5; i++) {
            idQuestion = questionService.insert(new Question());
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);
        }

        playQuizService.insert("admin", idQuiz);

        mockMvc.perform(get("/playQuiz/")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUser").value("admin"));
    }

    @Test
    public void testFinishQuizStatusPassed() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setCompletionRate(0);
        quiz.setPublic(true);
        int idQuiz = quizService.insert(quiz);

        int idQuestion = 0;
        Answer answer = new Answer();
        Question question;
        int idAnswer = 0;

        for (int i = 0; i < 5; i++) {
            question = new Question();
            question.setDifficulty(QuestionDifficulty.EASY);
            idQuestion = questionService.insert(question);
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            idAnswer = answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);
        }

        int idPlayQuiz = playQuizService.insert("admin", idQuiz);
        playQuizService.findById(idPlayQuiz).setStartTime(null);
        //playQuizService.updateStartTime(idPlayQuiz);
        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdQuestion(idQuestion);
        playQuestion.setIdPlayQuiz(idPlayQuiz);
        playQuestion.setCorrect(true);

        int idPlayQuestion = playQuestionService.insert(playQuestion);

        System.out.println(answer.getIdAnswer());
        playAnswerService.insert(Collections.singletonList(idAnswer), idPlayQuestion, null);

        PlayQuiz playQuiz = playQuizService.findById(idPlayQuiz);
        //playQuiz.setEndTime(LocalDateTime.now());
        mockMvc.perform(put(String.format("/playQuiz/finish/" + playQuiz.getIdPlayQuiz()))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(playQuiz.getIdPlayQuiz()))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk());

        assertEquals(playQuizService.findById(idPlayQuiz).getStatus(), PlayQuizStatus.PASSED);
    }

    @Test
    public void testFinishQuizStatusFailed() throws Exception {
        Quiz quiz = new Quiz();
        quiz.setCompletionRate(50);
        quiz.setPublic(true);
        int idQuiz = quizService.insert(quiz);

        int idQuestion = 0;
        Answer answer = new Answer();
        Question question;

        for (int i = 0; i < 5; i++) {
            question = new Question();
            question.setDifficulty(QuestionDifficulty.EASY);
            idQuestion = questionService.insert(question);
            answer = new Answer();
            answer.setCorrect(true);
            answer.setIdQuestion(idQuestion);

            answerService.insertAnswer(answer);
            questionQuizService.assignQuestionToQuiz(idQuiz, idQuestion);
        }

        int idPlayQuiz = playQuizService.insert("admin", idQuiz);

        PlayQuestion playQuestion = new PlayQuestion();
        playQuestion.setIdQuestion(idQuestion);
        playQuestion.setIdPlayQuiz(idPlayQuiz);
        playQuestion.setCorrect(true);

        int idPlayQuestion = playQuestionService.insert(playQuestion);
        //playQuestionService.updateStartTime(idPlayQuestion);
        playAnswerService.insert(Arrays.asList(answer.getIdAnswer()), idPlayQuestion, "");

        PlayQuiz playQuiz = playQuizService.findById(idPlayQuiz);
        playQuiz.setEndTime(LocalDateTime.now());
        System.out.println(playQuiz.toString());
        mockMvc.perform(put(String.format("/playQuiz/finish/"+ idPlayQuiz))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(playQuiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk());

        assertEquals(playQuizService.findById(idPlayQuiz).getStatus(), PlayQuizStatus.FAILED);
    }

}
