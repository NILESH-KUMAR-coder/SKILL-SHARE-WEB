package com.skillswap.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.skillswap.model.Listing;
import com.skillswap.service.ListingService;

@RestController
@RequestMapping("/api/match")
public class MatchController {

    private final ListingService listingService;

    public MatchController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public List<Listing> match(@RequestParam String skill, Authentication auth) {
        String currentUsername = auth != null ? auth.getName() : null;
        return listingService.matchBySkill(skill, currentUsername);
    }
}
