package com.example.demo.repository;
import com.example.demo.model.UserPreferences;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Role;
public interface UserPreferencesRepository extends JpaRepository<UserPreferences,Long>{

}
