package com.game.repository;

import com.game.entity.Player;

import org.hibernate.Criteria;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import java.util.List;
import java.util.Map;

@Repository
public class PlayerDAOImpl implements PlayerDAO{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public List<Player> getAllPlayers(int pageNumber, int pageSize) {
        Query query = entityManager.createQuery("From Player", Player.class);
        query.setFirstResult((pageNumber-1) * pageSize);
        query.setMaxResults(pageSize);
        List <Player> playerList = query.getResultList();

        return playerList;
    }

    @Override
    @Transactional
    public List<Player> getListPlayers() {
        Query query = entityManager.createQuery("From Player", Player.class);
        List <Player> playerList = query.getResultList();
        return playerList;
    }

    @Override
    @Transactional
    public Player createPlayer(Player player) {
        entityManager.merge(player);
        return entityManager.find(Player.class, player.getId());
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
