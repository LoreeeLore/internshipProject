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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-servlet-config.xml"})
@ActiveProfiles(profiles = "test")
@Transactional
public class TagControllerIT {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getAll() throws Exception {
        mockMvc.perform(get("/tags")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    public void save() throws Exception {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tags", Arrays.asList("child"));

        mockMvc.perform(post("/tags")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(tags)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteTag() throws Exception {
        Map<String, List<String>> tags = new HashMap<>();
        tags.put("tags", Arrays.asList("child"));

        mockMvc.perform(delete("/tags")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(tags)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
