package com.montini.teamsportsmongo.controller;

import com.montini.teamsportsmongo.model.Booking;
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

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
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

    // create new event
    @PostMapping("/create")
    public String saveEvent(@RequestBody Event event) {
        eventRepository.save(event);
        return "Added an event with ID: " + event.getId();
    }

    // read all events
    @GetMapping("/read")
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    // read a specific event
    @GetMapping("/read/{id}")
    public Optional<Event> getEvent(@PathVariable ObjectId id) {
        return eventRepository.findById(id);
    }

    // update a specific event
    @PostMapping("/update/{id}")
    public String updateEvent(@RequestBody Event event, @PathVariable ObjectId id) {
        event.setId(id);
        eventRepository.save(event);
        return "An event with ID " + id + " was updated";
    }

    // delete a specific event
    @DeleteMapping("/delete/{id}")
    public String deleteEvent(@PathVariable ObjectId id) {
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

    // set the location of the event
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
        // check if the specified event already takes place in the specified location
        if (location.getEvents().contains(eventId))
            return "[" + event.getName() + "] already has the location set";

        location.hostEvent(eventId);
        event.setLocation(locationId);
        locationRepository.save(location);
        eventRepository.save(event);

        return "[" + eventRepository.findById(eventId).get().getName() + "] will take place in [" + locationRepository.findById(locationId).get().getName() + "]";
    }

    // find event by name
    @GetMapping(value = "/read", params = {"name"})
    public List<Event> getEventByName(@RequestParam("name") String name) {
        return eventRepository.findByName(name);
    }

    // set the time for the event (time is persisted in UTC)
    @GetMapping(value = "/update", params = {"eid", "start", "end"})
    public String updateEventDate(@RequestParam("eid") ObjectId eid,
                                  @RequestParam("start") String startTime,
                                  @RequestParam("end") String endTime) {
        Location location;
        Event event;

        // select the specified event
        if (eventRepository.findById(eid).isPresent()) {
            event = eventRepository.findById(eid).get();
        } else return "An event with ID " + eid + " does not exist";

        // select the location of the specified event
        if (locationRepository.findById(event.getLocation()).isPresent()) {
            location = locationRepository.findById(event.getLocation()).get();
        } else return "The location does not exist";

        // convert the date from String to LocalDateTime as required by the model class field
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime cStartTime = LocalDateTime.parse(startTime, formatter);
        LocalDateTime cEndTime = LocalDateTime.parse(endTime, formatter);

        // check if the specified time is available (not booked yet)
        if (!location.isBookingAvailable(cStartTime, cEndTime)) return "[" + location.getName() + "]" + " is unavailable for the specified period";

        // everything is OK, complete the booking
        location.getBookings().add(new Booking(cStartTime, cEndTime));
        event.setStartTime(cStartTime);
        event.setEndTime(cEndTime);
        eventRepository.save(event);
        locationRepository.save(location);
        return "[" + event.getName() + "] will take place on " + startTime + " for " + event.getDuration();
    }

}