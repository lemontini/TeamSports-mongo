package com.montini.teamsportsmongo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString

@Document(collection = "Location")
public class Location {
    @Id
    private ObjectId id;
    private String name;
    private String address;
    private int maxCourts;
    @Field(name = "events")
    private List<ObjectId> events = new ArrayList<>();
    @Field(name = "bookings")
    private List<Booking> bookings = new ArrayList<>();

    public String hostEvent(ObjectId eid) {
        // TODO: implement checking if the location is available at the specific time (include time into the arguments)
        // if (this.events.contains(eid))
        //     return "A player with ID " + this.id + " is already in the list of participants of this event";
        this.events.add(eid);
        return "An event with ID " + eid + " will take place in the location with ID " + this.id;
    }

    public boolean isBookingAvailable(LocalDateTime startTime, LocalDateTime endTime) {
        for (Booking booking : bookings) {
            if (startTime.isEqual(booking.getStartTime())
                    || startTime.isAfter(booking.getStartTime()) & startTime.isBefore(booking.getEndTime())
                    || (endTime.isAfter(booking.getStartTime()) & endTime.isBefore(booking.getEndTime())))
                // TODO: implement the availability based on the maxCourts
                return false;
        }
        return true;
    }
}