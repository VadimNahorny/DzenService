package com.example.dzenservice.dto;

public class UserPostDTO {

    protected long userId;

    protected long postId;

    protected UserPostDTO(long userId, long postId) {
        this.userId = userId;
        this.postId = postId;
    }

    protected long getUserId() {
        return userId;
    }

    protected long getPostId() {
        return postId;
    }

    protected void setUserId(long userId) {
        this.userId = userId;
    }

    protected void setPostId(long postId) {
        this.postId = postId;
    }

    @Override
    public String toString() {
        return "UserPostDTO{" +
                "userId=" + userId +
                ", postId=" + postId +
                '}';
    }
}
