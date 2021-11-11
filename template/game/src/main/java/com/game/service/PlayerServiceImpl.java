package com.game.service;

import com.game.controller.PlayerOrder;
import com.game.entity.Player;
import com.game.entity.Profession;
import com.game.entity.Race;
import com.game.exception_handler.IncorrectPlayerArguments;
import com.game.exception_handler.NoSuchPlayerException;
import com.game.repository.PlayerDAO;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
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

    //todo добавить логгер

    @Override
    public List<Player> getAllPlayers(Map<String, String> params) {
        params.entrySet().forEach(System.out::println);
        int pageNumber = 0;
        int pageSize = 3;
        List<Player> allPlayersList = playerDAO.getAllPlayers();
        if(params == null) return allPlayersList.subList(0, 3);

        //System.out.println("метод гетАлл список до фильтрации");
        printList(allPlayersList);
        Predicate<Player> filter = filterByParams(params).stream().reduce(Predicate::and).orElse(x -> true);
        //фильтруем лист в соответствии с предикатами
        allPlayersList = allPlayersList.stream().filter(filter).collect(Collectors.toList());
        System.out.println("метод гетАлл список после фильтрации до сортировки");
        printList(allPlayersList);
        //проверяем в каком порядке должен быть отсрортирован лист
        Function<List<Player>, List<Player>> sortForList = s -> {
            if(!params.containsKey("order")) {
                return s.stream().sorted((x, y) -> (Long.compare(x.getId(), y.getId()))).collect(Collectors.toList());
            } else {
                PlayerOrder playerOrder = PlayerOrder.valueOf(params.get("order"));
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
        //System.out.println("метод гетАлл список после фильтрации и сортировки");
        //printList(allPlayersList);
        if(params.containsKey("pageNumber")){
            pageNumber = Integer.parseInt(params.get("pageNumber"));
        }
        if(params.containsKey("pageSize")){
            pageSize = Integer.parseInt(params.get("pageSize"));
        }
        int startOfSublist = pageNumber*pageSize;
        int endOfSublist = (pageNumber*pageSize) + pageSize;
        if(allPlayersList.size() < endOfSublist){
            endOfSublist = startOfSublist + allPlayersList.size() - startOfSublist;
        }
        allPlayersList = allPlayersList.subList(startOfSublist, endOfSublist);
        System.out.println("метод гетАлл список после Пагинации");
        printList(allPlayersList);
        return allPlayersList;


    }

    private void printList(List<Player> list){
        list.stream().map(s -> s.getName() + " ").forEach(System.out::print);
        System.out.println();
    }

    @Override
    public int getCountOfPlayers(Map<String, String> params) {
        if (params == null) return playerDAO.getAllPlayers().size();
        List<Player> allPlayersList = playerDAO.getAllPlayers();
        Predicate<Player> filter = filterByParams(params).stream().reduce(Predicate::and).orElse(x -> true);
        allPlayersList = allPlayersList.stream().filter(filter).collect(Collectors.toList());
        return allPlayersList.size();
    }

    @Override
    public Player createPlayer(Player player) {
        isPlayerValid(player);
        calculateLevelAndUntilNextLevel(player);
        return playerDAO.createPlayer(player);
    }

    @Override
    public Player updatePlayer(long id, Player player) {
        if(id <= 0) throw new IncorrectPlayerArguments("incorrect id");
        checkExperienceOfPlayer(player);
        checkBirthdayOfPlayer(player);
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


    private void isPlayerValid(Player player){
        if(player.getName() == null || player.getName().length() > 12) {
            throw new IncorrectPlayerArguments("incorrect name");
        }
        if(player.getTitle()== null || player.getTitle().length() > 30) {
            throw new IncorrectPlayerArguments("incorrect title");
        }
        if(player.getName().length() == 0 || player.getName() == "") {
            throw new IncorrectPlayerArguments("incorrect name length or title length");
        }
        if(player.getProfession() == null) {
            throw new IncorrectPlayerArguments("incorrect profession");
        }
        if(player.getRace() == null) {
            throw new IncorrectPlayerArguments("incorrect race");
        }
        checkExperienceOfPlayer(player);
        checkBirthdayOfPlayer(player);
    }

    private void checkExperienceOfPlayer (Player player){
        if(player.getExperience() == null) throw new IncorrectPlayerArguments("incorrect experience");
        if( player.getExperience() <0 || player.getExperience() > 10_000_000L) {
            throw new IncorrectPlayerArguments("incorrect experience");
        }
    }

    private void checkBirthdayOfPlayer(Player player){
        Date playerBirthday = player.getBirthday();
        if(playerBirthday == null)  throw new IncorrectPlayerArguments("incorrect player birthday");
        Date afterDate = new Date(1100, 01 ,01);
        Date beforeDate = new Date(100, 01 ,01);
        if(playerBirthday.after(afterDate) || playerBirthday.before(beforeDate))  {
            throw new IncorrectPlayerArguments("incorrect player birthday");
        }
    }

    private List<Predicate<Player>> filterByParams(Map<String, String> params){
        List<Predicate<Player>> allPredicates = new ArrayList<>();

        Predicate<Player> namePredicate = s -> {
            if(!params.containsKey("name") || params.get("name") == "") return true;
            return s.getName().contains(params.get("name"));
        };
        Predicate<Player> titlePredicate = s -> {
            if(!params.containsKey("title") || params.get("title") == "") return true;
            return s.getTitle().contains(params.get("title"));
        };

        Predicate<Player> racePredicate = s -> {
            if(!params.containsKey("race")) return true;
            return s.getRace().equals(Race.valueOf(params.get("race")));
        };
        Predicate<Player> professionPredicate = s -> {
            if(!params.containsKey("profession")) return true;
            return s.getProfession().equals(Profession.valueOf(params.get("profession")));
        };
        Predicate<Player> afterPredicate = s -> {
            if(!params.containsKey("after") || params.get("after") == "") return true;
            return s.getBirthday().after(new Date(Long.parseLong(params.get("after"))));
        };
        Predicate<Player> beforePredicate = s -> {
            if(!params.containsKey("before") || params.get("before") == "") return true;
            return s.getBirthday().before(new Date(Long.parseLong(params.get("before"))));
        };
        Predicate<Player> minExperiencePredicate = s -> {
            if(!params.containsKey("minExperience") || params.get("minExperience") == "") return true;
            return s.getExperience() >= Integer.parseInt(params.get("minExperience"));
        };
        Predicate<Player> maxExperiencePredicate = s -> {
            if(!params.containsKey("maxExperience") || params.get("maxExperience") == "") return true;
            return s.getExperience() <= Integer.parseInt(params.get("maxExperience"));
        };
        Predicate<Player> minLevelPredicate = s -> {
            if(!params.containsKey("minLevel") || params.get("minLevel") == "") return true;
            return s.getLevel() >= Integer.parseInt(params.get("minLevel"));
        };
        Predicate<Player> maxLevelPredicate = s -> {
            if(!params.containsKey("maxLevel") || params.get("maxLevel") == "") return true;
            return s.getLevel() <= Integer.parseInt(params.get("maxLevel"));
        };
        Predicate<Player> bannedPredicate = s -> {
            if(!params.containsKey("banned")) return true;
            return !s.isBanned(); //todo доделать фильтрацию
        };
        allPredicates.addAll(Arrays.asList(namePredicate, titlePredicate, racePredicate, professionPredicate, afterPredicate, beforePredicate, minExperiencePredicate, maxExperiencePredicate, minLevelPredicate, maxLevelPredicate, bannedPredicate));
        return allPredicates;
    }


    private Player mergePlayers(Player playerFromJSON, Player playerFromDB){
        Player resultPlayer = new Player();
        resultPlayer.setId(playerFromJSON.getId());
        resultPlayer.setName(playerFromJSON.getName() == null? playerFromDB.getName() : playerFromJSON.getName());
        resultPlayer.setTitle(playerFromJSON.getTitle() == null? playerFromDB.getTitle() : playerFromJSON.getTitle());
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
