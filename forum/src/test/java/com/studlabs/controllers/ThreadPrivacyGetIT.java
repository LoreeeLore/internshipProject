package com.studlabs.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-servlet-config.xml"})
@ActiveProfiles(profiles = "test")
@Transactional
public class ThreadPrivacyGetIT {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testGetUsers() throws Exception {
        MvcResult result = mockMvc.perform(get("/threads/2/privacy/")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<String> users = objectMapper.readValue(content, List.class);

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(t -> t.equals("u")));
        assertTrue(users.stream().anyMatch(t -> t.equals("username")));
    }

    @Test
    public void testGetUsersEmpty() throws Exception {
        MvcResult result = mockMvc.perform(get("/threads/1/privacy/")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<String> users = objectMapper.readValue(content, List.class);

        assertEquals(0, users.size());
    }

    @Test
    public void testGetUsersThread3() throws Exception {
        MvcResult result = mockMvc.perform(get("/threads/3/privacy/")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<String> users = objectMapper.readValue(content, List.class);

        assertEquals(1, users.size());
        assertTrue(users.stream().anyMatch(t -> t.equals("u")));
    }

    @Test
    public void testGetUsersPublic() throws Exception {
        //thread 4 is a public thread
        mockMvc.perform(get("/threads/4/privacy/")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());

    }

    @Test
    public void testGetUsersBadThread() throws Exception {
        mockMvc.perform(get("/threads/4253252/privacy/")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

    }
}
