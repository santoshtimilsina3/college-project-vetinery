package com.example.demo.controller;

import com.example.demo.model.AdoptionListing;
import com.example.demo.service.AdoptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class AdoptionController {

    @Autowired private AdoptionService adoptionService;

    @GetMapping("/adoption/listings")
    public String getAdoptionListings(Model model) {
        List<AdoptionListing> listings = adoptionService.getAvailableListings();
        model.addAttribute("listings", listings);
        return "adoption-list";
    }

    @GetMapping("/adoption/details/{id}")
    public String getAdoptionDetails(@PathVariable Long id, Model model) {
        AdoptionListing listing = adoptionService.getListingById(id);
        model.addAttribute("listing", listing);
        return "adoption-details";
    }

    @PostMapping("/adoption/express-interest")
    public String expressInterest(@RequestParam Long listingId, @RequestParam String message) {
        adoptionService.expressInterest(listingId, message);
        return "redirect:/adoption/listings";
    }
}
