package com.skillswap.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.skillswap.model.Listing;
import com.skillswap.model.User;
import com.skillswap.repository.ListingRepository;
import com.skillswap.repository.UserRepository;

@Service
public class ListingService {

    private final ListingRepository listingRepo;
    private final UserRepository userRepo;

    public ListingService(ListingRepository listingRepo, UserRepository userRepo) {
        this.listingRepo = listingRepo;
        this.userRepo = userRepo;
    }

    public Listing createListing(Listing listing) {
        return listingRepo.save(listing);
    }

    public Listing updateListing(Long id, Listing updated, String username) {
        Listing existing = listingRepo.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (existing.getOwner() == null || !existing.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }
        existing.setTitle(updated.getTitle() != null ? updated.getTitle() : existing.getTitle());
        existing.setDescription(updated.getDescription() != null ? updated.getDescription() : existing.getDescription());
        existing.setSkillOffered(updated.getSkillOffered() != null ? updated.getSkillOffered() : existing.getSkillOffered());
        existing.setSkillNeeded(updated.getSkillNeeded() != null ? updated.getSkillNeeded() : existing.getSkillNeeded());
        existing.setContact(updated.getContact() != null ? updated.getContact() : existing.getContact());
        return listingRepo.save(existing);
    }

    public void deleteListing(Long id, String username) {
        Listing existing = listingRepo.findById(id).orElseThrow(() -> new RuntimeException("Listing not found"));
        if (existing.getOwner() == null || !existing.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Not authorized");
        }
        listingRepo.delete(existing);
    }

    public List<Listing> getAllListings() {
        return listingRepo.findAll();
    }

    public List<Listing> getByOwner(String username) {
        User owner = userRepo.findByUsername(username).orElse(null);
        if (owner == null) return List.of();
        return listingRepo.findByOwner(owner);
    }

    public List<Listing> matchBySkill(String skill, String currentUsername) {
        List<Listing> offered = listingRepo.findBySkillOfferedContainingIgnoreCase(skill);
        List<Listing> needed = listingRepo.findBySkillNeededContainingIgnoreCase(skill);

        Set<Listing> combined = new HashSet<>();
        combined.addAll(offered);
        combined.addAll(needed);

        return combined.stream()
                .filter(l -> l.getOwner() != null && !l.getOwner().getUsername().equals(currentUsername))
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
