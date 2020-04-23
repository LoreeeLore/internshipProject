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
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
public class QuestionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private QuestionService questionService;

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
        questionService.findAll().forEach(t ->
                questionService.delete(t.getIdQuestion())
        );
    }

    @Test
    public void testAddQuestion() throws Exception {
        Question question = new Question(1, "descriere55", "category1", null,
                QuestionDifficulty.EASY,  true);

        mockMvc.perform(post("/questions/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        assertFalse(questionService.findAll().isEmpty());
    }

    @Test
    public void testDeleteQuestionValidId() throws Exception {
        Question question = new Question(1, "new description", null, null, QuestionDifficulty.EASY,  false);

        int idQuestion = questionService.insert(question);

        mockMvc.perform(delete("/questions/" + idQuestion)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(questionService.findAll().isEmpty());
    }

    @Test
    public void testDeleteQuestionInvalidId() throws Exception {
        Question question = new Question(1, "new description", "type1", null, QuestionDifficulty.EASY,  false);

        mockMvc.perform(delete("/questions/" + question.getIdQuestion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testGetAllQuestions() throws Exception {
        Question question = new Question(1, "desc1", "cat1", null, QuestionDifficulty.EASY,  false);

        questionService.insert(question);

        mockMvc.perform(get("/questions/")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("desc1"))
                .andExpect(jsonPath("$[0].category").value("cat1"))
                .andExpect(jsonPath("$[0].deprecated").value(false));

    }

    @Test
    public void testUpdateQuestionValidId() throws Exception {
        Question question1 = new Question(1, "desc1", "cat1", null, QuestionDifficulty.HARD,  false);

        int id = questionService.insert(question1);

        Question question2 = new Question(id, "desc1", "cat1", null, QuestionDifficulty.MODERATE,  false);

        mockMvc.perform(put("/questions/" + id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question2))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Question resultQuestion = questionService.findQuestionById(id);

        assertEquals(resultQuestion, question2);
    }

    @Test
    public void testUpdateQuestionInvalidId() throws Exception {
        Question question = new Question(1, "desc1", "cat1", "", QuestionDifficulty.HARD,  false);

        mockMvc.perform(put("/questions/" + question.getIdQuestion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testFindQuestionByIdValid() throws Exception {
        Question question = new Question(1, "description", null, null, QuestionDifficulty.EASY,  false);

        int idQuestion = questionService.insert(question);

        mockMvc.perform(get("/questions/" + idQuestion)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andExpect(jsonPath("$.idQuestion").value(idQuestion))
                .andExpect(jsonPath("$.description").value(question.getDescription()));
    }

    @Test
    public void testFindQuestionByIdInvalid() throws Exception {
        Question question = new Question(1, "description", null, "", QuestionDifficulty.EASY,  false);

        mockMvc.perform(get("/questions/" + question.getIdQuestion())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testSetDeprecatedQuestionExists() throws Exception {
        Question question = new Question(1, "desc1", "cat1", null, QuestionDifficulty.HARD,  false);

        int id = questionService.insert(question);

        mockMvc.perform(put(String.format("/questions/deprecated/%d?isDeprecated=true", id))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Question resultQuestion = questionService.findQuestionById(id);

        assertTrue(resultQuestion.isDeprecated());
    }

    @Test
    public void testSetDeprecatedQuestionDoesNotExist() throws Exception {
        Question question = new Question(500, "desc1", "cat1", "", QuestionDifficulty.HARD,  false);

        mockMvc.perform(put(String.format("/questions/deprecated/%d?isDeprecated=true", question.getIdQuestion()))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(question))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }
}
