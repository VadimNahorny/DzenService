package com.example.dzenservice.service;

import com.example.dzenservice.dao.PostRepository;
import com.example.dzenservice.dao.UserRepository;
import com.example.dzenservice.entity.Follower;
import com.example.dzenservice.entity.Post;
import com.example.dzenservice.entity.Tag;
import com.example.dzenservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class PostService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;


    private List<Long> getIdPostListAllFollowing(User user) {
        List<Follower> followingList = user.getFollowing();
        List<Long> postIdList = new ArrayList<>();
        for (Follower follower : followingList)
            postIdList.addAll(postRepository.getIdPostListByUserId(follower.getId()));
        return postIdList;
    }

    private Timestamp convertTimeToTimestamp(ZonedDateTime zoneDateTime) {
        zoneDateTime = zoneDateTime.withZoneSameInstant(ZoneId.of("UCT"));
        LocalDateTime withoutTimezone = zoneDateTime.toLocalDateTime();
        return Timestamp.valueOf(withoutTimezone);

    }

    private ZonedDateTime convertToCurrentZoneDateTime(Timestamp sqlTimestamp, String zone) {
        LocalDateTime withoutTimezone = sqlTimestamp.toLocalDateTime();
        return withoutTimezone.atZone(ZoneId.of("UCT")).withZoneSameInstant(ZoneId.of(zone));
    }

    private ZonedDateTime convertToUctZoneDateTime(Timestamp sqlTimestamp) {
        LocalDateTime withoutTimezone = sqlTimestamp.toLocalDateTime();
        return withoutTimezone.atZone(ZoneId.of("UCT"));
    }


    private List<Post> getPostListWithTime(List<Long> postIdList) {

        List<Post> postList = new ArrayList<>();
        for (Long postId : postIdList) {
            Post post = new Post();
            post.setId(postId);
            post.setDateTime(convertToUctZoneDateTime(postRepository.getTimeFromPost(postId)));
            postList.add(post);
        }
        return postList;
    }

    private List<Post> getPostListForDay(List<Post> postListAllFollowing) {
        List<Post> postListForDay = new ArrayList<>();
        ZonedDateTime zonedDateTime = ZonedDateTime.now().minusDays(1);
        for (Post post : postListAllFollowing) {
            if (post.getDateTime().isAfter(zonedDateTime)) postListForDay.add(post);
        }
        return postListForDay;
    }

    private List<Post> getPostListForMonths(List<Post> postListAllFollowing) {
        List<Post> postListForMonths = new ArrayList<>();
        ZonedDateTime zonedDateTimeMonths = ZonedDateTime.now().minusMonths(1);
        ZonedDateTime zonedDateTimeDay = ZonedDateTime.now().minusDays(1);
        for (Post post : postListAllFollowing) {
            if (post.getDateTime().isAfter(zonedDateTimeMonths) &&
                    post.getDateTime().isBefore(zonedDateTimeDay)) postListForMonths.add(post);
        }
        return postListForMonths;
    }

    private int getCountFollowingsPost(int countFollowing) {
        if (countFollowing < 6) return 4;
        if (countFollowing < 11) return 6;
        if (countFollowing < 16) return 8;
        return 10;
    }

    private int[] getCountPostBasedOnTag(List<Long> listTagId, long id) {
        if (listTagId.indexOf(id) == 0) return new int[]{5, 4};
        if (listTagId.indexOf(id) == 1) return new int[]{4, 3};
        if (listTagId.indexOf(id) == 2) return new int[]{3, 2};
        if (listTagId.indexOf(id) == 4) return new int[]{3, 1};
        return new int[]{2, 1};
    }

    private List<Long> getIdTagListFromTagList(List<Tag> tagList) {
        List<Long> idTagList = new ArrayList<>();
        for (Tag tag : tagList) idTagList.add(tag.getId());
        return idTagList;
    }

    private List<Post> getFollowingsPostsForRecentlyEnteredUser(int countFollowingsPost,
                                                                List<Post> postListAllFollowing) {
        List<Post> postList = new ArrayList<>();
        List<Post> postListForDay = getPostListForDay(postListAllFollowing);
        List<Post> postListForMonths = getPostListForMonths(postListAllFollowing);
        SecureRandom random = new SecureRandom();
        Post post = null;
        int addedId = 0;
        int notAddedId = 0;
        for (int i = 1; i <= countFollowingsPost; ) {
            if (i == (countFollowingsPost / 2 + 1)) {
                addedId = 0;
                notAddedId = 0;
            }
            if ((i <= countFollowingsPost / 2) && !postListForDay.isEmpty())
                post = postListForDay.get(random.nextInt(postListForDay.size()));
            else if ((i > countFollowingsPost / 2) && !postListForMonths.isEmpty())
                post = postListForMonths.get(random.nextInt(postListForMonths.size()));
            if (!postList.contains(post)) {
                postList.add(post);
                addedId++;
                i++;
            } else notAddedId++;
            if ((i <= (countFollowingsPost / 2)) && ((notAddedId + addedId) == postListForDay.size()))
                i = countFollowingsPost / 2 + 1;
            else if ((i > countFollowingsPost / 2) && ((notAddedId + addedId) == postListForMonths.size()))
                break;
        }
        return postList;
    }

    private Map<Tag, Long> sortMapByValue(Map<Tag, Long> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));
    }

    private Map<Long, Long> sortMapByValueLongLong(Map<Long, Long> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors
                        .toMap(Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e1,
                                LinkedHashMap::new));
    }

    private Map<Long, List<Long>> getTagPostIdForLastPeriod(List<Tag> listTag) {
        Map<Long, List<Long>> tagIdPostMap = new LinkedHashMap<>();
        for (Tag tag : listTag) {
            tagIdPostMap.put(tag.getId(),
                    postRepository.getPostIdByTagId(tag.getId()));
        }
        Map<Long, List<Long>> tagPostIdMapForLastPeriod = new LinkedHashMap<>();
        for (Tag tag : listTag) {
            List<Long> postIdList = tagIdPostMap.get(tag.getId());
            for (Long postId : postIdList) {
                ZonedDateTime zonedDateTimeFromBase = convertToUctZoneDateTime
                        (postRepository.getTimeFromPost(postId));
                ZonedDateTime zonedDateTimePeriodAgo = ZonedDateTime.now().
                        withZoneSameInstant(ZoneId.of("UCT")).minusDays(14);
                if (zonedDateTimeFromBase.isAfter(zonedDateTimePeriodAgo)) {
                    if (!tagPostIdMapForLastPeriod.containsKey(tag.getId()))
                        tagPostIdMapForLastPeriod.put(tag.getId(), new ArrayList<>(List.of(postId)));
                    else {
                        List<Long> list = tagPostIdMapForLastPeriod.get(tag.getId());
                        list.add(postId);
                        tagPostIdMapForLastPeriod.put(tag.getId(), list);
                    }
                }
            }
        }
        return tagPostIdMapForLastPeriod;
    }

    private List<Tag> mainTag(List<Tag> listTag) {
        if (listTag.size() > 5) listTag = listTag.subList(0, 5);
        return listTag;
    }

    private Map<Long, Long> getEmptyMapPostPoints(List<Long> postIdList) {
        Map<Long, Long> mapPostPoints = new LinkedHashMap<>();
        for (Long postId : postIdList) mapPostPoints.put(postId, 0L);
        return mapPostPoints;
    }

    private Map<Long, Map<Long, Long>> getMapInMapWithPostPoints
            (Map<Long, List<Long>> mainTagPostIdMap, List<Tag> listMainTag,
             List<Tag> listTag) {
        Map<Long, Map<Long, Long>> mapInMapWithPostPoints = new LinkedHashMap<>();

        for (Tag tagFromMainList : listMainTag) {
            if (mainTagPostIdMap.containsKey(tagFromMainList.getId())) {
                List<Long> postIdList = mainTagPostIdMap.get(tagFromMainList.getId());
                mapInMapWithPostPoints.put(tagFromMainList.getId(), getEmptyMapPostPoints(postIdList));
                for (Long postId : postIdList) {
                    List<Long> localTagIdList = postRepository.getTagIdByPostId(postId);
                    List<Long> generalTagIdList = getIdTagListFromTagList(listTag);
                    for (Long localTagId : localTagIdList)
                        if (generalTagIdList.contains(localTagId) && localTagId != tagFromMainList.getId()) {
                            Map<Long, Long> map = mapInMapWithPostPoints.get(tagFromMainList.getId());
                            map.put(postId, map.get(postId) + (generalTagIdList.size() - generalTagIdList.indexOf(localTagId)));
                            mapInMapWithPostPoints.put(tagFromMainList.getId(), map);
                        }
                }
            }
        }
        return mapInMapWithPostPoints;
    }

    private int adjustForOddNumber(int number) {
        if (number % 2 == 0) return 0;
        return 1;
    }

    private List<Long> addAllPostsFromIdPostList(List<Long> idPostList, List<Long> postIdListForClient,
                                                 List<Long> idShownPosts) {
        for (Long id : idPostList) {
            if (!postIdListForClient.contains(id) && !idShownPosts.contains(id))
                postIdListForClient.add(id);
            System.out.println("addAllPostsFromIdPostList");
        }
        return postIdListForClient;
    }

    private List<Long> getIdPostsListBasedOnPreferenceMap(User user) {
        Map<Tag, Long> preferenceMap = user.getPreferenceMap();
        Map<Tag, Long> sortedPreferenceMap = sortMapByValue(preferenceMap);
        Set<Tag> tagIdSet = sortedPreferenceMap.keySet();
        List<Tag> listTag = new ArrayList<>(tagIdSet);
        Collections.reverse(listTag);
        List<Tag> listMainTag = mainTag(listTag);
        Map<Long, List<Long>> mainTagPostIdMap = getTagPostIdForLastPeriod(listMainTag);
        Map<Long, Map<Long, Long>> mapInMapWithPostPoints = getMapInMapWithPostPoints(mainTagPostIdMap,
                listMainTag, listTag);
        List<Long> postIdListForClient = new ArrayList<>();
        List<Long> listIdMainTag = getIdTagListFromTagList(listMainTag);
        for (Long key : mapInMapWithPostPoints.keySet()) {
            Map<Long, Long> map = mapInMapWithPostPoints.get(key);
            Map<Long, Long> sortedMap = sortMapByValueLongLong(map);
            mapInMapWithPostPoints.put(key, sortedMap);
            Set<Long> idPostSet = sortedMap.keySet();
            List<Long> idPostList = new ArrayList<>(idPostSet);
            Collections.reverse(idPostList);
            System.out.println(idPostList);
            int[] countPost = getCountPostBasedOnTag(listIdMainTag, key);
            if (idPostList.size() <= (countPost[0] + countPost[1]))
                postIdListForClient = addAllPostsFromIdPostList(idPostList, postIdListForClient,
                        user.getIdShownPosts());
            else {
                postIdListForClient = formIdPostListForClient(countPost, idPostList,
                        postIdListForClient, user);
            }
        }
        Collections.shuffle(postIdListForClient);
        return postIdListForClient;
    }


    private List<Long> formIdPostListForClient(int[] countPost, List<Long> idPostList,
                                               List<Long> postIdListForClient, User user) {
        List <Long> addedId = new ArrayList<>();
        List <Long> notAddedId = new ArrayList<>();
        SecureRandom secureRandom = new SecureRandom();
        int j = 0;
        for (int i = 1; i <= (countPost[0] + countPost[1]); ) {
            if ((i == countPost[0] + 1)) {
                j++;
                if (j == 1) {
                    addedId.clear();
                    notAddedId.clear();
                }
            }
            Long idPost;
            if (i <= (countPost[0])) {
                idPost = idPostList.get(secureRandom.nextInt(idPostList.size() / 2 + adjustForOddNumber(idPostList.size())));
            } else idPost =
                    idPostList.get(secureRandom.nextInt
                            ((idPostList.size() / 2 + adjustForOddNumber(idPostList.size())), idPostList.size()));
            if (!postIdListForClient.contains(idPost) &&
                    (user.getIdShownPosts() == null || user.getIdShownPosts().isEmpty() ||
                            !user.getIdShownPosts().contains(idPost))) {
                postIdListForClient.add(idPost);
                addedId.add(idPost);
                i++;
            } else if (!notAddedId.contains(idPost)) notAddedId.add(idPost);
            if ((i <= (countPost[0])) && ((notAddedId.size() + addedId.size()) >= (idPostList.size() / 2 + adjustForOddNumber(idPostList.size()))))
                i = countPost[0] + 1;
            else if ((i > (countPost[0])) && ((notAddedId.size() + addedId.size()) >= (idPostList.size() / 2 - adjustForOddNumber(idPostList.size()))))
                break;
        }
        return postIdListForClient;
    }

    private List<Long> getRandomPostList(User user) {
        ZonedDateTime time1 = ZonedDateTime.now().minusMonths(1);
        ZonedDateTime time2 = ZonedDateTime.now();
        List<Long> postIdList = postRepository.getPostIdFromPeriod(convertTimeToTimestamp(time1),
                convertTimeToTimestamp(time2));
        List<Long> postIdListForClient = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        int addedId = 0;
        int notAddedId = 0;
        for (int i = 1; i <= 15;) {
            Long postId = postIdList.get(random.nextInt(postIdList.size()));
            if (!postIdListForClient.contains(postId) &&
                    !user.getIdShownPosts().contains(postId)) {
                postIdListForClient.add(postId);
                addedId++;
                i++;
                System.out.println("getRandomPostList");
            } else notAddedId++;
            if ((i < 15) && ((notAddedId + addedId) == postIdList.size()))
                break;
        }
        return postIdListForClient;
    }


    public List<Post> getPostsListForRecentlyEnteredUser(User user) {
        User userFromBase = userRepository.getReferenceById(user.getId());
        userFromBase.getIdShownPosts().clear();
        List<Post> postList = getPostListWithTime(getIdPostListAllFollowing(userFromBase));
        int countFollowingsPost = getCountFollowingsPost(userFromBase.getFollowing().size());
        List<Post> followingsPosts = getFollowingsPostsForRecentlyEnteredUser(countFollowingsPost,
                postList);
        List<Long> idPostListForClient = new ArrayList<>();
        for (Post post : followingsPosts) idPostListForClient.add(post.getId());
        userFromBase.getIdShownPosts().addAll(idPostListForClient);
        List<Long> idPostsListBasedOnPreferenceMap = getIdPostsListBasedOnPreferenceMap(userFromBase);
        idPostListForClient.addAll(idPostsListBasedOnPreferenceMap);
        userFromBase.getIdShownPosts().addAll(idPostsListBasedOnPreferenceMap);
        List <Long> randomIdPostList = getRandomPostList(userFromBase);
        idPostListForClient.addAll(randomIdPostList);
        userFromBase.getIdShownPosts().addAll(randomIdPostList);
        userRepository.save(userFromBase);
        List<Post> postListForClient = new ArrayList<>();
        for (Long postId : idPostListForClient) postListForClient.add(postRepository.getReferenceById(postId));
        return postListForClient;
    }

    public List<Post> getAdditionalPostList(User user) {
        User userFromBase = userRepository.getReferenceById(user.getId());
        List<Long> idPostsListBasedOnPreferenceMap = getIdPostsListBasedOnPreferenceMap(userFromBase);
        List<Long> idPostListForClient = new ArrayList<>(idPostsListBasedOnPreferenceMap);
        userFromBase.getIdShownPosts().addAll(idPostsListBasedOnPreferenceMap);
        List <Long> randomIdPostList = getRandomPostList(userFromBase);
        idPostListForClient.addAll(randomIdPostList);
        userFromBase.getIdShownPosts().addAll(randomIdPostList);
        userRepository.save(userFromBase);
        List<Post> postListForClient = new ArrayList<>();
        for (Long postId : idPostListForClient) postListForClient.add(postRepository.getReferenceById(postId));
        return postListForClient;
    }


    public void clearIdShownPosts(User user) {
        User userFromBase = userRepository.getReferenceById(user.getId());
        userFromBase.getIdShownPosts().clear();
        userRepository.save(userFromBase);
    }
}
