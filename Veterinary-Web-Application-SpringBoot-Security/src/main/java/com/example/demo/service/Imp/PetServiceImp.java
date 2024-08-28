package com.example.demo.service.Imp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.demo.util.AdoptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.model.Customer;
import com.example.demo.model.Pet;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PetRepository;
import com.example.demo.service.CustomerService;
import com.example.demo.service.PetService;

@Service
public class PetServiceImp implements PetService {

    public final CustomerRepository customerRepository;
    public final PetRepository petRepository;

    public PetServiceImp(CustomerRepository customerRepository, PetRepository petRepository) {
        this.customerRepository = customerRepository;
        this.petRepository = petRepository;
    }

    public Page<Customer> GetAllPagination(Pageable pageable) {
        int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;

        Page<Customer> page =
                customerRepository.findAll(
                        PageRequest.of(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                Sort.by(Sort.Direction.ASC, "customerid")));
        return page;
    }

    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    public List<Pet> findByCustomer(Customer customer) {
        return petRepository.findByCustomer(customer);
    }

    public Boolean save(Pet pet) {
        try {
            petRepository.save(pet);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Optional<Pet> findById(Long id) {
        return petRepository.findById(id);
    }

    public Boolean delete(Pet entity) {
        try {
            entity.setCustomer(null);
            petRepository.delete(entity);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void updateAdoptionStatus(Long petId, AdoptionStatus newStatus) {
        Optional<Pet> petOptional = petRepository.findById(petId);
        if (petOptional.isPresent()) {
            Pet pet = petOptional.get();
            pet.setAdoptionStatus(newStatus);
            petRepository.save(pet);
        } else {
            throw new RuntimeException("Pet not found with id: " + petId);
        }
    }

    public List<Pet> searchPets(String query, String ageRange, String size) {
        List<Pet> pets = petRepository.findAll();

        // Filter pets based on age and size
        List<Pet> filteredPets =
                pets.stream()
                        .filter(pet -> matchesAge(pet, ageRange) && matchesSize(pet, size))
                        .collect(Collectors.toList());

        // If there's no search query, return the filtered results directly
        if (query == null || query.isEmpty()) {
            return filteredPets;
        }

        // Calculate TF-IDF scores
        Map<Pet, Double> petScores = new HashMap<>();
        String[] queryTerms = query.toLowerCase().split("\\s+");

        for (Pet pet : filteredPets) {
            String petData =
                    (pet.getName() + " " + pet.getBreed() + " " + pet.getType()).toLowerCase();
            double score = calculateTfIdfScore(petData, queryTerms, filteredPets);
            petScores.put(pet, score);
        }

        // Sort pets by TF-IDF score
        return petScores.entrySet().stream()
                .sorted((e1, e2) -> Double.compare(e2.getValue(), e1.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean matchesAge(Pet pet, String ageRange) {
        // Implement the logic for filtering based on age range
        return ageRange.equals("all")
                || (ageRange.equals("puppy") && pet.getAge() < 1)
                || (ageRange.equals("adult") && pet.getAge() >= 1 && pet.getAge() <= 7)
                || (ageRange.equals("senior") && pet.getAge() > 7);
    }

    private boolean matchesSize(Pet pet, String size) {
        // Implement the logic for filtering based on size
        return size.equals("all") || pet.getSize().equalsIgnoreCase(size);
    }

    private double calculateTfIdfScore(String petData, String[] queryTerms, List<Pet> pets) {
        double score = 0.0;

        for (String term : queryTerms) {
            double tf = calculateTermFrequency(term, petData);
            double idf = calculateInverseDocumentFrequency(term, pets);
            score += tf * idf;
        }

        return score;
    }

    private double calculateTermFrequency(String term, String text) {
        // Calculate the frequency of the term in the text
        String[] words = text.split("\\s+");
        long count = Arrays.stream(words).filter(word -> word.equals(term)).count();
        return (double) count / words.length;
    }

    private double calculateInverseDocumentFrequency(String term, List<Pet> pets) {
        // Calculate how many pets contain the term
        long count =
                pets.stream()
                        .filter(
                                pet ->
                                        (pet.getName() + " " + pet.getBreed() + " " + pet.getType())
                                                .toLowerCase()
                                                .contains(term))
                        .count();
        return Math.log((double) pets.size() / (1 + count));
    }
}
