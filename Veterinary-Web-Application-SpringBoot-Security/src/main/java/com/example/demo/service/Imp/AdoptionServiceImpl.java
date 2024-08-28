package com.example.demo.service.Imp;

import com.example.demo.model.AdoptionListing;
import com.example.demo.service.AdoptionService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AdoptionServiceImpl implements AdoptionService {
    @Override
    public List<AdoptionListing> getAvailableListings() {
        return null;
    }

    @Override
    public AdoptionListing getListingById(Long id) {
        return null;
    }

    @Override
    public void expressInterest(Long listingId, String message) {

    }
}
