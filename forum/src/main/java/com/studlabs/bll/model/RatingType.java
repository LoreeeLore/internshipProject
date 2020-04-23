package com.studlabs.bll.model;

public enum RatingType {
    UPVOTE("upVote"),
    DOWNVOTE("downVote");


    private String name;

    RatingType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static RatingType fromString(String text) {
        for (RatingType b : RatingType.values()) {
            if (b.name.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }
}
