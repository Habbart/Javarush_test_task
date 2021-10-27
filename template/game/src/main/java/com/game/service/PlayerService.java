package com.game.service;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;

public interface PlayerService {

    List<Player> getAllPlayers(Map<String, String> params);

    Player savePlayer(Player player);

    void updatePlayer(long id, Player player);

    Player getPlayerById(long id);

    void deletePlayer(long id);



}
