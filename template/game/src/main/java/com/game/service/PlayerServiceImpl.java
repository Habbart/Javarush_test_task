package com.game.service;

import com.game.entity.Player;
import com.game.exception_handler.IncorrectPlayerArguments;
import com.game.exception_handler.NoSuchPlayerException;
import com.game.repository.PlayerDAO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@PersistenceContext
public class PlayerServiceImpl implements PlayerService{

    @Autowired
    private PlayerDAO playerDAO;

    @Override
    public List<Player> getAllPlayers(Map<String, String> params) {
        return null;
    }

    @Override
    public Player savePlayer(Player player) {
        //проверяем валидность данных
        if(!isPlayerValid(player)) throw new IncorrectPlayerArguments();
        //высчитываем уровень и количество опыта до следующего уровня
        int exp = player.getExperience();
        int level = (int)(Math.sqrt(2500+200*exp) - 50)/100;
        player.setLevel(level);
        player.setUnitNextLevel(50*(level+ 1) * (level + 2) - exp);
        playerDAO.savePlayer(player);
        return player;
    }

    @Override
    public void updatePlayer(long id, Player player) {
        playerDAO.updatePlayer(id);
    }

    @Override
    public Player getPlayerById(long id) {
        return playerDAO.getPlayerById(id);
    }

    @Override
    public void deletePlayer(long id) {
        if(getPlayerById(id) == null){
            throw new NoSuchPlayerException();
        }
        playerDAO.deletePlayer(id);
    }


    private boolean isPlayerValid(Player player){
        if(player.getName().length() > 12) return false;
        if(player.getTitle().length() > 30) return false;
        if(player.getName().length() == 0 || player.getName() == "" || player.getName() == null) return false;
        if(player.getExperience() <0 || player.getExperience() > 10_000_000L) return false;
        if(player.getBirthday().before(new Date(1 / 2000)) || player.getBirthday().after(new Date(1 / 3999))) return false;
        return true;
    }
}
