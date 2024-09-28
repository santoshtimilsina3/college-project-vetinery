package com.example.demo.controller;

import com.example.demo.dto.request.AppointmentBookDto;
import com.example.demo.model.Appointment;
import com.example.demo.service.AppointmentService;
import com.example.demo.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Controller
@RequestMapping(ApiPaths.AppointmentCtrl.CTRL)
public class AppointmentController {

    @Autowired AppointmentService appointmentService;

    @GetMapping(value = "{petId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Appointment> getAppointments(@PathVariable Long petId,Map<String, Object> map) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        map.put("adminname", auth.getName());
        map.put("userRole",auth.getAuthorities().stream().findFirst().get().getAuthority());

        return appointmentService.getAppointments(petId);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Appointment bookAppointment(
            @RequestBody AppointmentBookDto appointmentBookDto, @RequestParam Long petId) {
        LocalDate appointmentDate = LocalDate.parse(appointmentBookDto.getAppointmentDate());
        LocalTime appointmentTime = LocalTime.parse(appointmentBookDto.getAppointmentTime());

        if (appointmentTime.isBefore(LocalTime.of(9, 0)) || appointmentTime.isAfter(LocalTime.of(17, 0))) {
            throw new IllegalArgumentException("Invalid appointment time. Please select a slot between 9:00 AM and 5:00 PM.");
        }

        Appointment newAppointment = appointmentService.bookAppointment(petId, appointmentDate, appointmentTime);
        return newAppointment;
    }


    @PostMapping(value = "changeStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String changeAppointmentStatus(
            @RequestParam("newStatus") String status, @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<? extends GrantedAuthority> first = authentication.getAuthorities().stream().findFirst();
        String authority = null;
        if(first.isPresent()){
            GrantedAuthority grantedAuthority = first.get();
             authority = grantedAuthority.getAuthority();
        }
        if (authority !=null && !authority.isEmpty() && authority.equalsIgnoreCase("ROLE_SUPERADMIN") ){
           return appointmentService.changeStatus(id, status);
        }
        return "";
    }
}
