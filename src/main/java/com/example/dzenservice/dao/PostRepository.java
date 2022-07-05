package com.example.dzenservice.dao;

import com.example.dzenservice.entity.Post;
import com.example.dzenservice.entity.Tag;
import com.example.dzenservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(long userId);

    @Query(value = "select id from post where user_id = :userId", nativeQuery = true)
    List<Long> getListPostIdByUserId(long userId);

    @Query(value = "select tag_list_id from post_tag_list where post_id = :postId", nativeQuery = true)
    List<Long> getTagIdByPostId(long postId);

    @Query(value = "select id from post where user_id = :userId", nativeQuery = true)
    List<Long> getIdPostListByUserId(long userId);

    @Query(value = "select date_time from post where id = :postId", nativeQuery = true)
    Timestamp getTimeFromPost(long postId);

    @Query(value = "select zone from post where id = :postId", nativeQuery = true)
    String getZoneFromPost(long postId);

    @Query(value = "select post_id from post_tag_list where tag_list_id = :tagId", nativeQuery = true)
    List<Long> getPostIdByTagId(long tagId);

    @Query(value = "select id from post where date_time between :time1 AND :time2", nativeQuery = true)
    List<Long> getPostIdFromPeriod(Timestamp time1, Timestamp time2);

}
