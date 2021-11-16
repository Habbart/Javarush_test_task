package com.game.service;

import com.game.entity.Player;
import com.game.exception_handler.IncorrectPlayerArguments;

import java.util.Date;

public class PlayerPojoCheck {


    public static void validatePLayerPOJO(PlayerPOJO playerPOJO) {
    }

    public static void isPlayerValid(Player player){
        if(player.getName() == null || player.getName().length() > 12) {
            throw new IncorrectPlayerArguments("incorrect name");
        }
        if(player.getTitle()== null || player.getTitle().length() > 30) {
            throw new IncorrectPlayerArguments("incorrect title");
        }
        if(player.getName().length() == 0 || player.getName().equals("")) {
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

    public static void checkExperienceOfPlayer (Player player){
        if(player.getExperience() == null) return;
        if( player.getExperience() <0 || player.getExperience() > 10_000_000L) {
            throw new IncorrectPlayerArguments("incorrect experience");
        }
    }

    public static void checkBirthdayOfPlayer(Player player){
        Date playerBirthday = player.getBirthday();
        if(playerBirthday == null)  return;
        Date afterDate = new Date(1100, 01 ,01);
        Date beforeDate = new Date(100, 01 ,01);
        if(playerBirthday.after(afterDate) || playerBirthday.before(beforeDate))  {
            throw new IncorrectPlayerArguments("incorrect player birthday");
        }
    }
}
