package com.game.entity;

import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name = "player")
public class Player {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NonNull
    @Column(name = "name")
    private String name;

    @Column(name = "title")
    private String title;

    @Column(name = "race")
    private Race race;

    @Column(name = "profession")
    private Profession profession;

    @Column(name = "experience")
    private int experience;

    @Column(name = "level")
    private int level;

    @Column(name = "unitNextLevel")
    private int unitNextLevel;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "banned")
    private boolean banned;


    public Player() {
    }

    public Player(String name, String title, Race race, Profession profession, int experience, int level, int unitNextLevel, Date birthday, boolean banned) {
        this.name = name;
        this.title = title;
        this.race = race;
        this.profession = profession;
        this.experience = experience;
        this.level = level;
        this.unitNextLevel = unitNextLevel;
        this.birthday = birthday;
        this.banned = banned;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Race getRace() {
        return race;
    }

    public void setRace(Race race) {
        this.race = race;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getUnitNextLevel() {
        return unitNextLevel;
    }

    public void setUnitNextLevel(int unitNextLevel) {
        this.unitNextLevel = unitNextLevel;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
