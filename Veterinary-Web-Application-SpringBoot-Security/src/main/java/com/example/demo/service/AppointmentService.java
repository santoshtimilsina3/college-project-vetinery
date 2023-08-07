package com.example.demo.service;

import com.example.demo.model.Appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentService {

    public List<Appointment> getAppointments(Long id);

    Appointment bookAppointment(Long pet, LocalDate date, LocalTime time);

    String changeStatus(Long id, String status);
}
