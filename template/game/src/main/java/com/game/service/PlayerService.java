package com.game.service;

import com.game.entity.Player;

import java.util.List;

public interface PlayerService {

    List<Player> getAllPlayers(RequestedPlayer requestedPlayer);

    Player createPlayer(Player player);

    Player updatePlayer(long id, Player player);

    Player getPlayerById(long id);

    void deletePlayer(long id);

    int getCountOfPlayers(RequestedPlayer requestedPlayer);
}
