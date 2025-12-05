package com.skillswap.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "listings")
public class Listing {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String skillOffered;
    private String skillNeeded;
    private String contact;

    @Column(name = "created_at")
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Transient
    private String ownerUsername;

    public Listing() {
        this.createdAt = Instant.now();
    }

    // getters & setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String t) { this.title = t; }

    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }

    public String getSkillOffered() { return skillOffered; }
    public void setSkillOffered(String s) { this.skillOffered = s; }

    public String getSkillNeeded() { return skillNeeded; }
    public void setSkillNeeded(String s) { this.skillNeeded = s; }

    public String getContact() { return contact; }
    public void setContact(String c) { this.contact = c; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public String getOwnerUsername() { 
        return ownerUsername != null ? ownerUsername : (owner != null ? owner.getUsername() : null); 
    }
    public void setOwnerUsername(String ownerUsername) { 
        this.ownerUsername = ownerUsername; 
    }
}
