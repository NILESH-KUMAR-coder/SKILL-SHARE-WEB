package com.skillswap.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;   // ✅ You were missing this import

import org.springframework.stereotype.Service;

import com.skillswap.model.ContactRequest;
import com.skillswap.repository.ContactRequestRepository;

@Service
public class ContactRequestService {

    private final ContactRequestRepository repo;

    public ContactRequestService(ContactRequestRepository repo) {
        this.repo = repo;
    }

    public ContactRequest sendRequest(String sender, String receiver, Long listingId) {

        if (repo.existsBySenderUsernameAndReceiverUsernameAndListingId(sender, receiver, listingId)) {
            throw new RuntimeException("Request already sent");
        }

        ContactRequest r = new ContactRequest();
        r.setSenderUsername(sender);
        r.setReceiverUsername(receiver);
        r.setListingId(listingId);
        r.setStatus("pending");

        return repo.save(r);
    }

    public List<ContactRequest> incomingRequests(String username) {
        return repo.findByReceiverUsernameAndStatus(username, "pending");
    }

    public List<ContactRequest> findBySenderUsername(String username) {
        return repo.findBySenderUsername(username);
    }

    public List<ContactRequest> findByReceiverUsername(String username) {
        return repo.findByReceiverUsername(username);
    }

    public ContactRequest accept(Long requestId) {
        ContactRequest r = repo.findById(requestId).orElseThrow();
        r.setStatus("accepted");
        return repo.save(r);
    }

    public ContactRequest decline(Long requestId) {
        ContactRequest r = repo.findById(requestId).orElseThrow();
        r.setStatus("declined");
        return repo.save(r);
    }

    // ✅ Used by the controller
    public List<String> getAcceptedUsers(String me) {
        List<ContactRequest> accepted = repo.findAcceptedForUser(me);

        Set<String> users = new HashSet<>();

        for (ContactRequest r : accepted) {
            if (r.getSenderUsername().equals(me)) {
                users.add(r.getReceiverUsername());
            } else {
                users.add(r.getSenderUsername());
            }
        }

        return new ArrayList<>(users);
    }
}
