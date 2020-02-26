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

@Document(collection = "Player")
public class Player {
    @Id
    private ObjectId id;
    private String name;
    @Field(name = "events")
    private List<ObjectId> events = new ArrayList<>();

    public String attend(ObjectId eid) {
        if (this.events.contains(eid))
            return "A player with ID " + this.id + " is already in the list of participants of this event";
        this.events.add(eid);
        return "[" + this.name + "] will participate in event with ID " + eid;
    }
}