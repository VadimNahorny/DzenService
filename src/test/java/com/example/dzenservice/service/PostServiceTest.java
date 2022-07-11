package com.example.dzenservice.service;

import com.example.dzenservice.dao.UserRepository;
import com.example.dzenservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
class PostServiceTest {


    @Autowired
    UserRepository userRepository;
    @Autowired
    PostService postService;

    private static final String LOGIN = "weffgrsferfrqrwr3545429ruriejsdkfjsdkf4u8rur230ufoifjsdcfke0wefffewf4" +
            "asdwesadat4tfsfert4etect4ext4yeyb7i7u5vyc4wera3wxr4tcgrtg3xwt5rhtgrsc6vrby";


    @Test
    void clearIdShownPosts() {
        User userTest = new User();
        userTest.setIdShownPosts(new ArrayList<>(Arrays.asList(1L, 2L)));
        userTest.setLogin(LOGIN);
        userRepository.save(userTest);
        postService.clearIdShownPosts(userTest);
        User userTestFromBase = userRepository.findByLogin(LOGIN);
        userRepository.deleteById(userTestFromBase.getId());
        assertTrue(userTestFromBase.getIdShownPosts().isEmpty());
    }
}