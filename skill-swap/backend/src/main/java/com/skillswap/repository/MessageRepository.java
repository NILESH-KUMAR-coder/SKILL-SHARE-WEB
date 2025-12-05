package com.skillswap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillswap.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByFromUsernameAndToUsernameOrderByCreatedAtAsc(String from, String to);
    List<Message> findByToUsernameAndFromUsernameOrderByCreatedAtAsc(String to, String from);
    List<Message> findByFromUsernameOrToUsernameOrderByCreatedAtDesc(String from, String to);
    List<Message> findByFromUsernameAndToUsernameOrToUsernameAndFromUsernameOrderByCreatedAtAsc(String a, String b, String c, String d);
}
