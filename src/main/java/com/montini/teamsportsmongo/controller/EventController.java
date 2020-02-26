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

    // // host should not be set via REST API, this is only for debug purposes
    // @GetMapping(value = "/update/{eid}",params = {"host"})
    // public String setHost(@PathVariable ObjectId eid, @RequestParam(name = "host") ObjectId hid) {
    //     Event event = eventRepository.findById(eid).get();
    //     event.setHost(hid);
    //     eventRepository.save(event);
    //     return "";
    // }

    // read all events
    @GetMapping("/read")
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    // read a specific event
    @GetMapping("/read/{eid}")
    public Optional<Event> getEvent(@PathVariable ObjectId eid) {
        return eventRepository.findById(eid);
    }

    // update a specific event
    @PostMapping("/update/{eid}")
    public String updateEvent(@RequestBody Event event, @PathVariable ObjectId eid) {
        event.setId(eid);
        eventRepository.save(event);
        return "An event with ID " + eid + " was updated";
    }

    // delete a specific event
    @DeleteMapping("/delete/{eid}")
    public String deleteEvent(@PathVariable ObjectId eid) {
        eventRepository.deleteById(eid);
        return "An event with ID " + eid + " was deleted";
    }

    /* OTHER CONTROLLERS */

    // add new participant to the event
    @GetMapping(value = "/update/{eid}", params = {"pid"})
    public String addParticipant(@PathVariable("eid") ObjectId eid,
                                 @RequestParam("pid") ObjectId pid) {
        Player player;
        Event event;

        // check if the player exists
        if (playerRepository.findById(pid).isPresent()) {
            player = playerRepository.findById(pid).get();
            log.info("PLAYER: " + player);
        } else return "A player with ID " + pid + " does not exist";

        // check if the event exists
        if (eventRepository.findById(eid).isPresent()) {
            event = eventRepository.findById(eid).get();
            log.info("EVENT: " + event);
        } else return "An event with ID " + eid + " does not exist";

        // check if the specified player already participates in the specified event
        if (player.getEvents().contains(eid) || event.getParticipants().contains(pid))
            return "A player with ID " + pid + " is already in the list of participants of this event";

        player.attend(eid);
        event.addParticipant(pid);
        playerRepository.save(player);
        eventRepository.save(event);

        return "[" + player.getName() + "] will attend the [" + event.getName() + "]";
    }

    // set the location of the event
    @GetMapping(value = "/update/{eid}", params = {"lid"})
    public String addLocation(@PathVariable ObjectId eid,
                              @RequestParam("lid") ObjectId lid) {
        Location location;
        Event event;

        // check if the location exists
        if (locationRepository.findById(lid).isPresent()) {
            location = locationRepository.findById(lid).get();
            log.info("PLAYER: " + location);
        } else return "A player with ID " + lid + " does not exist";

        // check if the event exists
        if (eventRepository.findById(eid).isPresent()) {
            event = eventRepository.findById(eid).get();
            log.info("EVENT: " + event);
        } else return "An event with ID " + eid + " does not exist";

        // TODO: implement the functionality of locations booking according to free time duration available
        // check if the specified event already takes place in the specified location
        if (location.getEvents().contains(eid))
            return "[" + event.getName() + "] already has the location set";

        location.hostEvent(eid);
        event.setLocation(lid);
        locationRepository.save(location);
        eventRepository.save(event);

        return "[" + eventRepository.findById(eid).get().getName() + "] will take place in [" + locationRepository.findById(lid).get().getName() + "]";
    }

    // find event by name
    @GetMapping(value = "/read", params = {"name"})
    public List<Event> getEventByName(@RequestParam("name") String name) {
        return eventRepository.findByName(name);
    }

    // set the time for the event (time is persisted in UTC)
    @GetMapping(value = "/update/{eid}", params = {"start", "end"})
    public String updateEventDate(@PathVariable ObjectId eid,
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
        if (!location.isBookingAvailable(cStartTime, cEndTime))
            return "[" + location.getName() + "]" + " is unavailable for the specified period";

        // everything is OK, complete the booking
        location.getBookings().add(new Booking(cStartTime, cEndTime));
        event.setStartTime(cStartTime);
        event.setEndTime(cEndTime);
        eventRepository.save(event);
        locationRepository.save(location);
        return "[" + event.getName() + "] will take place on " + startTime + " for " + event.getDuration();
    }

}