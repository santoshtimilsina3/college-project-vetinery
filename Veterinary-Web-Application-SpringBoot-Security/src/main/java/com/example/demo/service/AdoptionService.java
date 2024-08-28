package com.example.demo.service;

import com.example.demo.model.AdoptionListing;
import org.springframework.stereotype.Service;

import java.util.List;


public interface AdoptionService {
    List<AdoptionListing> getAvailableListings();

    AdoptionListing getListingById(Long id);

    void expressInterest(Long listingId, String message);
}
