package com.example.demo.controller;

import com.example.demo.dto.request.AppointmentBookDto;
import com.example.demo.model.Appointment;
import com.example.demo.service.AppointmentService;
import com.example.demo.util.ApiPaths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

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

        Appointment newAppointment =
                appointmentService.bookAppointment(petId, appointmentDate, appointmentTime);
        return newAppointment;
    }

    @PostMapping(value = "changeStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String changeAppointmentStatus(
            @RequestParam("newStatus") String status, @PathVariable Long id) {
        return appointmentService.changeStatus(id, status);
    }
}
