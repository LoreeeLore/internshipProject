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
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
@WebAppConfiguration
public class QuestionSuggestionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private QuestionSuggestionService questionSuggestionService;

    @Autowired
    private RestTemplate restTemplate;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

        Mockito.doReturn(new ResponseEntity<>(HttpStatus.OK)).when(restTemplate).exchange(any(String.class), any(HttpMethod.class), any(HttpEntity.class), any(Class.class));
    }

    @After
    public void tearDown() {
        questionSuggestionService.getAll().forEach(t ->
                questionSuggestionService.delete(t.getIdQuestionSuggestion())
        );
    }

    @Test
    public void getAll() throws Exception {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion(1, "?", "", "animals", "animals", "single answer", QuestionDifficulty.EASY);

        questionSuggestionService.insert(questionSuggestion);

        mockMvc.perform(get("/suggestQuestions/")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUser").value("?"))
                .andExpect(jsonPath("$[0].description").value(""))
                .andExpect(jsonPath("$[0].category").value("animals"))
                .andExpect(jsonPath("$[0].type").value("single answer"))
                .andExpect(jsonPath("$[0].difficulty").value("EASY"));
    }

    @Test
    public void insertQuestionSuggestion() throws Exception {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion("admin", "?", null, "animals", "single answer", QuestionDifficulty.EASY);

        mockMvc.perform(post("/suggestQuestions/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionSuggestion))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());

        List<QuestionSuggestion> result = questionSuggestionService.getAll();
        assertEquals(1, result.size());

        assertEquals(result.get(0).getIdUser(), "admin");
        assertEquals(result.get(0).getDescription(), "?");
        assertEquals(result.get(0).getImage(), null);
        assertEquals(result.get(0).getCategory(), "animals");
        assertEquals(result.get(0).getType(), "single answer");
        assertEquals(result.get(0).getDifficulty(), QuestionDifficulty.EASY);
    }

    @Test
    public void updateQuestionSuggestionWhenOk() throws Exception {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion("1", "?");

        int id = questionSuggestionService.insert(questionSuggestion);
        questionSuggestion.setIdQuestionSuggestion(id);
        questionSuggestion.setDescription("What");

        mockMvc.perform(put("/suggestQuestions/"+ id)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(questionSuggestion))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        List<QuestionSuggestion> result = questionSuggestionService.getAll();
        assertEquals(1, result.size());
        assertEquals(questionSuggestion, result.get(0));
    }

    @Test
    public void updateQuestionSuggestionWhenNotFound() throws Exception {
        mockMvc.perform(put("/suggestQuestions/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(new QuestionSuggestion("1", "?")))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound())
                .andExpect(content().string("Couldn't find the object you are trying to update!"));
    }

    @Test
    public void deleteQuestionSuggestionWhenOk() throws Exception {
        QuestionSuggestion questionSuggestion = new QuestionSuggestion("1", "?", null, "animals", "single answer", QuestionDifficulty.EASY);
        int id = questionSuggestionService.insert(questionSuggestion);

        mockMvc.perform(delete(String.format("/suggestQuestions/%s", id))
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        assertTrue(questionSuggestionService.getAll().isEmpty());
    }

    @Test
    public void deleteQuestionSuggestionWhenNotFound() throws Exception {
        mockMvc.perform(delete("/suggestQuestions/0")
                .contentType("application/json")
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Couldn't find the object you are trying to delete"))
                .andDo(MockMvcResultHandlers.print());
    }
}
