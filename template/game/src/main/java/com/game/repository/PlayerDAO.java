package com.game.repository;

import com.game.entity.Player;

import java.util.List;


public interface PlayerDAO {

    List<Player> getAllPlayers();

    Player createPlayer(Player player);

    Player getPlayerById(long id);

    void deletePlayer(long id);


}
