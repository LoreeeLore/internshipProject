package com.studlabs.bll.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ValidImage
public class Image {
    private Integer id;
    private Integer messageId;
    @ApiModelProperty(notes = "base64 encoded image")
    @Size(max = Constants.MAX_IMAGE_SIZE)
    private String image;

    public Image(Integer messageId, String image) {
        this.messageId = messageId;
        this.image = image;
    }

    public Image(String image) {
        this.image = image;
    }

    public Image(int i, int i1, String o) {
        this.id=i;
        this.messageId=i1;
        this.image=o;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
