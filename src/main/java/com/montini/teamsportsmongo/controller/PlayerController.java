package com.montini.teamsportsmongo.controller;

import com.montini.teamsportsmongo.model.Player;
import com.montini.teamsportsmongo.repository.PlayerRepository;
import org.bson.types.ObjectId;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("players/")
public class PlayerController {

    private static final Logger log = LogManager.getLogger(PlayerController.class);

    @Autowired
    private PlayerRepository playerRepository;

    /* BASIC CRUD */

    @PostMapping("/create")
    public String savePlayer(@RequestBody Player player) {
        playerRepository.save(player);
        return "Added a player with ID: " + player.getId();
    }

    @GetMapping("/read")
    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    @GetMapping("/read/{id}")
    public Optional<Player> getPlayer(@PathVariable ObjectId id) {
        return playerRepository.findById(id);
    }

    @PostMapping("/update/{id}")
    public String updatePlayer(@RequestBody Player player, @PathVariable ObjectId id) {
        if (!playerRepository.findById(id).isPresent()) return "A player with ID " + id + " does not exist";
        player.setId(id);
        playerRepository.save(player);
        return "A player with ID " + id + " was updated";
    }

    @DeleteMapping("/delete/{id}")
    public String deletePlayer(@PathVariable ObjectId id) {
        if (!playerRepository.findById(id).isPresent()) return "A player with ID " + id + " does not exist";
        playerRepository.deleteById(id);
        return "A player with ID " + id + " was deleted";
    }

    /* OTHER CONTROLLERS */

    // list all events the player is attending
    @GetMapping("/read/{id}/events")
    public List<ObjectId> getEvents(@PathVariable ObjectId id) { // public List<ObjectId> getEvents(@PathVariable ObjectId id) {
        Optional<Player> player = playerRepository.findById(id);
        if (!player.isPresent()) {
            log.info("Player with ID " + id + " does not exist");
            return null;
        }
        return player.get().getEvents();
    }

    // find player by name
    @GetMapping(value = "/read", params = {"name"})
    public List<Player> getPlayerByName(@RequestParam("name") String name) {
        return playerRepository.findByName(name);
    }
}