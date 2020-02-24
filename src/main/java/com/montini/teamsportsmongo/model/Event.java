package com.montini.teamsportsmongo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString

@Document(collection="Event")
public class Event {
    @Id
    private ObjectId id;
    private String name;
    private ObjectId location;
    private List<ObjectId> participants = new ArrayList<>();

    public String addParticipant(ObjectId pid) {
        this.participants.add(pid);
        return "A player with ID " + pid + " was added to the event with ID " + this.id;
    }
}