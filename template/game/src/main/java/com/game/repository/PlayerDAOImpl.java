package com.game.repository;

import com.game.entity.Player;
import com.game.exception_handler.NoSuchPlayerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Map;

@Repository
public class PlayerDAOImpl implements PlayerDAO{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<Player> getAllPlayers(Map<String, String> params) {
        return null;
    }

    @Override
    @Transactional
    public void savePlayer(Player player) {
        entityManager.merge(player);
    }

    @Override
    @Transactional
    public Player updatePlayer(long id) {
        return null;
    }

    @Override
    @Transactional
    public Player getPlayerById(long id) {
        return entityManager.find(Player.class, id);
    }

    @Override
    @Transactional
    public void deletePlayer(long id) {
        Query query = entityManager.createQuery("delete from Player where id= : playerId");
        query.setParameter("playerId", id);
        query.executeUpdate();
    }

}
