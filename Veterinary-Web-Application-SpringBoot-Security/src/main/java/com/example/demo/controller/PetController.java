package com.example.demo.controller;

import com.example.demo.enums.Animals;
import com.example.demo.model.Customer;
import com.example.demo.model.Pet;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.PetRepository;
import com.example.demo.service.Imp.CustomerServiceImp;
import com.example.demo.service.Imp.PetServiceImp;
import com.example.demo.util.AdoptionStatus;
import com.example.demo.util.ApiPaths;
import com.example.demo.util.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

@Controller
@RequestMapping(ApiPaths.PetBasicCtrl.CTRL)
public class PetController {
    @Autowired CustomerRepository customerRepository;
    @Autowired CustomerServiceImp customerServiceImp;
    @Autowired PetServiceImp petServiceImp;
    @Autowired PetRepository petRepository;

    @RequestMapping(value = "/show-pets/{customerid}", method = RequestMethod.GET)
    public String PetsShowPanel(@PathVariable int customerid, Map<String, Object> map)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        Optional<Customer> customer = customerServiceImp.findById(customerid);
        if (customer.isPresent()) {

            List<Pet> pets = petServiceImp.findByCustomer(customer.get());
            map.put("title", "Animals Belonging to the Customer");
            map.put("customer", customer.get());
            map.put("pet", new Pet());
            map.put("pets", pets);
            return "pet/pets";
        } else {
            List<Customer> customers = customerServiceImp.findAll();
            map.put("title", "Customers");
            map.put("customers", customers);
            map.put("message", " No records found.");
            return "customer/customers";
        }
    }

    @RequestMapping(value = "/pet-search", method = RequestMethod.GET)
    public String showPetSearchPage(Model model) {
        // Add any initial data required for the page

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("adminname", auth.getName());
        model.addAttribute("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        model.addAttribute("pets", new ArrayList<>()); // Initialize with empty list
        return "pet/pet-search"; // Returns the pet-search.html view
    }

    @RequestMapping(value = "/show-pets-by-name/{customerid}", method = RequestMethod.GET)
    public String PetsShowPanel2(
            @RequestParam("name") String name,
            @PathVariable int customerid,
            Map<String, Object> map)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        map.put("adminname", auth.getName());
        List<Pet> pets = new ArrayList<>();
        Optional<Customer> customer = customerServiceImp.findById(customerid);
        if (customer.isPresent()) {
            List<Pet> pets2 = petServiceImp.findByCustomer(customer.get());
            pets2.stream()
                    .forEach(
                            item -> {
                                if (item.getName().equals(name)) {
                                    item = item;
                                    System.out.println(
                                            item.getId()
                                                    + " : "
                                                    + item.getName()
                                                    + " "
                                                    + item.getProblem());
                                    pets.add(item);
                                }
                            });
            if (pets.size() > 0) {
                map.put("pets", pets);
                map.put("message", "Records have been found.");
            } else {
                map.put("message", name + " No animal found under the name.");
                pets2 = petServiceImp.findByCustomer(customer.get());
                map.put("pets", pets2);
            }
            map.put("title", "Animals Belonging to the Customer");
            map.put("customer", customer.get());
            return "pet/pets";
        } else {
            List<Customer> customers = customerServiceImp.findAll();
            map.put("title", "Customers");
            map.put("customers", customers);
            map.put("message", "There is no record with this name. id : " + customerid);
            return "customer/customers";
        }
    }

    @RequestMapping(value = "/insert-pet/{customerid}", method = RequestMethod.GET)
    public String InsertPetPanel(
            @PathVariable int customerid,
            Map<String, Object> map,
            @Valid @ModelAttribute("pet") Pet pet,
            BindingResult result,
            Model model)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        Optional<Customer> customer = customerServiceImp.findById(customerid);
        if (customer.isPresent()) {
            List<Pet> pets = petServiceImp.findByCustomer(customer.get());
            map.put("title", "Customer Details");
            map.put("customer", customer.get());
            map.put("pet", new Pet());
            map.put("pets", pets);
            map.put("types", (Arrays.asList(Animals.values())));
            map.put("sizes", (Arrays.asList(Size.values())));

            return "pet/pet-insert-panel";
        } else {
            List<Customer> customers = customerServiceImp.findAll();
            map.put("title", "Customers");
            map.put("customers", customers);
            map.put("message", " No records found.");
            return "customer/customers";
        }
    }

    @RequestMapping(value = "/insert-pet/{customerid}", method = RequestMethod.POST)
    public String InsertPet(
            @PathVariable int customerid,
            Map<String, Object> map,
            @Valid @ModelAttribute("pet") Pet pet,
            BindingResult result,
            Model model)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        if (!pet.getName().equals("") || !pet.getProblem().equals("")) {
            Optional<Customer> customer = customerServiceImp.findById(customerid);
            if (customer.isPresent()) {
                if (result.hasErrors()) {
                    map.put("message", "An issue has occurred.");

                } else {
                    pet.setCustomer(customer.get());
                    pet.setAdoptionStatus(AdoptionStatus.UNAVAILABLE);
                    petRepository.save(pet);
                    map.put("message", " No records found.");
                }
                List<Pet> pets = petServiceImp.findByCustomer(customer.get());
                map.put("customer", customer.get());
                map.put("pet", new Pet());
                map.put("pets", pets);
                map.put("title", "Animals Belonging to the Customer");
                return "pet/pets";
            } else {
                List<Customer> customers = customerRepository.findAll();
                map.put("title", "Customers");
                map.put("customers", customers);
                map.put("message", " No records found.");
                return "customer/customers";
            }
        } else {
            Optional<Customer> customer = customerServiceImp.findById(customerid);
            if (customer.isPresent()) {
                List<Pet> pets = petServiceImp.findByCustomer(customer.get());
                map.put("customer", customer.get());
                map.put("pet", new Pet());
                map.put("message", "Fill in the blank fields.");
                map.put("pets", pets);

                return "customer/show-customer";
            } else {
                List<Customer> customers = customerRepository.findAll();
                map.put("title", "Customers");
                map.put("customers", customers);
                map.put("message", "Fill in the blank fields.");
                return "customer/customers";
            }
        }
    }

    @RequestMapping(value = "/delete-pet/{pet_id}", method = RequestMethod.GET)
    public String DeletePet(
            @PathVariable long pet_id,
            Map<String, Object> map,
            @Valid @ModelAttribute("pet") Pet pet,
            BindingResult result,
            Model model)
            throws SQLException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        Optional<Pet> selected_pet = petServiceImp.findById(pet_id);
        if (selected_pet.isPresent()) {
            int customerid = selected_pet.get().getCustomer().getCustomerid();
            selected_pet.get().setCustomer(null);
            Boolean control = petServiceImp.delete(selected_pet.get());
            if (control == true) {
                map.put("message", "The record has been successfully deleted.");
            } else {
                map.put("message", "An error has occurred.");
            }
            Customer customer = customerRepository.findById(customerid).get();

            List<Pet> pets = petRepository.findByCustomer(customer);
            map.put("title", "Animals Belonging to the Customer");
            map.put("customer", customer);
            map.put("pet", new Pet());
            map.put("pets", pets);
            return "pet/pets";
        } else {
            List<Customer> customers = customerRepository.findAll();
            map.put("title", "Customers");
            map.put("customers", customers);
            map.put("message", "No pet records found.");
            return "customer/customers";
        }
    }
    
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchPets(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "ageRange", required = false, defaultValue = "all")
                    String ageRange,
            @RequestParam(value = "size", required = false, defaultValue = "all") String size,
            Model model) {

        List<Pet> pets = petServiceImp.searchPets(query, ageRange, size);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("pets", pets);
        model.addAttribute("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        return "pet/pet-search";
    }

    @RequestMapping(value = "pet/details/{id}", method = RequestMethod.GET)
    public String viewPetDetails(@PathVariable Long id, Model model) {
        Pet pet = petServiceImp.findById(id).get();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        model.addAttribute("pet", pet);

        return "pet/pet-details";
    }
}
