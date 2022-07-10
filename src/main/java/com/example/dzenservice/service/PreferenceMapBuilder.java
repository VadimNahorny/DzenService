package com.example.dzenservice.service;

import com.example.dzenservice.dao.PostRepository;
import com.example.dzenservice.dao.TagRepository;
import com.example.dzenservice.dao.UserRepository;
import com.example.dzenservice.entity.Post;
import com.example.dzenservice.entity.Tag;
import com.example.dzenservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class PreferenceMapBuilder {

    @Autowired
    PostRepository postRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TagRepository tagRepository;

    public void addPointsPerLike(Post post, User user) {
        addPoints(post, user, 10L);
    }

    public void addPointsPerSeeing(Post post, User user) {
        addPoints(post, user, 1L);
    }

    public void addPointsPerRepost(Post post, User user) {
        addPoints(post, user, 5L);
    }

    public void addPointsPerComment(Post post, User user) {
        addPoints(post, user, 2L);
    }

    public void addPointsPerAdditionPost(Post post, User user) {
        addPoints(post, user, 3L);
    }

    private void addPoints(Post post, User user, Long addPoints) {
        if (postRepository.existsById(user.getId()) && userRepository.existsById(user.getId())) {
            Post postFromBase = postRepository.getOne(post.getId());
            User userFromBase = userRepository.getOne(user.getId());
            List<Tag> tagList = postFromBase.getTagList();
            for (Tag tag : tagList) {
                if (userFromBase.getPreferenceMap().containsKey(tag)) {
                    Long points = userFromBase.getPreferenceMap().get(tag) + addPoints;
                    userFromBase.getPreferenceMap().put(tag, points);
                } else userFromBase.getPreferenceMap().put(tag, addPoints);
            }
            userRepository.save(userFromBase);
        }
    }

    public void addPointsPerAdditionFollowing(List<User> userList) {
        User following = userList.get(0);
        User follower = userList.get(1);
        List<Long> postId = postRepository.getListPostIdByUserId(following.getId());
        List<Long> tagId = new ArrayList<>();
        for (Long id : postId) tagId.addAll(postRepository.getTagIdByPostId(id));
        Map<Long, Long> tagCount = getTagCountMap(tagId);
        Map<Long, Long> sortedMap = sortMapByValue(tagCount);
        Set<Long> tagIdSet = sortedMap.keySet();
        List<Long> listTagId = new ArrayList<>(tagIdSet);
        Collections.reverse(listTagId);
        addPointToUserPreferenceMapPerAdditionFollowing (follower, listTagId, sortedMap);
    }

    private Map <Long, Long> sortMapByValue (Map <Long, Long> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));
    }

    private Map <Long, Long> getTagCountMap (List<Long>  tagId) {
        Map<Long, Long> tagCount = new HashMap<>();
        for (Long id : tagId) {
            if (!tagCount.containsKey(id)) tagCount.put(id, 1L);
            else tagCount.put(id, tagCount.get(id) + 1L);
        }
        return tagCount;
    }

      private void addPointToUserPreferenceMapPerAdditionFollowing (User follower, List<Long> listTagId,
                                                                    Map<Long, Long> sortedMap) {
        User userFollowerFromBase = userRepository.getOne(follower.getId());
        long points = 15;
        for (int i = 0; i < listTagId.size(); i++) {
            Tag tag = tagRepository.getOne(listTagId.get(i));
            if (userFollowerFromBase.getPreferenceMap().containsKey(tag)) {
                Long allPoints = userFollowerFromBase.getPreferenceMap().get(tag) + points;
                userFollowerFromBase.getPreferenceMap().put(tag, allPoints);
            } else userFollowerFromBase.getPreferenceMap().put(tag, points);
            if (i != (listTagId.size() - 1) &&
                    (sortedMap.get(listTagId.get(i + 1)) != sortedMap.get(listTagId.get(i)))) {
                if (i >= 2) break;
                else points = points - 2;
            }
        }
        userRepository.save(userFollowerFromBase);
    }
}
