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
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
@WebAppConfiguration
public class QuizAccessControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private QuizAccessService quizAccessService;

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
        quizService.findAll().forEach(t -> quizService.delete(t.getIdQuiz()));
    }

    @Test
    public void testFindAllQuizAccess() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0,100, true, true);
        int id = quizService.insert(quiz);

        quizAccessService.assignQuizToUser("admin", id);

        mockMvc.perform(get("/quizAccess/")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUser").value("admin"))
                .andExpect(jsonPath("$[0].idQuiz").value(id));

    }

    @Test
    public void assignQuizToUserWithValidIdsAndQuizPrivate() throws Exception {
        int idQuiz = quizService.insert(new Quiz("english", QuizDifficulty.HARD, 4.0,100, false, true));

        mockMvc.perform(post("/quizAccess/" + idQuiz)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        Assert.assertEquals(quizAccessService.findAll().get(0), new QuizAccess(idQuiz, "admin"));
    }

    @Test
    public void assignQuizToUserWithValidIdsAndQuizPublic() throws Exception {
        int idQuiz = quizService.insert(new Quiz("english", QuizDifficulty.HARD, 4.0,100, true, true));

        mockMvc.perform(post("/quizAccess/" + idQuiz)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void assignQuizToUserWithInvalidIdsAndQuizPrivate() throws Exception {
        int idQuiz = quizService.insert(new Quiz("english", QuizDifficulty.HARD, 4.0,100, false, true));

        quizAccessService.assignQuizToUser("admin", idQuiz);

        mockMvc.perform(post("/quizAccess/" + idQuiz)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void assignQuizToUserWithInvalidIdsAndQuizPublic() throws Exception {
        int idQuiz = quizService.insert(new Quiz("english", QuizDifficulty.HARD, 4.0, 100,true, true));

        quizAccessService.assignQuizToUser("1", idQuiz);

        mockMvc.perform(post("/quizAccess/" + idQuiz)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteQuizFromUserWithValidIds() throws Exception {
        int idQuiz = quizService.insert(new Quiz("english", QuizDifficulty.HARD, 4.0,100, true, true));

        quizAccessService.assignQuizToUser("admin", idQuiz);

        mockMvc.perform(delete("/quizAccess/" + idQuiz)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(quizAccessService.findAll().isEmpty());
    }

    @Test
    public void deleteQuizFromUserWithInvalidIds() throws Exception {
        mockMvc.perform(delete("/quizAccess/" + 4)
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }
}
