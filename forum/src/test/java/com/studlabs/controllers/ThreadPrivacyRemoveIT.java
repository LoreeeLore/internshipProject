package com.studlabs.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studlabs.dao.ThreadDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-servlet-config.xml"})
@ActiveProfiles(profiles = "test")
@Transactional
public class ThreadPrivacyRemoveIT {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ThreadDao threadDao;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testDeleteUsers() throws Exception {
        List<String> users = Arrays.asList("u");

        mockMvc.perform(delete("/threads/3/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());


        List<String> usersPresent = threadDao.getUsers(3);
        assertEquals(0, usersPresent.size());
    }

    @Test
    public void testDeleteUsersNonExistent() throws Exception {
        List<String> users = Arrays.asList("username", "u");

        mockMvc.perform(delete("/threads/3/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteUsersNonExistent2() throws Exception {
        List<String> users = Arrays.asList("username");

        mockMvc.perform(delete("/threads/3/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDelete2Users() throws Exception {
        List<String> users = Arrays.asList("username", "u");

        mockMvc.perform(delete("/threads/2/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        List<String> usersPresent = threadDao.getUsers(2);
        assertEquals(0, usersPresent.size());
    }

    @Test
    public void testDelete1UserFrom2() throws Exception {
        List<String> users = Arrays.asList("u");

        mockMvc.perform(delete("/threads/2/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        List<String> usersPresent = threadDao.getUsers(2);
        assertEquals(1, usersPresent.size());
        assertTrue(usersPresent.stream().anyMatch(t -> t.equals("username")));
    }

    @Test
    public void testDeleteUsersPublic() throws Exception {
        List<String> users = Arrays.asList("username", "u");

        //thread 4 is a public thread
        mockMvc.perform(delete("/threads/4/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testRemoveUsersBadThreadId() throws Exception {
        List<String> users = Arrays.asList("username", "u");

        //thread 4 is a public thread
        mockMvc.perform(delete("/threads/43214432/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }
}
