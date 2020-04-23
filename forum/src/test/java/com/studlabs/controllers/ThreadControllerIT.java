package com.studlabs.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studlabs.bll.model.ForumThread;
import com.studlabs.dao.MessageDao;
import com.studlabs.dao.TagDao;
import com.studlabs.dao.ThreadDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-servlet-config.xml"})
@ActiveProfiles(profiles = "test")
@Transactional
public class ThreadControllerIT {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ThreadDao threadDao;

    @Autowired
    private MessageDao messageDao;

    @Autowired
    private TagDao tagDao;

    @Before
    public void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void save() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 1, 1, 1);

        ForumThread ft = new ForumThread("IT", "public", "Software architectures",
                Arrays.asList("nothing"), localDateTime);

        mockMvc.perform(post("/threads/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(ft)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        //check if it has been saved
        List<ForumThread> allThreads = threadDao.findAllOpen();
        assertTrue(allThreads.stream().anyMatch(thread -> "IT".equals(thread.getCategory())));
        assertTrue(allThreads.stream().anyMatch(thread -> "public".equals(thread.getAccess())));
        assertTrue(allThreads.stream().anyMatch(thread -> "Software architectures".equals(thread.getTitle())));
    }

    @Test
    public void findById() throws Exception {
        mockMvc.perform(get("/threads/1")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.category").value("prog"))
                .andExpect(jsonPath("$.access").value("private"))
                .andExpect(jsonPath("$.title").value("Hello guys"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findAll() throws Exception {
        mockMvc.perform(get("/threads/")
                .contentType("application/json"))
                .andExpect(status().isOk());

        List<ForumThread> threads = threadDao.filterThreads(Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(threads.stream().anyMatch(thread -> "prog".equals(thread.getCategory())));
        assertTrue(threads.stream().anyMatch(thread -> "public".equals(thread.getAccess())));
        assertTrue(threads.stream().anyMatch(thread -> "Hello guys".equals(thread.getTitle())));
        assertTrue(threads.stream().anyMatch(thread -> thread.getTags().contains("book")));
    }

    @Test
    public void update() throws Exception {
        ForumThread ft = new ForumThread(2, "IT22", "public", "Software architectures22",
                Arrays.asList("tag", "prog", "bla"));

        mockMvc.perform(put("/threads/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ft)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Optional<ForumThread> byId = threadDao.findById(2);
        assertEquals("IT22", byId.get().getCategory());
        assertEquals("public", byId.get().getAccess());
        assertEquals("Software architectures22", byId.get().getTitle());
        assertTrue(byId.get().getTags().contains("tag"));
        assertTrue(byId.get().getTags().contains("prog"));
        assertTrue(byId.get().getTags().contains("bla"));
    }

    @Test
    public void getThreadsByMainCategory() throws Exception {
        mockMvc.perform(get("/threads?maincategory=food")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("food"))
                .andExpect(jsonPath("$[0].access").value("private"))
                .andExpect(jsonPath("$[0].title").value("I am a test"));
    }

    @Test
    public void getThreadsByTags() throws Exception {
        mockMvc.perform(get("/threads?tag=computer&tag=school")
                .contentType("application/json"))
                .andExpect(status().isOk());

        List<ForumThread> forumThreadList = threadDao.filterThreads(Optional.empty(), Optional.of(Arrays.asList("computer", "school")),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
        assertTrue(forumThreadList.stream().anyMatch(thread -> thread.getId() == 3));
        assertTrue(forumThreadList.stream().anyMatch(thread -> "food".equals(thread.getCategory())));
        assertTrue(forumThreadList.stream().anyMatch(thread -> "private".equals(thread.getAccess())));
        assertTrue(forumThreadList.stream().anyMatch(thread -> "I am a test".equals(thread.getTitle())));
    }

    @Test
    public void getThreadsByTagsAndMainCategory() throws Exception {
        mockMvc.perform(get("/threads?maincategory=IT&tag=computer&tag=school")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("IT"))
                .andExpect(jsonPath("$[0].access").value("public"))
                .andExpect(jsonPath("$[0].title").value("I am a test"));
    }

    @Test
    public void getThreadsSortedAscByCategory() throws Exception {
        mockMvc.perform(get("/threads?sortBy=category")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("food"));
    }

    @Test
    public void getThreadsSortedDescByCategory() throws Exception {
        mockMvc.perform(get("/threads?sortBy=category&order=desc")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("prog"));
    }

    @Test
    public void getThreadsSortedAscByAccess() throws Exception {
        mockMvc.perform(get("/threads?sortBy=access&order=asc")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].access").value("private"));
    }

    @Test
    public void getThreadsSortedDescByAccess() throws Exception {
        mockMvc.perform(get("/threads?sortBy=access&order=desc")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].access").value("public"));
    }

    @Test
    public void getThreadsSortedAscByTitle() throws Exception {
        mockMvc.perform(get("/threads?sortBy=title&order=asc")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Hello guys"));
    }

    @Test
    public void getThreadsSortedAscByDate() throws Exception {
        mockMvc.perform(get("/threads?sortBy=date&order=asc")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"));
    }

    @Test
    public void getThreadsSortedDescByAscFilterByMainCategoryAndTag() throws Exception {
        mockMvc.perform(get("/threads?sortBy=date&order=asc&maincategory=IT&tag=computer")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("4"));
    }

    @Test
    public void getThreadsSortedDescByAscFilterByMainCategoryAndTags() throws Exception {
        mockMvc.perform(get("/threads?sortBy=date&order=asc&maincategory=IT&tag=computer&tag=school")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("4"));
    }


    @Test
    public void getThreadsSortedByCategoryFilterTags() throws Exception {
        mockMvc.perform(get("/threads?sortBy=title&tag=computer")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("3"))
                .andExpect(jsonPath("$[0].category").value("food"));
    }

    @Test
    public void getThreadsEmptyResult() throws Exception {
        mockMvc.perform(get("/threads?sortBy=date&order=asc&maincategory=IT&tag=computer&tag=school&tag=fruits")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }


    @Test
    public void delete() throws Exception {
        final int threadId = 5;
        //check that message exists for thread
        assertTrue(messageDao.findAll().stream().anyMatch(m -> m.getThreadId() == threadId));

        mockMvc.perform(MockMvcRequestBuilders.delete("/threads/" + threadId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        //check that messages were deleted
        assertFalse(messageDao.findAll().stream().anyMatch(m -> m.getThreadId() == threadId));
    }

    @Test
    public void findBadId() throws Exception {
        mockMvc.perform(get("/threads/20")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveInvalidThread() throws Exception {
        ForumThread forumThread = new ForumThread("cat", "access", "title");
        mockMvc.perform(post("/threads/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(forumThread)))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateInvalidThread() throws Exception {
        ForumThread forumThread = new ForumThread("cat", "access", "title");
        mockMvc.perform(put("/threads/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(forumThread)))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateInvalidId() throws Exception {
        ForumThread forumThread = new ForumThread(2, "cat", "access", "title");
        mockMvc.perform(put("/threads/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(forumThread)))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateNonexistentThread() throws Exception {
        ForumThread forumThread = new ForumThread(20, "cat", "public", "title", Collections.emptyList());
        mockMvc.perform(put("/threads/20")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(forumThread)))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void deleteNonexistentThread() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/threads/12")
                .contentType("application/json"))
                .andExpect(status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void filterByInvalidOrder() throws Exception {
        mockMvc.perform(get("/threads/orderBy=title&order=invalid")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void filterByInvalidField() throws Exception {
        mockMvc.perform(get("/threads?sortBy=invalid&order=asc")
                .contentType("application/json"))
                .andExpect(status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void filterByNonexistentCategory() throws Exception {
        mockMvc.perform(get("/threads?maincategory=invalid")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void saveThreadWithTags() throws Exception {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 1, 1, 1);

        ForumThread ft = new ForumThread("IT", "public", "Software architectures",
                Arrays.asList("tag"), localDateTime);

        MvcResult result = mockMvc.perform(post("/threads/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(ft)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ForumThread thread = objectMapper.readValue(content, ForumThread.class);

        assertTrue(tagDao.getTag("tag").isPresent());
        assertTrue(tagDao.getTagsByThreadId(thread.getId()).stream().anyMatch(tag -> "tag".equals(tag.getTagName())));
    }

    @Test
    public void filterByNonexistentTag() throws Exception {
        mockMvc.perform(get("/threads?tag=invalid")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void updateWithNullId() throws Exception {
        ForumThread ft = new ForumThread(null, "IT", "public", "Software architectures", Arrays.asList("tag", "prog"), LocalDateTime.now());

        mockMvc.perform(put("/threads/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ft)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void testThreadLimit() throws Exception {
        MvcResult result = mockMvc.perform(get("/threads?limit=2")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<ForumThread> threads = objectMapper.readValue(content,
                new TypeReference<List<ForumThread>>() {
                });
        assertTrue(threads.size() == 2);
    }

    @Test
    public void testThreadOffset() throws Exception {
        MvcResult result = mockMvc.perform(get("/threads?offset=100")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        List<ForumThread> threads = objectMapper.readValue(content,
                new TypeReference<List<ForumThread>>() {
                });
        assertEquals(0, threads.size());
    }

    @Test
    public void testInvalidLimit() throws Exception {
        mockMvc.perform(get("/threads?limit=-1")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testInvalidOffset() throws Exception {
        mockMvc.perform(get("/threads?offset=-1")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testLimitWithFilters() throws Exception {
        MvcResult result = mockMvc.perform(get("/threads?limit=2&maincategory=IT")
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
}