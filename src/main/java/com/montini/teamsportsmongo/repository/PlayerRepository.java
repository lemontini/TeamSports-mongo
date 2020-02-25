package com.montini.teamsportsmongo.repository;

import com.montini.teamsportsmongo.model.Player;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PlayerRepository extends MongoRepository<Player, ObjectId> {

    @Query("{ 'name' : ?0 }")
    List<Player> findByName(String name);

}