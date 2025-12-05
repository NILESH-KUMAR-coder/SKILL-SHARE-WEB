package com.skillswap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillswap.model.Listing;
import com.skillswap.model.User;

public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByOwner(User owner);
    List<Listing> findBySkillOfferedContainingIgnoreCase(String skill);
    List<Listing> findBySkillNeededContainingIgnoreCase(String skill);
}
