package com.studlabs.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.bll.model.Message;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-servlet-config.xml"})
@ActiveProfiles(profiles = "test")
@Transactional
public class ThreadControllerCloseValidateIT {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    @Autowired
    private ThreadDao threadDao;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void testClose() throws Exception {
        //check if thread is open
        ForumThread thread = new ForumThread("IT", "public", "Software architectures");
        //save a thread
        ForumThread savedThread = threadDao.save(thread);

        assertEquals("open", savedThread.getState());


        mockMvc.perform(get("/threads/1/close")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        //check if it was closed
        Optional<ForumThread> byId = threadDao.findById(1);

        assertEquals("closed", byId.get().getState());
    }

    @Test
    public void testValidate() throws Exception {
        ForumThread thread = new ForumThread("IT", "public", "Software architectures");
        //save a thread
        ForumThread savedThread = threadDao.save(thread);

        assertEquals("open", savedThread.getState());

        mockMvc.perform(get("/threads/" + savedThread.getId() + "/validate")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        //check if it was validated
        Optional<ForumThread> byId = threadDao.findById(savedThread.getId());

        assertEquals("validated", byId.get().getState());
    }

    @Test
    public void testGetAllOpen() throws Exception {

        //save some open threads
        ForumThread thread = new ForumThread("IT", "public", "Software architectures");
        //save a thread
        ForumThread savedThreads = threadDao.save(thread);
        threadDao.save(thread);
        threadDao.save(thread);

        assertEquals(3, threadDao.findAllOpen().size());

        //validate one of the threads
        threadDao.validate(savedThreads.getId());

        MvcResult result = mockMvc.perform(get("/threads/open")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<ForumThread> threads = objectMapper.readValue(content,
                new TypeReference<List<ForumThread>>() {
                });

        assertEquals(2, threads.size());
    }

    @Test
    public void testGetAllOpenCloseSomeThreads() throws Exception {

        //save some threads
        ForumThread savedThreads = threadDao.save(new ForumThread("IT", "public", "Software architectures"));
        ForumThread savedThreads2 = threadDao.save(new ForumThread("IT", "public", "Software architectures"));
        ForumThread savedThreads3 = threadDao.save(new ForumThread("IT", "public", "Software architectures"));
        threadDao.save(new ForumThread("IT", "public", "Software architectures"));
        threadDao.save(new ForumThread("IT", "public", "Software architectures"));

        //close two threads and validate one
        threadDao.close(savedThreads.getId());
        threadDao.close(savedThreads2.getId());
        threadDao.validate(savedThreads3.getId());

        MvcResult result = mockMvc.perform(get("/threads/open")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<ForumThread> threads = objectMapper.readValue(content,
                new TypeReference<List<ForumThread>>() {
                });

        assertEquals(2, threads.size());
    }

    @Test
    public void testCloseInexistent() throws Exception {
        mockMvc.perform(get("/threads/123423/close")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void testValidateInexistent() throws Exception {
        mockMvc.perform(get("/threads/242352/validate")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveMessageInClosedThread() throws Exception {
        //first close a thread
        threadDao.close(2);

        LocalDateTime aDateTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30, 40);
        Message message = new Message(1, "u", "salutare iQuest", aDateTime);

        mockMvc.perform(post("/threads/2/messages/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(message)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveWithState() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 1, 1, 1);

        ForumThread ft = new ForumThread("12345", "public", "abcd",
                new ArrayList<>(), localDateTime);
        ft.setState("validated");

        MvcResult result = mockMvc.perform(post("/threads/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(ft)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ForumThread thread = objectMapper.readValue(content, ForumThread.class);

        thread = threadDao.findById(thread.getId()).get();
        //check if it has been saved
        assertEquals("12345", thread.getCategory());
        assertEquals("validated", thread.getState());
        assertEquals("abcd", thread.getTitle());
    }

    @Test
    public void saveWithStateInvalid() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 1, 1, 1);

        ForumThread ft = new ForumThread("12345", "public", "abcd",
                new ArrayList<>(), localDateTime);
        ft.setState("badState");

        mockMvc.perform(post("/threads/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(ft)))
                .andExpect(status().isBadRequest());
    }
}
