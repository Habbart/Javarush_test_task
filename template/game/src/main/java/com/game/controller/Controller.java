package com.game.controller;


import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/rest")
public class Controller {

    @Autowired
    private PlayerService playerService;

    @GetMapping(name = "/players", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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
        playerService.savePlayer(player);
        return playerService.getPlayerById(player.getId());
    }

    @GetMapping("/players/{id}")
    public Player getPlayerById(@PathVariable long id){
        return playerService.getPlayerById(id);
    }

    @PostMapping("/players/{id}")
    public Player updatePlayer(@PathVariable long id, @RequestBody(required = false) Player player){
        playerService.updatePlayer(id, player);
        return playerService.getPlayerById(id);
    }

    @DeleteMapping("/players/{id}")
    public void deletePlayer(@RequestParam long id){
        playerService.deletePlayer(id);
    }

}
