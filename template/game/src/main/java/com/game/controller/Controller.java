package com.game.controller;


import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/rest")
public class Controller {

    @Autowired
    private PlayerService playerService;

    @GetMapping("/players")
    public List<Player> getAllPlayers(@PathVariable(required = false) Map<String, String> params){
        return playerService.getAllPlayers(params);
    }

    @GetMapping("/players/count")
    public int getCountOfPlayers(@PathVariable(required = false) Map<String, String> params){
        List<Player> result = playerService.getAllPlayers(params);
        return result.size();
    }

    @PostMapping("/players")
    public Player createPlayer(@RequestBody Player player){
        playerService.createPlayer(player);
        return playerService.getPlayerById(player.getId());
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
