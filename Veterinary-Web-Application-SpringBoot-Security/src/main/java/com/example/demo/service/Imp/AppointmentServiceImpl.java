package com.example.demo.service.Imp;

import com.example.demo.enums.AppointmentStatus;
import com.example.demo.model.Appointment;
import com.example.demo.model.Pet;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    @Autowired
    AppointmentRepository repository;

    private final PetServiceImp petService;

    @Override
    public List<Appointment> getAppointments(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be empty");
        }
        Optional<List<Appointment>> byPet = repository.findByPetId(id);
        if (byPet.isPresent()) {
            return byPet.get();
        }
        return null;
    }

    @Override
    public Appointment bookAppointment(Long pet, LocalDate date, LocalTime time) {
        Optional<Pet> bookingPet = petService.findById(pet);
        if (!bookingPet.isPresent()) {
            return null;
        }
        Appointment newAppointment = new Appointment();
        LocalDateTime localDateTime = LocalDateTime.of(date, time);
        newAppointment.setPet(bookingPet.get());
        newAppointment.setDateTime(localDateTime);
        newAppointment.setStatus(AppointmentStatus.CONFIRMED);
        repository.save(newAppointment);
        return newAppointment;
    }

    @Override
    public String changeStatus(Long id, String status) {
        if (id == null) {
            throw new IllegalArgumentException("Cannot find Appointment with id " + id);
        }
        Optional<Appointment> appointment = repository.findById(id);
        if (appointment.isPresent()) {
            Appointment changeStatus = appointment.get();
            changeStatus.setStatus(AppointmentStatus.valueOf(status));
            repository.save(changeStatus);
            return "Success";
        }
        return "Failed";
    }

}