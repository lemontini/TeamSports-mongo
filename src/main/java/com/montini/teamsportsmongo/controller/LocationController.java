package com.montini.teamsportsmongo.controller;

import com.montini.teamsportsmongo.model.Location;
import com.montini.teamsportsmongo.model.Player;
import com.montini.teamsportsmongo.repository.LocationRepository;
import com.montini.teamsportsmongo.repository.PlayerRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("locations/")
public class LocationController {

    private static final Logger log = LogManager.getLogger(LocationController.class);

    @Autowired
    private LocationRepository locationRepository;

    /* BASIC CRUD */

    @PostMapping("/create")
    public String saveLocation(@RequestBody Location location) {
        locationRepository.save(location);
        return "Added a location with ID: " + location.getId();
    }

    @GetMapping("/read")
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }

    @GetMapping("/read/{id}")
    public Optional<Location> getLocation(@PathVariable ObjectId id) {
        return locationRepository.findById(id);
    }

    @PostMapping("/update/{id}")
    public String updateLocation(@RequestBody Location location, @PathVariable ObjectId id) {
        if (!locationRepository.findById(id).isPresent()) return "A location with ID " + id + " does not exist";
        location.setId(id);
        locationRepository.save(location);
        return "A location with ID " + id + " was updated";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteLocation(@PathVariable ObjectId id) {
        if (!locationRepository.findById(id).isPresent()) return "A location with ID " + id + " does not exist";
        locationRepository.deleteById(id);
        return "A location with ID " + id + " was deleted";
    }

    /* OTHER CONTROLLERS */

    // list all events in the the specified location
    @GetMapping("/read/{id}/events")
    public List<ObjectId> getEvents(@PathVariable ObjectId id) {
        Optional<Location> location = locationRepository.findById(id);
        if (!location.isPresent()) {
            log.info("Location with ID " + id + " does not exist");
            return null;
        }
        return location.get().getEvents();
    }
}