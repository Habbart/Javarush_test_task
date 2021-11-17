package com.game.controller;


import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.service.PlayerPOJO;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/rest")
public class PlayerController {


    public static final String NAME = "name";
    public static final String TITLE = "name";
    public static final String RACE = "name";



    @Autowired
    private PlayerService playerService;

    @GetMapping("/players")
    public List<Player> getAllPlayers(@RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "title", required = false) String title,
                                      @RequestParam(value = "race", required = false) Race race,
                                      @RequestParam(value = "profession", required = false)Profession profession,
                                      @RequestParam(value = "after", required = false) Long after,
                                      @RequestParam(value = "before", required = false) Long before,
                                      @RequestParam(value = "banned", required = false) Boolean banned,
                                      @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                      @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                      @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                      @RequestParam(value = "maxLevel", required = false) Integer maxLevel,
                                      @RequestParam(value = "order", required = false) PlayerOrder order,
                                      @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                      @RequestParam(value = "pageSize", required = false) Integer pageSize){
        PlayerPOJO playerPOJO = new PlayerPOJO(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel, order, pageNumber, pageSize );
        return playerService.getAllPlayers(playerPOJO);
    }

    @GetMapping("/players/count")
    public int getCountOfPlayers(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "title", required = false) String title,
                                 @RequestParam(value = "race", required = false) Race race,
                                 @RequestParam(value = "profession", required = false)Profession profession,
                                 @RequestParam(value = "after", required = false) Long after,
                                 @RequestParam(value = "before", required = false) Long before,
                                 @RequestParam(value = "banned", required = false) Boolean banned,
                                 @RequestParam(value = "minExperience", required = false) Integer minExperience,
                                 @RequestParam(value = "maxExperience", required = false) Integer maxExperience,
                                 @RequestParam(value = "minLevel", required = false) Integer minLevel,
                                 @RequestParam(value = "maxLevel", required = false) Integer maxLevel){
        PlayerPOJO playerPOJO = new PlayerPOJO(name, title, race, profession, after, before, banned, minExperience, maxExperience, minLevel, maxLevel);
        return playerService.getCountOfPlayers(playerPOJO);
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
