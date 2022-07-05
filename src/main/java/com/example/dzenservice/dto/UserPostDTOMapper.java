package com.example.dzenservice.dto;

import com.example.dzenservice.entity.Post;
import com.example.dzenservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserPostDTOMapper {


    public User mapperDTOtoUser(UserPostDTO userPostDTO) {
        User user = new User();
        user.setId(userPostDTO.getUserId());
        return user;
    }

    public Post mapperDTOtoPost(UserPostDTO userPostDTO) {
        Post post = new Post();
        post.setId(userPostDTO.getPostId());
        return post;
    }

    public UserPostDTO mapperEntityToUserPostDTO(User user, Post post) {
        return new UserPostDTO(user.getId(), post.getId());
    }
}
