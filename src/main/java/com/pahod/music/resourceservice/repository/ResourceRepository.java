package com.pahod.music.resourceservice.repository;

import com.pahod.music.resourceservice.entity.AudioResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<AudioResourceEntity, Integer> {}
