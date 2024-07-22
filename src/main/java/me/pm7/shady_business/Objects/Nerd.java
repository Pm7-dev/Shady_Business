package me.pm7.shady_business.Objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Nerd {
    private Player player;
    private String name;
    private int lives;
    private boolean objectiveCompleted = false;
    private RoleType role = RoleType.VILLAGER;
    private List<Object> data = new ArrayList<>();

    public void setPlayer(Player p) { this.player = p; }
    public Player getPlayer() {return this.player;}

    public void setName(String name) { this.name = name;}
    public String getName() { return name; }

    public void setLives(int newLives) {
        this.lives = newLives;
    }
    public void addLife() {
        this.lives++;
    }
    public void removeLife() {
        this.lives--;
    }
    public int getLives() { return this.lives; }

    public void setObjectiveCompleted(boolean completed) { this.objectiveCompleted = completed; }
    public boolean getObjectiveCompleted() { return this.objectiveCompleted; }

    public void setRole(RoleType type) { this.role = type;}
    public RoleType getRole() { return this.role; }

    public void setData(List<Object> newData) { this.data = newData; }
    public List<Object> getData() { return this.data; }

    @Override
    public String toString() { return " \nName: " + this.getName() + "\nLives: " + this.getLives() + "\nobjectiveCompleted: " + objectiveCompleted + "\nRole: " + role + "\nOther Data: " + this.getData(); }

}
