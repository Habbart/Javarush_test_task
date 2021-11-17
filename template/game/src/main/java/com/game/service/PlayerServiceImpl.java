package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.exception_handler.NoSuchPlayerException;
import com.game.repository.PlayerDAO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@PersistenceContext
public class PlayerServiceImpl implements PlayerService{

    @Autowired
    private PlayerDAO playerDAO;

    @Override
    public List<Player> getAllPlayers(RequestedPlayer requestedPlayer) {
        List<Player> allPlayersList = playerDAO.getAllPlayers();
        Predicate<Player> filter = filterByParamsFromRequestedPlayer(requestedPlayer).stream().reduce(Predicate::and).orElse(x -> true);
        //фильтруем лист в соответствии с предикатами
        allPlayersList = allPlayersList.stream().filter(filter).collect(Collectors.toList());
        //проверяем в каком порядке должен быть отсрортирован лист
        Function<List<Player>, List<Player>> sortForList = s -> {
            if(requestedPlayer.getOrder() == null) {
                return s.stream().sorted((x, y) -> (Long.compare(x.getId(), y.getId()))).collect(Collectors.toList());
            } else {
                PlayerOrder playerOrder = requestedPlayer.getOrder();
                switch (playerOrder) {
                    case NAME:
                        return s.stream().sorted((x, y) -> (x.getName().compareTo(y.getName()))).collect(Collectors.toList());
                    case LEVEL:
                        return s.stream().sorted((x, y) -> (Integer.compare(x.getLevel(), y.getLevel()))).collect(Collectors.toList());
                    case BIRTHDAY:
                        return s.stream().sorted((x, y) -> (x.getBirthday().compareTo(y.getBirthday()))).collect(Collectors.toList());
                    case EXPERIENCE:
                        return s.stream().sorted((x, y) -> (Integer.compare(x.getExperience(), y.getExperience()))).collect(Collectors.toList());
                    case ID:
                        return s.stream().sorted((x, y) -> (Long.compare(x.getId(), y.getId()))).collect(Collectors.toList());
                }
            }
            return s;
        };
        allPlayersList = sortForList.apply(allPlayersList);

        //проверяем была ли передана пагинация и ограничиваем лист в соответствии с ней
        int pageNumber = 0;
        int pageSize = 3;
        if(requestedPlayer.getPageNumber() != null){
            pageNumber = requestedPlayer.getPageNumber();
        }
        if(requestedPlayer.getPageSize() != null){
            pageSize = requestedPlayer.getPageSize();
        }
        int startOfSublist = pageNumber*pageSize;
        int endOfSublist = (pageNumber*pageSize) + pageSize;
        if(allPlayersList.size() < endOfSublist){
            endOfSublist = startOfSublist + allPlayersList.size() - startOfSublist;
        }
        allPlayersList = allPlayersList.subList(startOfSublist, endOfSublist);
        allPlayersList = sortForList.apply(allPlayersList);
        return allPlayersList;


    }

    @Override
    public int getCountOfPlayers(RequestedPlayer requestedPlayer) {
        if (requestedPlayer.isEmpty()) return playerDAO.getAllPlayers().size();
        List<Player> allPlayersList = playerDAO.getAllPlayers();
        Predicate<Player> filter = filterByParamsFromRequestedPlayer(requestedPlayer).stream().reduce(Predicate::and).orElse(x -> true);
        allPlayersList = allPlayersList.stream().filter(filter).collect(Collectors.toList());
        return allPlayersList.size();
    }

    @Override
    public Player createPlayer(Player player) {
        Validator.isPlayerValid(player);
        calculateLevelAndUntilNextLevel(player);
        return playerDAO.createPlayer(player);
    }

    @Override
    public Player updatePlayer(long id, Player player) {
        Validator.checkId(id);
        Validator.checkExperienceOfPlayer(player);
        Validator.checkBirthdayOfPlayer(player);
        player.setId(id);
        Player playerFromDB = playerDAO.getPlayerById(id);
        if(playerFromDB == null) throw new NoSuchPlayerException(String.format("player with this id: %d not found", id));
        Player playerForUpdate = mergePlayers(player, playerFromDB);
        calculateLevelAndUntilNextLevel(playerForUpdate);
        return playerDAO.createPlayer(playerForUpdate);
    }

    @Override
    public Player getPlayerById(long id) {
        Validator.checkId(id);
        if(playerDAO.getPlayerById(id) == null) throw new NoSuchPlayerException(String.format("player with this id: %d not found", id));
        return playerDAO.getPlayerById(id);
    }

