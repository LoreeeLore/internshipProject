package com.studlabs.quiz.security;

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
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {ApplicationConfiguration.class, RestTemplateMockProvider.class})
public class TestSecurity {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private QuizService quizService;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private RestTemplate restTemplate;

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
    public void testExpiredToken() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0,120, true, true);

        mockMvc.perform(post("/quiz/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + expiredAccessToken))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAccessDenied() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0, 120,true, true);

        mockMvc.perform(post("/quiz/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + userAccessToken))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testNullUniqueName() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0, 120,true, true);

        mockMvc.perform(post("/quiz/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + nullUniqueNameAccessToken))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testNullNameId() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0,120, true, true);

        mockMvc.perform(post("/quiz/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + nullNameIdAccessToken))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testNullToken() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0, 120,true, true);

        mockMvc.perform(post("/quiz/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + null))
                .andExpect(status().isUnauthorized())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testAccessAllowed() throws Exception {
        Quiz quiz = new Quiz("english", QuizDifficulty.HARD, 4.0,120, true, true);

        mockMvc.perform(post("/quiz/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(quiz))
                .header("Authorization", "Bearer " + adminAccessToken))
                .andExpect(status().isCreated())
                .andDo(MockMvcResultHandlers.print());
    }
}
