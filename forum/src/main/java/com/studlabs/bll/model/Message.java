package com.studlabs.bll.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//@AllArgsConstructor
@NoArgsConstructor
@Data
public class Message {
    private Integer id;
    private Integer threadId;
    @NotNull
    @Size(min = 1, max = Constants.MAX_USERNAME_LENGTH)
    private String user;

    @Size(min = 1, max = Constants.MAX_MESSAGE_TEXT_LENGTH)
    @NotBlank
    private String text;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;
    private int upCounter;
    private int downCounter;

    @NotNull
    private List<@Size(min = 1, max = Constants.MAX_USERNAME_LENGTH) @NotNull String> taggedUserNames;

    @NotNull
    @Size(max = Constants.MAX_IMAGES, message = "Image limit exceeded")
    @Valid
    private List<Image> images;

    public Message(Integer id, Integer threadId, String user, String text,
                   LocalDateTime date, int upCounter, int downCounter) {
        this.id = id;
        this.threadId = threadId;
        this.user = user;
        this.text = text;
        this.date = date;
        this.upCounter = upCounter;
        this.downCounter = downCounter;
        images = new ArrayList<>();
        taggedUserNames = new ArrayList<>();
    }

    public Message(Integer threadId, String user, String text, LocalDateTime date, int upCounter, int downCounter,
                   List<String> taggedUserNames, List<Image> images) {
        this.threadId = threadId;
        this.user = user;
        this.text = text;
        this.date = date;
        this.upCounter = upCounter;
        this.downCounter = downCounter;
        this.taggedUserNames = taggedUserNames;
        this.images = images;
    }

    public Message(Integer threadId,
                   String user,
                   String text,
                   LocalDateTime date) {
        this.threadId = threadId;
        this.user = user;
        this.text = text;
        this.date = date;
        images = new ArrayList<>();
        taggedUserNames = new ArrayList<>();
    }

    public <T> Message(int i,Integer threadId, String user, String text, LocalDateTime date, int upCounter, int downCounter,
                       List<String> taggedUserNames, List<Image> images) {
        this.id = i;this.threadId = threadId;
        this.user = user;
        this.text = text;
        this.date = date;
        this.upCounter = upCounter;
        this.downCounter = downCounter;
        this.taggedUserNames = taggedUserNames;
        this.images = images;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getThreadId() {
        return threadId;
    }

    public void setThreadId(Integer threadId) {
        this.threadId = threadId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getUpCounter() {
        return upCounter;
    }

    public void setUpCounter(int upCounter) {
        this.upCounter = upCounter;
    }

    public int getDownCounter() {
        return downCounter;
    }

    public void setDownCounter(int downCounter) {
        this.downCounter = downCounter;
    }

    public List<String> getTaggedUserNames() {
        return taggedUserNames;
    }

    public void setTaggedUserNames(List<String> taggedUserNames) {
        this.taggedUserNames = taggedUserNames;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }
}