    @Override
    public void deletePlayer(long id) {
        Validator.checkId(id);
        if(playerDAO.getPlayerById(id) == null) throw new NoSuchPlayerException(String.format("player with this id: %d not found", id));
        playerDAO.deletePlayer(id);
    }

    private void print(List<Player> playerList){
        playerList.forEach(s -> System.out.print(s.getName() + " "+ s.getTitle()));
        System.out.println();
    }


    private List<Predicate<Player>> filterByParamsFromRequestedPlayer(RequestedPlayer requestedPlayer){
        List<Predicate<Player>> allPredicates = new ArrayList<>();

        Predicate<Player> namePredicate = s -> {
            if(requestedPlayer.getName() == null || requestedPlayer.getName().isEmpty()) return true;
            return s.getName().contains(requestedPlayer.getName());
        };
        Predicate<Player> titlePredicate = s -> {
            if(requestedPlayer.getTitle() == null || requestedPlayer.getTitle().isEmpty()) return true;
            return s.getTitle().contains(requestedPlayer.getTitle());
        };

        Predicate<Player> racePredicate = s -> {
            if(requestedPlayer.getRace() == null) return true;
            return s.getRace().equals(requestedPlayer.getRace());
        };
        Predicate<Player> professionPredicate = s -> {
            if(requestedPlayer.getProfession() == null) return true;
            return s.getProfession().equals(requestedPlayer.getProfession());
        };
        Predicate<Player> afterPredicate = s -> {
            if(requestedPlayer.getAfter() == null ) return true;
            return s.getBirthday().after(requestedPlayer.getAfter());
        };
        Predicate<Player> beforePredicate = s -> {
            if(requestedPlayer.getBefore() == null) return true;
            return s.getBirthday().before(requestedPlayer.getBefore());
        };
        Predicate<Player> minExperiencePredicate = s -> {
            if(requestedPlayer.getMinExperience() == null) return true;
            return s.getExperience() >= requestedPlayer.getMinExperience();
        };
        Predicate<Player> maxExperiencePredicate = s -> {
            if(requestedPlayer.getMaxExperience() == null) return true;
            return s.getExperience() <= requestedPlayer.getMaxExperience();
        };
        Predicate<Player> minLevelPredicate = s -> {
            if(requestedPlayer.getMinLevel() == null) return true;
            return s.getLevel() >= requestedPlayer.getMinLevel();
        };
        Predicate<Player> maxLevelPredicate = s -> {
            if(requestedPlayer.getMaxLevel() == null) return true;
            return s.getLevel() <= requestedPlayer.getMaxLevel();
        };
        Predicate<Player> bannedPredicate = s -> {
            if(requestedPlayer.getBanned() == null) return true;
            return s.isBanned().equals(requestedPlayer.getBanned());
        };
        allPredicates.addAll(Arrays.asList(namePredicate, titlePredicate, racePredicate, professionPredicate, afterPredicate, beforePredicate, minExperiencePredicate, maxExperiencePredicate, minLevelPredicate, maxLevelPredicate, bannedPredicate));
        return allPredicates;
    }


    private Player mergePlayers(Player playerFromJSON, Player playerFromDB){
        Player resultPlayer = new Player();
        resultPlayer.setId(playerFromJSON.getId());
        resultPlayer.setName((playerFromJSON.getName() == null || playerFromJSON.getName().isEmpty()) ? playerFromDB.getName() : playerFromJSON.getName());
        resultPlayer.setTitle((playerFromJSON.getTitle() == null || playerFromJSON.getTitle().isEmpty()) ? playerFromDB.getTitle() : playerFromJSON.getTitle());
        resultPlayer.setRace(playerFromJSON.getRace() == null? playerFromDB.getRace() : playerFromJSON.getRace());
        resultPlayer.setProfession(playerFromJSON.getProfession() == null? playerFromDB.getProfession() : playerFromJSON.getProfession());
        resultPlayer.setBirthday(playerFromJSON.getBirthday() == null? playerFromDB.getBirthday() : playerFromJSON.getBirthday());
        resultPlayer.setBanned(playerFromJSON.isBanned() == null? playerFromDB.isBanned() : playerFromJSON.isBanned());
        resultPlayer.setExperience(playerFromJSON.getExperience() == null? playerFromDB.getExperience() : playerFromJSON.getExperience());
        return resultPlayer;
    }

    private void calculateLevelAndUntilNextLevel(Player player){
        int exp = player.getExperience();
        int level = (int)(Math.sqrt(2500+200*exp) - 50)/100;
        player.setLevel(level);
        player.setUntilNextLevel(50*(level+ 1) * (level + 2) - exp);
    }




}
