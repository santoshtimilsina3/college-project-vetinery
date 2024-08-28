//package com.example.demo.service.Imp;
//
//import com.example.demo.model.AdoptionListing;
//import com.example.demo.model.AdoptionMatch;
//import com.example.demo.model.Pet;
//import com.example.demo.model.User;
//import com.example.demo.model.UserPreferences;
//import com.example.demo.repository.AdoptionListingRepository;
//import com.example.demo.repository.AdoptionMatchRepository;
//import com.example.demo.repository.UserPreferencesRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class AdoptionMatchingService {
//
//    @Autowired
//    private AdoptionListingRepository adoptionListingRepository;
//
//    @Autowired
//    private UserPreferencesRepository userPreferencesRepository;
//
//    @Autowired
//    private AdoptionMatchRepository adoptionMatchRepository;
//
//    public void generateMatchesForUser(User user) {
//        UserPreferences preferences = userPreferencesRepository.findByUserId(user.getUserId());
//        List<AdoptionListing> availableListings = adoptionListingRepository.findAllByStatus(AdoptionStatus.AVAILABLE);
//
//        for (AdoptionListing listing : availableListings) {
//            double matchScore = calculateMatchScore(preferences, listing.getPet());
//
//            if (matchScore > 0.7) {  // Arbitrary threshold for a good match
//                AdoptionMatch match = new AdoptionMatch();
//                match.setListing(listing);
//                match.setUser(user);
//                match.setMatchScore(matchScore);
//                match.setMatchDate(LocalDateTime.now());
//
//                adoptionMatchRepository.save(match);
//            }
//        }
//    }
//
//    private double calculateMatchScore(UserPreferences preferences, Pet pet) {
//        double score = 0.0;
//
//        if (preferences.getPreferredAnimalType() == pet.getType()) {
//            score += 0.4;  // Weighted score for animal type
//        }
//
//        if (preferences.getPreferredAge() != null && preferences.getPreferredAge().equals(pet.getAge())) {
//            score += 0.3;  // Weighted score for age
//        }
//
//        if (preferences.getPreferredGender() != null && preferences.getPreferredGender().equalsIgnoreCase(pet.getGender())) {
//            score += 0.3;  // Weighted score for gender
//        }
//
//        // Add more conditions based on other preference fields
//
//        return score;  // Returns a score between 0 and 1
//    }
//}
