package com.example.dzenservice.restControllers;

import com.example.dzenservice.dto.UserPostDTO;
import com.example.dzenservice.dto.UserPostDTOMapper;
import com.example.dzenservice.entity.Post;
import com.example.dzenservice.entity.User;
import com.example.dzenservice.service.PostService;
import com.example.dzenservice.service.PreferenceMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preferenceMap")
public class PreferenceMapRestController {

    @Autowired
    PreferenceMapBuilder preferenceMapBuilder;
    @Autowired
    PostService postService;
    @Autowired
    UserPostDTOMapper userPostDTOMapper;


    @PostMapping("/addPointsPerLike")
    public void addPointsPerLike(@RequestBody UserPostDTO userPostDTO) {
        Post post = userPostDTOMapper.mapperDTOtoPost(userPostDTO);
        User user = userPostDTOMapper.mapperDTOtoUser(userPostDTO);
        preferenceMapBuilder.addPointsPerLike(post, user);
    }

    @PostMapping("/addPointsPerAdditionFollowing")
    public void addPointsPerAdditionFollowing(@RequestBody List<User> users) {
        preferenceMapBuilder.addPointsPerAdditionFollowing(users);
    }

    @PostMapping("/addPointsPerSeeing")
    public void addPointsPerSeeing(@RequestBody UserPostDTO userPostDTO) {
        Post post = userPostDTOMapper.mapperDTOtoPost(userPostDTO);
        User user = userPostDTOMapper.mapperDTOtoUser(userPostDTO);
        preferenceMapBuilder.addPointsPerSeeing(post, user);
    }

    @PostMapping("/addPointsPerRepost")
    public void addPointsPerRepost(@RequestBody UserPostDTO userPostDTO) {
        Post post = userPostDTOMapper.mapperDTOtoPost(userPostDTO);
        User user = userPostDTOMapper.mapperDTOtoUser(userPostDTO);
        preferenceMapBuilder.addPointsPerRepost(post, user);
    }

    @PostMapping("/addPointsPerComment")
    public void addPointsPerComment(@RequestBody UserPostDTO userPostDTO) {
        Post post = userPostDTOMapper.mapperDTOtoPost(userPostDTO);
        User user = userPostDTOMapper.mapperDTOtoUser(userPostDTO);
        preferenceMapBuilder.addPointsPerComment(post, user);
    }

    @PostMapping("/addPointsPerAdditionPost")
    public void addPointsPerAdditionPost(@RequestBody UserPostDTO userPostDTO) {
        Post post = userPostDTOMapper.mapperDTOtoPost(userPostDTO);
        User user = userPostDTOMapper.mapperDTOtoUser(userPostDTO);
        preferenceMapBuilder.addPointsPerAdditionPost(post, user);
    }

    @GetMapping("/getPostsListForRecentlyEnteredUser")
    public List<Post> getPostsListForRecentlyEnteredUser(@RequestBody User user) {
        return postService.getPostsListForRecentlyEnteredUser(user);
    }

    @GetMapping("/getAdditionalPostList")
    public List<Post> getAdditionalPostList(@RequestBody User user) {
        return postService.getAdditionalPostList(user);
    }

    @PostMapping("/clearIdShownPosts")
    public void addPointsPerAdditionPost(@RequestBody User user) {
        postService.clearIdShownPosts(user);
    }
}
