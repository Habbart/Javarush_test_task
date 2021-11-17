package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.RequestedPlayer;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/rest")
public class PlayerController {


    @Autowired
    private PlayerService playerService;

    @GetMapping("/players")
    public List<Player> getAllPlayers(RequestedPlayer requestedPlayer){
        return playerService.getAllPlayers(requestedPlayer);
    }

    @GetMapping("/players/count")
    public int getCountOfPlayers(RequestedPlayer requestedPlayer){
        return playerService.getCountOfPlayers(requestedPlayer);
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Player player){
        return playerService.createPlayer(player);
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@PathVariable long id, @RequestBody(required = false) Player player){
        return playerService.updatePlayer(id, player);
    }
    @GetMapping("/players/{id}")
    public Player getPlayerById(@PathVariable long id){
        return playerService.getPlayerById(id);
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@PathVariable long id){
        playerService.deletePlayer(id);

    }

}
