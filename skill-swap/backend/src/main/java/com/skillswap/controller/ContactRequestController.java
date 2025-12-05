package com.skillswap.controller;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.skillswap.model.ContactRequest;
import com.skillswap.service.ContactRequestService;

@RestController
@RequestMapping("/api/contact")
public class ContactRequestController {

    private final ContactRequestService service;

    public ContactRequestController(ContactRequestService service) {
        this.service = service;
    }

    @PostMapping("/send/{receiver}/{listingId}")
    public ResponseEntity<?> send(
            @PathVariable String receiver,
            @PathVariable Long listingId,
            Authentication auth) {

        String sender = auth.getName();
        ContactRequest req = service.sendRequest(sender, receiver, listingId);

        return ResponseEntity.ok(Map.of("status", "sent", "id", req.getId()));
    }

    // ✅ FIXED /allowed endpoint
    @GetMapping("/allowed")
    public ResponseEntity<?> allowed(Authentication auth) {

        String me = auth.getName(); // username from JWT

        // requests I sent
        List<ContactRequest> sentAccepted = service
                .findBySenderUsername(me)
                .stream()
                .filter(r -> "accepted".equals(r.getStatus()))
                .toList();

        // requests I received
        List<ContactRequest> receivedAccepted = service
                .findByReceiverUsername(me)
                .stream()
                .filter(r -> "accepted".equals(r.getStatus()))
                .toList();

        List<String> contactUsernames = new ArrayList<>();

        // users I sent to
        sentAccepted.forEach(r -> contactUsernames.add(r.getReceiverUsername()));

        // users who sent to me
        receivedAccepted.forEach(r -> contactUsernames.add(r.getSenderUsername()));

        return ResponseEntity.ok(contactUsernames);
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<ContactRequest>> incoming(Authentication auth) {
        return ResponseEntity.ok(service.incomingRequests(auth.getName()));
    }

    @PostMapping("/accept/{id}")
    public ResponseEntity<?> accept(@PathVariable Long id) {
        service.accept(id);
        return ResponseEntity.ok(Map.of("status", "accepted"));
    }

    @PostMapping("/decline/{id}")
    public ResponseEntity<?> decline(@PathVariable Long id) {
        service.decline(id);
        return ResponseEntity.ok(Map.of("status", "declined"));
    }
}
