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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-servlet-config.xml"})
@ActiveProfiles(profiles = "test")
@Transactional
public class ThreadPrivacyAddIT {
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
    public void testAddUsers() throws Exception {
        List<String> users = Arrays.asList("username");

        mockMvc.perform(post("/threads/3/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());


        List<String> usersPresent = threadDao.getUsers(3);
        assertEquals(2, usersPresent.size());
        assertTrue(usersPresent.stream().anyMatch(t -> t.equals("u")));
        assertTrue(usersPresent.stream().anyMatch(t -> t.equals("username")));
    }

    @Test
    public void testAddUsersDuplicate() throws Exception {
        List<String> users = Arrays.asList("username", "u");

        mockMvc.perform(post("/threads/3/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAdd2Users() throws Exception {
        List<String> users = Arrays.asList("username", "u");

        mockMvc.perform(post("/threads/1/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        List<String> usersPresent = threadDao.getUsers(1);
        assertEquals(2, usersPresent.size());
        assertTrue(usersPresent.stream().anyMatch(t -> t.equals("u")));
        assertTrue(usersPresent.stream().anyMatch(t -> t.equals("username")));
    }

    @Test
    public void testAddUsersPublic() throws Exception {
        List<String> users = Arrays.asList("username", "u");

        //thread 4 is a public thread
        mockMvc.perform(post("/threads/4/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddUsersBadThreadId() throws Exception {
        List<String> users = Arrays.asList("username", "u");

        //thread 4 is a public thread
        mockMvc.perform(post("/threads/43214432/privacy/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(users)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }
}
