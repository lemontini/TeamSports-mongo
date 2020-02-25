package com.montini.teamsportsmongo.repository;

import com.montini.teamsportsmongo.model.Event;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface EventRepository extends MongoRepository<Event, ObjectId> {

    @Query("{ 'name' : ?0 }")
    List<Event> findByName(String name);

}