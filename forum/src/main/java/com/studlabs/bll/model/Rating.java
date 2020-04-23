package com.studlabs.bll.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Rating {
    private Integer messageId;
    @Size(min = 1, max = Constants.MAX_USERNAME_LENGTH)
    @NotNull
    private String user;
    @NotNull
    private RatingType type;

    public Rating(int i, String u, RatingType downvote) {
        this.messageId = i;
        this.user = u;
        this.type=downvote;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public RatingType getType() {
        return type;
    }

    public void setType(RatingType type) {
        this.type = type;
    }
}
