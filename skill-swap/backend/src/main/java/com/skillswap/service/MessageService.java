package com.skillswap.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.skillswap.model.Message;
import com.skillswap.repository.MessageRepository;
import com.skillswap.repository.ContactRequestRepository;

@Service
public class MessageService {

    private final MessageRepository msgRepo;
    private final ContactRequestRepository contactRepo;

    // ← THIS CONSTRUCTOR MUST BE EXACTLY LIKE THIS
    public MessageService(
            MessageRepository msgRepo,
            ContactRequestRepository contactRepo
    ) {
        this.msgRepo = msgRepo;          // ← THIS WAS MISSING / BROKEN
        this.contactRepo = contactRepo;  // ← AND THIS
    }

    public Message sendMessage(Message m) {
        String sender = m.getFromUsername();
        String receiver = m.getToUsername();

        boolean allowed =
                contactRepo.existsBySenderUsernameAndReceiverUsernameAndStatus(sender, receiver, "accepted")
                || contactRepo.existsBySenderUsernameAndReceiverUsernameAndStatus(receiver, sender, "accepted");

        if (!allowed) {
            throw new RuntimeException("Chat not allowed. Contact request not accepted.");
        }

        return msgRepo.save(m);
    }

    public List<Message> getConversation(String userA, String userB) {
        List<Message> aToB =
                msgRepo.findByFromUsernameAndToUsernameOrderByCreatedAtAsc(userA, userB);

        List<Message> bToA =
                msgRepo.findByFromUsernameAndToUsernameOrderByCreatedAtAsc(userB, userA);

        List<Message> combined = new ArrayList<>();
        combined.addAll(aToB);
        combined.addAll(bToA);
        combined.sort((x, y) -> x.getCreatedAt().compareTo(y.getCreatedAt()));
        return combined;
    }

    public List<Message> findRecentForUser(String username) {
        return msgRepo.findByFromUsernameOrToUsernameOrderByCreatedAtDesc(username, username);
    }
}
