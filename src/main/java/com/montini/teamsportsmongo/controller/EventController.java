package com.montini.teamsportsmongo.controller;

import com.montini.teamsportsmongo.model.Event;
import com.montini.teamsportsmongo.model.Location;
import com.montini.teamsportsmongo.model.Player;
import com.montini.teamsportsmongo.repository.EventRepository;
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
@RequestMapping("events/")
public class EventController {

    private static final Logger log = LogManager.getLogger(EventController.class);

    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private PlayerRepository playerRepository;

    /* BASIC CRUD */

    @PostMapping("/create")
    public String savePlayEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return "Added an event with ID: " + event.getId();
    }

    @GetMapping("/read")
    public List<Event> getPlayEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/read/{id}")
    public Optional<Event> getPlayEvent(@PathVariable ObjectId id) {
        return eventRepository.findById(id);
    }

    @PostMapping("/update/{id}")
    public String updatePlayEvent(@RequestBody Event event, @PathVariable ObjectId id) {
        event.setId(id);
        eventRepository.save(event);
        return "An event with ID " + id + " was updated";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePlayEvent(@PathVariable ObjectId id) {
        eventRepository.deleteById(id);
        return "An event with ID " + id + " was deleted";
    }

    /* OTHER CONTROLLERS */

    // add new participant to the event
    @GetMapping(value = "/update", params = {"eid", "pid"})
    public String addParticipant(@RequestParam("eid") ObjectId eventId,
                                 @RequestParam("pid") ObjectId playerId) {
        Player player;
        Event event;

        // check if the player exists
        if (playerRepository.findById(playerId).isPresent()) {
            player = playerRepository.findById(playerId).get();
            log.info("PLAYER: " + player);
        } else return "A player with ID " + playerId + " does not exist";

        // check if the event exists
        if (eventRepository.findById(eventId).isPresent()) {
            event = eventRepository.findById(eventId).get();
            log.info("EVENT: " + event);
        } else return "An event with ID " + eventId + " does not exist";

        // check if the specified player already participates in the specified event
        if (player.getEvents().contains(eventId) || event.getParticipants().contains(playerId))
            return "A player with ID " + playerId + " is already in the list of participants of this event";

        player.attend(eventId);
        event.addParticipant(playerId);
        playerRepository.save(player);
        eventRepository.save(event);

        return "An event with ID " + eventId + " was updated";
    }

    @GetMapping(value = "/update", params = {"eid", "lid"})
    public String addLocation(@RequestParam("eid") ObjectId eventId,
                              @RequestParam("lid") ObjectId locationId) {
        Location location;
        Event event;

        // check if the location exists
        if (locationRepository.findById(locationId).isPresent()) {
            location = locationRepository.findById(locationId).get();
            log.info("PLAYER: " + location);
        } else return "A player with ID " + locationId + " does not exist";

        // check if the event exists
        if (eventRepository.findById(eventId).isPresent()) {
            event = eventRepository.findById(eventId).get();
            log.info("EVENT: " + event);
        } else return "An event with ID " + eventId + " does not exist";


        // TODO: implement the functionality of locations booking according to free time duration available
        // // check if the specified event already participates in the specified event
        // if (player.getEvents().contains(eventId) || event.getParticipants().contains(playerId))
        //     return "A player with ID " + playerId + " is already in the list of participants of this event";

        location.hostEvent(eventId);
        event.setLocation(locationId);
        locationRepository.save(location);
        eventRepository.save(event);

        return "A location with ID " + locationId + " was assigned for event with ID " + eventId;
    }

}