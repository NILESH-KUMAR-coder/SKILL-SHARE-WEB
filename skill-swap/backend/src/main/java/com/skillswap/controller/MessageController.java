package com.skillswap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.skillswap.model.Message;
import com.skillswap.service.MessageService;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService msgService;

    public MessageController(MessageService msgService) {
        this.msgService = msgService;
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody Message msg, Authentication auth) {
        String username = auth.getName();
        msg.setFromUsername(username);
        Message saved = msgService.sendMessage(msg);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/conversation")
    public ResponseEntity<?> conversation(@RequestParam String with, Authentication auth) {
        String me = auth.getName();
        List<Message> conv = msgService.getConversation(me, with);
        return ResponseEntity.ok(conv);
    }

    // REQUIRED for frontend compatibility
    @GetMapping("/{otherUser}")
    public ResponseEntity<?> conversation2(@PathVariable("otherUser") String otherUser, Authentication auth) {
        String me = auth.getName();
        return ResponseEntity.ok(msgService.getConversation(me, otherUser));
    }

    @GetMapping("/recent")
    public ResponseEntity<?> recent(Authentication auth) {
        String me = auth.getName();
        return ResponseEntity.ok(msgService.findRecentForUser(me));
    }
}
