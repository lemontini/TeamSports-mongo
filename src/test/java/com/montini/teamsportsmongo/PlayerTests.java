package com.montini.teamsportsmongo;

import com.montini.teamsportsmongo.model.Player;
import com.montini.teamsportsmongo.repository.PlayerRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class PlayerTests {

    private static final Logger log = LoggerFactory.getLogger(PlayerTests.class);

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testPlayerIdSetByMongoDb() {
        Player player = new Player();
        player.setName("dummyName");
        playerRepository.save(player);
        ObjectId id = playerRepository.findAll().get(playerRepository.findAll().size() - 1).getId();
        Assertions.assertNotNull(id);
        playerRepository.delete(player);
        Assertions.assertEquals(Optional.empty(), playerRepository.findById(id));
    }
}
