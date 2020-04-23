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

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
public class QuizRatingControllerIntegrationTest {

    @Autowired
    private QuizRatingService quizRatingService;

    @Autowired
    private QuizService quizService;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private RestTemplate restTemplate;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @After
    public void cleanup() {
        quizRatingService.findAll().forEach(t -> quizRatingService.deleteQuizRating(t.getIdQuiz(), t.getIdUser()));
        quizService.findAll().forEach(t -> quizService.delete(t.getIdQuiz()));
    }

    @Test
    public void testGetAllQuizRatings() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setDifficulty(QuizDifficulty.EASY);

        int id = quizService.insert(quiz1);

        QuizRating quizRating1 = new QuizRating("5", id, false);

        quizRatingService.insertQuizRating(quizRating1);

        mockMvc.perform(get("/quizRatings/")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUser").value(quizRating1.getIdUser()))
                .andExpect(jsonPath("$[0].idQuiz").value(id))
                .andExpect(jsonPath("$[0].like").value(quizRating1.isLike()));

        assertEquals(quizRatingService.findAll().size(), 1);
    }

    @Test
    public void testAddQuizRatingValidIds() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setDifficulty(QuizDifficulty.EASY);

        int id = quizService.insert(quiz1);

        QuizRating quizRating = new QuizRating("8", id, true);

        mockMvc.perform(post("/quizRatings/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quizRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAddQuizRatingAlreadyExists() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setDifficulty(QuizDifficulty.EASY);

        int id = quizService.insert(quiz1);

        QuizRating quizRating = new QuizRating("8", id, true);
        quizRatingService.insertQuizRating(quizRating);

        mockMvc.perform(post("/quizRatings/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quizRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testUpdateQuizRatingValidIds() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setDifficulty(QuizDifficulty.EASY);

        int id = quizService.insert(quiz1);

        QuizRating quizRating = new QuizRating("admin", id, false);
        quizRatingService.insertQuizRating(quizRating);

        mockMvc.perform(put("/quizRatings/" + id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quizRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testUpdateQuizRatingInvalidIds() throws Exception {
        QuizRating quizRating = new QuizRating("1", 1, false);

        mockMvc.perform(put("/quizRatings/" + 1)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quizRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testFindQuizRatingByIdValid() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setDifficulty(QuizDifficulty.EASY);
        int id = quizService.insert(quiz1);

        QuizRating quizRating = new QuizRating("admin", id, false);

        quizRatingService.insertQuizRating(quizRating);

        mockMvc.perform(get("/quizRatings/" + id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quizRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testFindQuizRatingByIdInvalid() throws Exception {
        QuizRating quizRating = new QuizRating("1", 1, false);

        mockMvc.perform(get("/quizRatings/" + quizRating.getIdQuiz())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quizRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDeleteQuizRatingValidIds() throws Exception {
        Quiz quiz1 = new Quiz();
        quiz1.setDifficulty(QuizDifficulty.EASY);

        int id = quizService.insert(quiz1);

        QuizRating quizRating = new QuizRating("admin", id, true);

        quizRatingService.insertQuizRating(quizRating);

        mockMvc.perform(delete("/quizRatings/" + id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quizRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDeleteQuizRatingInvalidIds() throws Exception {
        QuizRating quizRating = new QuizRating("1", 1, true);

        mockMvc.perform(delete("/quizRatings/" + quizRating.getIdQuiz())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quizRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }
}
