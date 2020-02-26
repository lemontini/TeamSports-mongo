package com.montini.teamsportsmongo.controller;

import com.montini.teamsportsmongo.model.Location;
import com.montini.teamsportsmongo.model.Player;
import com.montini.teamsportsmongo.repository.LocationRepository;
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
        return "Added a location [" + location.getName() + "]";
    }

    @GetMapping("/read")
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }

    @GetMapping("/read/{lid}")
    public Optional<Location> getLocation(@PathVariable ObjectId lid) {
        return locationRepository.findById(lid);
    }

    @PostMapping("/update/{lid}")
    public String updateLocation(@RequestBody Location location, @PathVariable ObjectId lid) {
        if (!locationRepository.findById(lid).isPresent()) return "A location with ID " + lid + " does not exist";
        location.setId(lid);
        locationRepository.save(location);
        return "A location [" + location.getName() + "] was updated";
    }

    @DeleteMapping("/delete/{lid}")
    public String deleteLocation(@PathVariable ObjectId lid) {
        if (!locationRepository.findById(lid).isPresent()) return "A location with ID " + lid + " does not exist";
        locationRepository.deleteById(lid);
        return "A location with ID " + lid + " was deleted";
    }

    /* OTHER CONTROLLERS */

    // list all events in the the specified location
    @GetMapping("/read/{lid}/events")
    public List<ObjectId> getEvents(@PathVariable ObjectId lid) {
        Optional<Location> location = locationRepository.findById(lid);
        if (!location.isPresent()) {
            log.info("Location with ID " + lid + " does not exist");
            return null;
        }
        return location.get().getEvents();
    }

    // find location by name
    @GetMapping(value = "/read", params = {"name"})
    public List<Location> getLocationByName(@RequestParam("name") String name) {
        return locationRepository.findByName(name);
    }

}