package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.exception_handler.IncorrectPlayerArguments;
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
    public List<Player> getAllPlayers(PlayerPOJO playerPOJO) {
        PlayerPojoCheck.validatePLayerPOJO(playerPOJO);
        int pageNumber = 0;
        int pageSize = 3;
        List<Player> allPlayersList = playerDAO.getAllPlayers();
        Predicate<Player> filter = filterByParamsFromPlayerPOJO(playerPOJO).stream().reduce(Predicate::and).orElse(x -> true);
        //фильтруем лист в соответствии с предикатами
        allPlayersList = allPlayersList.stream().filter(filter).collect(Collectors.toList());
        //проверяем в каком порядке должен быть отсрортирован лист
        Function<List<Player>, List<Player>> sortForList = s -> {
            if(playerPOJO.getOrder() == null) {
                return s.stream().sorted((x, y) -> (Long.compare(x.getId(), y.getId()))).collect(Collectors.toList());
            } else {
                PlayerOrder playerOrder = playerPOJO.getOrder();
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
        if(playerPOJO.getPageNumber() != null){
            pageNumber = playerPOJO.getPageNumber();
        }
        if(playerPOJO.getPageSize() != null){
            pageSize = playerPOJO.getPageSize();
        }
        int startOfSublist = pageNumber*pageSize;
        int endOfSublist = (pageNumber*pageSize) + pageSize;
        if(allPlayersList.size() < endOfSublist){
            endOfSublist = startOfSublist + allPlayersList.size() - startOfSublist;
        }
        allPlayersList = allPlayersList.subList(startOfSublist, endOfSublist);

        return allPlayersList;


    }

    @Override
    public int getCountOfPlayers(PlayerPOJO playerPOJO) {
        PlayerPojoCheck.validatePLayerPOJO(playerPOJO);
        if (playerPOJO.isEmpty()) return playerDAO.getAllPlayers().size();
        List<Player> allPlayersList = playerDAO.getAllPlayers();
        Predicate<Player> filter = filterByParamsFromPlayerPOJO(playerPOJO).stream().reduce(Predicate::and).orElse(x -> true);
        allPlayersList = allPlayersList.stream().filter(filter).collect(Collectors.toList());
        return allPlayersList.size();
    }

    @Override
    public Player createPlayer(Player player) {
        PlayerPojoCheck.isPlayerValid(player);
        calculateLevelAndUntilNextLevel(player);
        return playerDAO.createPlayer(player);
    }

    @Override
    public Player updatePlayer(long id, Player player) {
        if(id <= 0) throw new IncorrectPlayerArguments("incorrect id");
        PlayerPojoCheck.checkExperienceOfPlayer(player);
        PlayerPojoCheck.checkBirthdayOfPlayer(player);
        player.setId(id);
        Player playerFromDB = playerDAO.getPlayerById(id);
        if(playerFromDB == null) throw new NoSuchPlayerException(String.format("player with this id: %d not found", id));
        Player playerForUpdate = mergePlayers(player, playerFromDB);
        calculateLevelAndUntilNextLevel(playerForUpdate);
        return playerDAO.createPlayer(playerForUpdate);
    }

    @Override
    public Player getPlayerById(long id) {
        if(id <= 0 ){
            throw new IncorrectPlayerArguments("incorrect id");
        }
        if(playerDAO.getPlayerById(id) == null) throw new NoSuchPlayerException(String.format("player with this id: %d not found", id));
        return playerDAO.getPlayerById(id);
    }

    @Override
    public void deletePlayer(long id) {
        if(id <= 0){
            throw new IncorrectPlayerArguments("incorrect id");
        }
        if(getPlayerById(id) == null ){
            throw new NoSuchPlayerException(String.format("player with this id: %d not found", id));
        }
        playerDAO.deletePlayer(id);
    }


    private List<Predicate<Player>> filterByParamsFromPlayerPOJO(PlayerPOJO playerPOJO){
        List<Predicate<Player>> allPredicates = new ArrayList<>();

        Predicate<Player> namePredicate = s -> {
            if(playerPOJO.getName() == null || !playerPOJO.getName().isEmpty()) return true;
            return s.getName().contains(playerPOJO.getName());
        };
        Predicate<Player> titlePredicate = s -> {
            if(playerPOJO.getTitle() == null || !playerPOJO.getTitle().isEmpty()) return true;
            return s.getName().contains(playerPOJO.getTitle());
        };

        Predicate<Player> racePredicate = s -> {
            if(playerPOJO.getRace() == null) return true;
            return s.getRace().equals(playerPOJO.getRace());
        };
        Predicate<Player> professionPredicate = s -> {
            if(playerPOJO.getProfession() == null) return true;
            return s.getProfession().equals(playerPOJO.getProfession());
        };
        Predicate<Player> afterPredicate = s -> {
            if(playerPOJO.getAfter() == null ) return true;
            return s.getBirthday().after(playerPOJO.getAfter());
        };
        Predicate<Player> beforePredicate = s -> {
            if(playerPOJO.getBefore() == null) return true;
            return s.getBirthday().before(playerPOJO.getBefore());
        };
        Predicate<Player> minExperiencePredicate = s -> {
            if(playerPOJO.getMinExperience() == null) return true;
            return s.getExperience() >= playerPOJO.getMinExperience();
        };
        Predicate<Player> maxExperiencePredicate = s -> {
            if(playerPOJO.getMaxExperience() == null) return true;
            return s.getExperience() <= playerPOJO.getMaxExperience();
        };
        Predicate<Player> minLevelPredicate = s -> {
            if(playerPOJO.getMinLevel() == null) return true;
            return s.getLevel() >= playerPOJO.getMinLevel();
        };
        Predicate<Player> maxLevelPredicate = s -> {
            if(playerPOJO.getMaxLevel() == null) return true;
            return s.getLevel() <= playerPOJO.getMaxLevel();
        };
        Predicate<Player> bannedPredicate = s -> {
            if(playerPOJO.getBanned() == null) return true;
            return s.isBanned().equals(playerPOJO.getBanned());
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
