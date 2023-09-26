package com.gameloft.profilematcher.repository;

import com.gameloft.profilematcher.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByPlayerId(String playerId);

}
