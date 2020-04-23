package com.studlabs.bll.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ForumThread {
    private Integer id;

    @NotNull
    @NotBlank
    private String category;

    @ApiModelProperty(notes = "Public or private")
    @NotNull
    @Pattern(regexp = "public|private")
    private String access;

    @Size(min = 1, max = 30)
    @NotNull
    private String title;

    @Size(max = 20)
    @NotNull
    private List<@Size(min = 1, max = 20) @NotNull String> tags;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime date;

    @Size(min = 1, max = Constants.MAX_USERNAME_LENGTH)
    private String user;

    @Pattern(regexp = "open|validated")
    private String state;

    public ForumThread(String category, String access, String title) {
        this.category = category;
        this.access = access;
        this.title = title;
        tags = new ArrayList<>();
    }

    public ForumThread(int id, String category, String access, String title) {
        this.id = id;
        this.category = category;
        this.access = access;
        this.title = title;
        tags = new ArrayList<>();
    }

    public ForumThread(String category, String access, String title, LocalDateTime date) {
        this.category = category;
        this.access = access;
        this.title = title;
        this.date = date;
        tags = new ArrayList<>();
    }

    public ForumThread(String category, String access, String title, List<String> tags, LocalDateTime date) {
        this.category = category;
        this.access = access;
        this.title = title;
        this.tags = tags;
        this.date = date;
    }

    public ForumThread(int id, String category, String access, String title, LocalDateTime date) {
        this.id = id;
        this.category = category;
        this.access = access;
        this.title = title;
        this.date = date;
        tags = new ArrayList<>();
    }

    public ForumThread(int id, String category, String access, String title, List<String> tags) {
        this.id = id;
        this.category = category;
        this.access = access;
        this.title = title;
        this.tags = tags;
    }

    public ForumThread(String category, String access, String title, List<String> tags) {
        this.category = category;
        this.access = access;
        this.title = title;
        this.tags = tags;
    }

    public ForumThread(Integer id, String category,
                       String access,
                       String title,
                       List<String> tags,
                       LocalDateTime date) {
        this.id = id;
        this.category = category;
        this.access = access;
        this.title = title;
        this.tags = tags;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
