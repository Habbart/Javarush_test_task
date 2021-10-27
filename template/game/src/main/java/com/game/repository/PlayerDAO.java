package com.game.repository;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;

public interface PlayerDAO {

    List<Player> getAllPlayers(Map<String, String> params);

    void savePlayer(Player player);

    Player updatePlayer(long id);

    Player getPlayerById(long id);

    void deletePlayer(long id);


}
