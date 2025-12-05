package com.skillswap.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillswap.model.Listing;
import com.skillswap.model.User;
import com.skillswap.repository.UserRepository;
import com.skillswap.service.ListingService;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;
    private final UserRepository userRepo;

    public ListingController(ListingService listingService, UserRepository userRepo) {
        this.listingService = listingService;
        this.userRepo = userRepo;
    }

    // Create
    @PostMapping("/create")
    public ResponseEntity<?> createListing(@RequestBody Listing listing, Authentication auth) {
        String username = auth.getName();
        User owner = userRepo.findByUsername(username).orElse(null);
        if (owner == null) return ResponseEntity.status(401).body("User not found");

        listing.setOwner(owner);
        Listing saved = listingService.createListing(listing);
        return ResponseEntity.ok(saved);
    }

    // Get all
    @GetMapping
    public ResponseEntity<List<Listing>> getAllListings() {
        List<Listing> list = listingService.getAllListings();
        // fill ownerUsername for convenience
        list.forEach(l -> { if (l.getOwner() != null) l.setOwnerUsername(l.getOwner().getUsername()); });
        return ResponseEntity.ok(list);
    }

    // Get all (alias /all)
    @GetMapping("/all")
    public ResponseEntity<List<Listing>> getAllAlias() {
        return getAllListings();
    }

    // Get mine
    @GetMapping("/mine")
    public ResponseEntity<List<Listing>> getMyListings(Authentication auth) {
        String username = auth.getName();
        List<Listing> list = listingService.getByOwner(username);
        list.forEach(l -> { if (l.getOwner() != null) l.setOwnerUsername(l.getOwner().getUsername()); });
        return ResponseEntity.ok(list);
    }

    // Match by skill (exclude current user)
    @GetMapping("/match")
    public ResponseEntity<List<Listing>> matchBySkill(@RequestParam String skill, Authentication auth) {
        String currentUsername = auth != null ? auth.getName() : null;
        List<Listing> matches = listingService.matchBySkill(skill, currentUsername);
        matches.forEach(l -> { if (l.getOwner() != null) l.setOwnerUsername(l.getOwner().getUsername()); });
        return ResponseEntity.ok(matches);
    }

    // Update listing (only owner)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateListing(@PathVariable Long id, @RequestBody Listing updated, Authentication auth) {
        String username = auth.getName();
        Listing saved = listingService.updateListing(id, updated, username);
        if (saved.getOwner() != null) saved.setOwnerUsername(saved.getOwner().getUsername());
        return ResponseEntity.ok(saved);
    }

    // Delete listing
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id, Authentication auth) {
        String username = auth.getName();
        listingService.deleteListing(id, username);
        return ResponseEntity.ok(java.util.Map.of("ok", true));
    }
}
