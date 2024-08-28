package com.example.demo.repository;

import com.example.demo.model.AdoptionMatch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdoptionMatchRepository extends JpaRepository<AdoptionMatch,Long> {
}
