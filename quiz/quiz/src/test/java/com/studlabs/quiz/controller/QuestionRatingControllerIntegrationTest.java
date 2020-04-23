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
public class QuestionRatingControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private QuestionRatingService questionRatingService;

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
        questionRatingService.findAll().forEach(t -> questionRatingService.delete(t.getIdUser(), t.getIdQuestion()));

        questionService.findAll().forEach(t -> questionService.delete(t.getIdQuestion()));
    }

    @Test
    public void testAddQuestionRatingValidIds() throws Exception {
        Question question = new Question(4, "descriere1", "category1",
                null, QuestionDifficulty.EASY, true);

        int idQuestion = questionService.insert(question);

        QuestionRating questionRating = new QuestionRating("1", idQuestion, true);

        mockMvc.perform(post("/questions/rate/" + questionRating.getIdQuestion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertFalse(questionService.findAll().isEmpty());
    }

    @Test
    public void testAddQuestionRatingAlreadyExists() throws Exception {
        Question question = new Question(4, "descriere1", "category1",
                null, QuestionDifficulty.EASY,  true);

        int idQuestion = questionService.insert(question);

        QuestionRating questionRating = new QuestionRating("admin", idQuestion, true);
        questionRatingService.insert(questionRating);

        mockMvc.perform(post("/questions/rate/" + questionRating.getIdQuestion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testDeleteQuestionRatingValidIds() throws Exception {
        Question question = new Question(6, "new description", null, null,
                QuestionDifficulty.EASY,  false);

        int idQuestion = questionService.insert(question);

        QuestionRating questionRating = new QuestionRating("admin", idQuestion, true);
        questionRatingService.insert(questionRating);

        mockMvc.perform(delete("/questions/rate/" + questionRating.getIdQuestion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(questionRatingService.findAll().isEmpty());
    }

    @Test
    public void testDeleteQuestionRatingInvalidIds() throws Exception {
        QuestionRating questionRating = new QuestionRating("5", 1, true);

        mockMvc.perform(delete("/questions/rate/" + questionRating.getIdQuestion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionRating))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetAllQuestionRatings() throws Exception {
        Question question = new Question(6, "new description", null, null,
                QuestionDifficulty.EASY,  false);

        int idQuestion = questionService.insert(question);

        QuestionRating questionRating = new QuestionRating("admin", idQuestion, true);
        questionRatingService.insert(questionRating);

        mockMvc.perform(get("/questions/rate/")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUser").value("admin"))
                .andExpect(jsonPath("$[0].idQuestion").value(idQuestion))
                .andExpect(jsonPath("$[0].like").value(true));
    }
}
