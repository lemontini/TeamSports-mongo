package com.montini.teamsportsmongo.repository;

import com.montini.teamsportsmongo.model.Location;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface LocationRepository extends MongoRepository<Location, ObjectId> {

    @Query("{ 'name' : ?0 }")
    List<Location> findByName(String name);

}