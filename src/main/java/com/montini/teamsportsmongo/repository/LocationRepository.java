package com.montini.teamsportsmongo.repository;

import com.montini.teamsportsmongo.model.Location;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LocationRepository extends MongoRepository<Location, ObjectId> {
}