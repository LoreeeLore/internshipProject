package com.studlabs.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studlabs.bll.model.Rating;
import com.studlabs.bll.model.RatingType;
import com.studlabs.dao.RatingDaoImpl;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-servlet-config.xml"})
@ActiveProfiles(profiles = "test")
@Transactional
public class RatingIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private RatingDaoImpl ratingDao;


    private ObjectMapper objectMapper = new ObjectMapper();
    //for ratings

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void findRatingByUser() throws Exception {
        mockMvc.perform(get("/threads/2/messages/1/rating/u")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messageId").value(1))
                .andExpect(jsonPath("$.user").value("u"))
                .andExpect(jsonPath("$.type").value("UPVOTE"));

    }

    @Test
    public void saveRating() throws Exception {
        Rating rating = new Rating(2, "username", RatingType.DOWNVOTE);

        mockMvc.perform(post("/threads/2/messages/2/rating/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        //check if the rating has been saved
        Rating rat = ratingDao.findById(2, "username").get();
        assertEquals(Integer.valueOf(2), rat.getMessageId());
        assertEquals("username", rat.getUser());
        assertEquals(RatingType.DOWNVOTE, rat.getType());
    }

    @Test
    public void updateRating() throws Exception {
        Rating rating = new Rating(2, "u", RatingType.DOWNVOTE);

        mockMvc.perform(put("/threads/2/messages/2/rating/u")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(rating)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

        Rating rat = ratingDao.findById(2, "u").get();

        assertEquals(Integer.valueOf(2), rat.getMessageId());
        assertEquals("u", rat.getUser());
        assertEquals(RatingType.DOWNVOTE, rat.getType());
    }

    @Test
    public void removeRating() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/threads/2/messages/2/rating/u")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void findRatingByIdBadUserId() throws Exception {
        mockMvc.perform(get("/threads/2/messages/2/rating/123")
                .contentType("application/json"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void saveRatingIncomplete() throws Exception {
        Rating rating = new Rating(2, "username", null);

        mockMvc.perform(post("/threads/2/messages/2/rating/")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(rating)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRatingIncomplete() throws Exception {
        Rating rating = new Rating(2, "u", null);

        mockMvc.perform(put("/threads/2/messages/2/rating/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(rating)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateRatingInexistent() throws Exception {
        Rating rating = new Rating(2, "1ada", RatingType.UPVOTE);

        mockMvc.perform(put("/threads/2/messages/2/rating/1ada")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(rating)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateRatingBadUser() throws Exception {
        Rating rating = new Rating(2, "not_same", RatingType.UPVOTE);

        mockMvc.perform(put("/threads/2/messages/2/rating/1ada")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(rating)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void removeRatingInexistent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/threads/2/messages/2/rating/1131ad")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());
    }


}
