package com.game.repository;

import com.game.entity.Player;

import java.util.List;
import java.util.Map;

public interface PlayerDAO {

    List<Player> getAllPlayers(int pageNumber, int pageSize);

    Player createPlayer(Player player);

    Player getPlayerById(long id);

   List<Player> getListPlayers();

    void deletePlayer(long id);


}
