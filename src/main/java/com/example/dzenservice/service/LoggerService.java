package com.example.dzenservice.service;

import com.example.dzenservice.entity.Post;
import com.example.dzenservice.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LoggerService {

    private static final String LOGGER_MESSAGE_METHOD = "It was used GET method: ";
    private static final String LOGGER_MESSAGE_REQUEST = "Request parameter: user with ID - ";
    private static final String LOGGER_MESSAGE_RESPONSE = "Response parameter: post list with size - ";


    public String getLoggerMessage(User user, List<Post> postList) {
        String methodName = new Object() {}.getClass().getEnclosingMethod().getName();
        return LOGGER_MESSAGE_METHOD + methodName + ". " + LOGGER_MESSAGE_REQUEST + user.getId()+". "+
                LOGGER_MESSAGE_RESPONSE + postList.size() + ".";

    }
}
