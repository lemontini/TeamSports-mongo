package com.montini.teamsportsmongo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString

@Document(collection = "Event")
public class Event {
    @Id
    private ObjectId id;
    private String name;
    private ObjectId location;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ObjectId host;
    private List<ObjectId> participants = new ArrayList<>();

    public String addParticipant(ObjectId pid) {
        this.participants.add(pid);
        return "A player with ID " + pid + " was added to the event with ID " + this.id;
    }

    public String getDuration() {
        long duration = Duration.between(startTime, endTime).toMinutes();
        System.out.println(startTime + " | " + endTime + " | " + duration);
        String hours, minutes, junction;
        hours = (duration / 60 == 1) ? " hour" : " hours";
        junction = (duration > 60) ? " and " : "";
        minutes = (duration % 60 == 1) ? " minute" : " minutes";
        return ((duration > 60) ? (duration / 60 + hours + junction) : "") + duration % 60 + minutes;
    }
}