package com.montini.teamsportsmongo.repository;

import com.montini.teamsportsmongo.model.Event;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, ObjectId> {
}