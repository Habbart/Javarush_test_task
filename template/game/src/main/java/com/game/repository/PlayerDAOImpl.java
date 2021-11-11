package com.game.repository;

import com.game.entity.Player;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


@Repository
public class PlayerDAOImpl implements PlayerDAO{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<Player> getAllPlayers() {
        return entityManager.createQuery("From Player", Player.class).getResultList();
    }


    @Override
    @Transactional
    public Player createPlayer(Player player) {
        return entityManager.merge(player);
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
