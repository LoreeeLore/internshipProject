package com.studlabs.bll.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Size;

@AllArgsConstructor
@Data
public class MessageTag {
    @Size(min = 1, max = 100)
    private String user;
    private int messageId;

    public MessageTag(String username, Integer id) {
        this.user=username;
        this.messageId=id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
}
