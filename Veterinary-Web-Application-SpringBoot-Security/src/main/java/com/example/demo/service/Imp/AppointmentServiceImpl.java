package com.example.demo.service.Imp;

import com.example.demo.enums.AppointmentStatus;
import com.example.demo.model.Appointment;
import com.example.demo.model.ClusterData;
import com.example.demo.model.Pet;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.DoublePoint;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterer;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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

    @Override
    public List<ClusterData> calcualteClustering() {
        List<Appointment> appointments = repository.findAll(); // Fetch appointment data from the database

        List<ClusterData> clusteredData = performKMeansClustering(appointments);

        return clusteredData;
    }

//    public List<ClusterData> performKMeansClustering(List<Appointment> appointments) {
//        List<DoublePoint> features = new ArrayList<>();
//
//        // Convert LocalDateTime to hour of the day for clustering
//        for (Appointment appointment : appointments) {
//            int hourOfDay = appointment.getDateTime().getHour();
//            features.add(new DoublePoint((double) hourOfDay));
//        }
//
//        int numClusters = 2; // Morning and afternoon
//        DistanceMeasure distanceMeasure = new EuclideanDistance();
//        Clusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(numClusters, 1000, distanceMeasure);
//        List<Cluster<DoublePoint>> clusters = (List<Cluster<DoublePoint>>) clusterer.cluster(features);
//
//        // Prepare clustered data for frontend
//        List<ClusterData> clusteredData = new ArrayList<>();
//        for (int i = 0; i < clusters.size(); i++) {
//            ClusterData clusterData = new ClusterData();
//            clusterData.setClusterNumber(i);
//            List<Integer> appointmentHours = new ArrayList<>();
//            for (DoublePoint point : clusters.get(i).getPoints()) {
//                appointmentHours.add((int) point.getPoint()[0]);
//            }
//            clusterData.setAppointmentHours(appointmentHours);
//            clusteredData.add(clusterData);
//        }
//
//        return clusteredData;
//    }

    public List<ClusterData> performKMeansClustering(List<Appointment> appointments) {


        List<DoublePoint> features = new ArrayList<>();

        for (Appointment appointment : appointments) {
            double[] feature = new double[2];
            feature[0] = appointment.getDateTime().getDayOfWeek().getValue(); // Day of the week
            feature[1] = appointment.getDateTime().getHour() + appointment.getDateTime().getMinute() / 60.0; // Time of the day
            features.add(new DoublePoint(feature));
        }

        int numClusters = 2; // Number of clusters
        DistanceMeasure distanceMeasure = new EuclideanDistance();
        Clusterer<DoublePoint> clusterer = new KMeansPlusPlusClusterer<>(numClusters, 1000, distanceMeasure);
        List<Cluster<DoublePoint>> clusters = (List<Cluster<DoublePoint>>) clusterer.cluster(features);

        List<ClusterData> clusteredData = new ArrayList<>();
        for (int i = 0; i < clusters.size(); i++) {
            ClusterData clusterData = new ClusterData();
            clusterData.setClusterNumber(i);
            List<double[]> clusterPoints = new ArrayList<>();
            for (DoublePoint point : clusters.get(i).getPoints()) {
                clusterPoints.add(point.getPoint());
            }
            clusterData.setClusterPoints(clusterPoints);
            clusteredData.add(clusterData);
        }

        return clusteredData;
    }

}