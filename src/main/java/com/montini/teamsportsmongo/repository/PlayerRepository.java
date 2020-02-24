package com.montini.teamsportsmongo.repository;

import com.montini.teamsportsmongo.model.Player;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlayerRepository extends MongoRepository<Player, ObjectId> {
}
