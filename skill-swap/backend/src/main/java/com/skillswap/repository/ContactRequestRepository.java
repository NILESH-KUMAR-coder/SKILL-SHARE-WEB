package com.skillswap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.skillswap.model.ContactRequest;

public interface ContactRequestRepository extends JpaRepository<ContactRequest, Long> {

    List<ContactRequest> findByReceiverUsernameAndStatus(String receiver, String status);

    List<ContactRequest> findByReceiverUsername(String receiverUsername);

    List<ContactRequest> findBySenderUsername(String senderUsername);

    // ✅ Fixed JPQL query — valid and working
    @Query("SELECT c FROM ContactRequest c " +
           "WHERE (c.senderUsername = :u OR c.receiverUsername = :u) " +
           "AND c.status = 'accepted'")
    List<ContactRequest> findAcceptedForUser(@Param("u") String username);

    boolean existsBySenderUsernameAndReceiverUsernameAndListingId(
            String sender, String receiver, Long listingId);

    boolean existsBySenderUsernameAndReceiverUsernameAndStatus(
            String senderUsername,
            String receiverUsername,
            String status);
}
