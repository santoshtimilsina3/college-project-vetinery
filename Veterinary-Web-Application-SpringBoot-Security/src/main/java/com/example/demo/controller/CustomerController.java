package com.example.demo.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import com.example.demo.dto.response.ApiResponse;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.enums.Citys;
import com.example.demo.model.Customer;
import com.example.demo.repository.PetRepository;
import com.example.demo.service.Imp.CustomerServiceImp;
import com.example.demo.util.ApiPaths;

@Controller
@RequestMapping(ApiPaths.CustomerBasicCtrl.CTRL)
public class CustomerController {

    @Autowired PetRepository petRepository;
    @Autowired CustomerServiceImp customerServiceImp;
    @Autowired PasswordEncoder passwordEncoder;
    @Autowired UserRepository userRepository;
    @Autowired private RoleRepository roleRepository;

    @ResponseBody
    @GetMapping()
    public String hello() {

        return "hello customer";
    }

    @RequestMapping(value = "/customers", method = RequestMethod.GET)
    public String getAllCustomers(Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());
        List<Customer> customers = customerServiceImp.findAll();
        map.put("title", "Customers");
        map.put("adminname", auth.getName());
        map.put("customers", customers);
        return "customer/customers";
    }

    // http://localhost:8182/customer/customers-page?page=1&size=3
    @ResponseBody
    @RequestMapping(value = "/customers-page", method = RequestMethod.GET)
    public String getAllCustomersPage(
            Map<String, Object> map,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        Page<Customer> customers =
                customerServiceImp.GetAllPagination(PageRequest.of(currentPage - 1, pageSize));
        map.put("title", "Customers");
        map.put("adminname", auth.getName());
        map.put("customers", customers);
        customers
                .getContent()
                .forEach(
                        aa -> {
                            System.out.println(aa.getCustomerid() + " " + aa.getEmail());
                        });
        return "currentPage : " + currentPage + " <br>" + "pageSize : " + pageSize + " <br>";
    }

    @RequestMapping(value = "/get-customers", method = RequestMethod.GET)
    public String getAnyCustomers(@RequestParam("name") String name, Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("title", "Customers");
        map.put("adminname", auth.getName());
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        map.put("adminname", auth.getName());
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());
        List<Customer> customers = null;
        customers = customerServiceImp.findByFirstname(name);

        // control whether there is any customer having this firstname called name
        if (customers.size() > 0) {
            map.put("customers", customers);
        } else {
            // control whether there is any customer having this lastname called name
            customers = customerServiceImp.findByLastname(name);
            if (customers.size() > 0) {
                map.put("customers", customers);
            } else {
                customers = customerServiceImp.findAll();
                map.put("customers", customers);
                map.put("message", " No records found.");
            }
        }
        return "customer/customers";
    }

    @RequestMapping(value = "/insert-customer", method = RequestMethod.GET)
    public String CustomerRegisterPanel(Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("title", "Customer Addition Section");
        map.put("adminname", auth.getName());
        map.put("customer", new Customer());
        map.put("adminname", auth.getName());
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());
        map.put("citys", new ArrayList<Citys>(Arrays.asList(Citys.values())));
        return "customer/customer-insert-panel";
    }

    @RequestMapping(value = "/insert-customer", method = RequestMethod.POST)
    public String saveRegisterPage(
            @Valid @ModelAttribute("customer") Customer customer,
            BindingResult result,
            Model model,
            Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("customer", new Customer());
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        map.put("title", "Customer Addition Section");
        if (result.hasErrors()) {
            return "customer-insert-panel";
        } else {
            Boolean control = customerServiceImp.save(customer);

            if (control == false) {
                map.put("message", "An Issue Occurred.");
            } else {
                User newUser = new User();
                newUser.setEmail(customer.getEmail());
                newUser.setFirstname(customer.getFirstname());
                newUser.setLastname(customer.getLastname());
                newUser.setCity(customer.getCity());
                newUser.setUsername("admin");
                newUser.setPassword(passwordEncoder.encode("admin"));
                newUser.setReel_password("admin");
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findByRole("ADMIN"));
                newUser.setRoles(roles);
                userRepository.save(newUser);
                map.put("message", "Registration process successful");
            }
        }

        return "customer/customer-insert-panel";
    }

    @RequestMapping(value = "/show-customer/{customerid}", method = RequestMethod.GET)
    public String CustomerShowPanel(@PathVariable int customerid, Map<String, Object> map)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        Optional<Customer> customer = customerServiceImp.findById(customerid);
        if (customer.isPresent()) {
            map.put("title", "Customer Details");
            map.put("customer", customer.get());

            return "customer/show-customer";
        } else {
            List<Customer> customers = customerServiceImp.findAll();
            map.put("title", "Customers");
            map.put("customers", customers);
            map.put("message", " No records found.");
            return "customer/customers";
        }
    }

    @RequestMapping(value = "/show-customer", method = RequestMethod.GET)
    public String getSpecificCustomer(Map<String, Object> map) throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        String email = auth.getName();

        Optional<Customer> customer = customerServiceImp.findByEmail(email);
        if (customer.isPresent()) {
            map.put("title", "Customer Details");
            map.put("customer", customer.get());

            return "customer/show-customer";
        } else {
            List<Customer> customers = customerServiceImp.findAll();
            map.put("title", "Customers");
            map.put("customers", customers);
            map.put("message", " No records found.");
            return "admin-panel";
        }
    }

    @RequestMapping(value = "/update-customer/{customerid}", method = RequestMethod.GET)
    public String CustomerUpdatePanel(@PathVariable int customerid, Map<String, Object> map)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        Optional<Customer> customer = customerServiceImp.findById(customerid);
        if (customer.isPresent()) {
            map.put("title", "Customer Update Section");
            map.put("customer", customer.get());
            map.put("citys", new ArrayList<Citys>(Arrays.asList(Citys.values())));
            return "customer/customer-update-panel";
        } else {
            List<Customer> customers = customerServiceImp.findAll();
            map.put("title", "Customers");
            map.put("customers", customers);
            map.put("message", " No records found.");
            return "customer/customers";
        }
    }

    @RequestMapping(value = "/update-customer", method = RequestMethod.POST)
    public String CustomerUpdate(
            @Valid @ModelAttribute("customer") Customer customer,
            BindingResult result,
            Map<String, Object> map)
            throws SQLException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        map.put("customer", new Customer());
        if (result.hasErrors()) {
            map.put("title", "Add Customer");
            return "customer-insert-panel";
        } else {
            Boolean control = customerServiceImp.save(customer);
            if (control == false) {
                map.put("message", "An Issue Occurred.");
            } else {
                map.put("message", " Record update process is successful.");
            }
        }
        List<Customer> customers = customerServiceImp.findAll();
        map.put("title", "Customers");
        map.put("adminname", auth.getName());
        map.put("customers", customers);
        return "customer/customers";
    }

    @RequestMapping(value = "/delete-customer/{customerid}", method = RequestMethod.GET)
    public String CustomerDelete(@PathVariable int customerid, Map<String, Object> map)
            throws SQLException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("userRole", auth.getAuthorities().stream().findFirst().get().getAuthority());

        map.put("adminname", auth.getName());
        Optional<Customer> customer = customerServiceImp.findById(customerid);
        if (customer.isPresent()) {
            Boolean control = customerServiceImp.delete(customer.get());
            if (control == true) {
                map.put("message", "Record has been deleted.");
            } else {
                map.put("message", " Customer Update Section");
            }
        } else {
            map.put("message", " No records found.");
        }
        List<Customer> customers = customerServiceImp.findAll();
        map.put("title", "Customers");
        map.put("customers", customers);
        return "customer/customers";
    }

    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<ApiResponse> getCustomer() {
        return customerServiceImp.getCustomers();
    }
}
