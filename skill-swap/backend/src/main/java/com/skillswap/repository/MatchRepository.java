package com.skillswap.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillswap.model.Match;

public interface MatchRepository extends JpaRepository<Match, Long> {
}
