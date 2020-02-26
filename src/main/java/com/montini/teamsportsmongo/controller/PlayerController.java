package com.montini.teamsportsmongo.controller;

import com.montini.teamsportsmongo.model.Player;
import com.montini.teamsportsmongo.repository.PlayerRepository;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("players/")
public class PlayerController {

    private static final Logger log = LoggerFactory.getLogger(PlayerController.class);

    @Autowired
    private PlayerRepository playerRepository;

    /* BASIC CRUD */

    @PostMapping("/create")
    public String savePlayer(@RequestBody Player player) {
        playerRepository.save(player);
        return "Added a player [" + player.getName() + "]";
    }

    @GetMapping("/read")
    public List<Player> getPlayers() {
        return playerRepository.findAll();
    }

    @GetMapping("/read/{pid}")
    public Optional<Player> getPlayer(@PathVariable ObjectId pid) {
        return playerRepository.findById(pid);
    }

    @PostMapping("/update/{pid}")
    public String updatePlayer(@RequestBody Player player, @PathVariable ObjectId pid) {
        if (!playerRepository.findById(pid).isPresent()) return "A player with ID " + pid + " does not exist";
        player.setId(pid);
        playerRepository.save(player);
        return "A player [" + player.getName() + "] was updated";
    }

    @DeleteMapping("/delete/{pid}")
    public String deletePlayer(@PathVariable ObjectId pid) {
        if (!playerRepository.findById(pid).isPresent()) return "A player with ID " + pid + " does not exist";
        playerRepository.deleteById(pid);
        return "A player with ID " + pid + " was deleted";
    }

    /* OTHER CONTROLLERS */

    // list all events the player is attending
    @GetMapping("/read/{pid}/events")
    public List<ObjectId> getEvents(@PathVariable ObjectId pid) {
        Optional<Player> player = playerRepository.findById(pid);
        if (!player.isPresent()) {
            log.info("Player with ID " + pid + " does not exist");
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