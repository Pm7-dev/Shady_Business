package me.pm7.shady_business.Objects;

import me.pm7.shady_business.ScoreboardManager;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Nerd implements ConfigurationSerializable {


        //I called this object "Nerd" Because "Player" is already a class in spigot and the word Townsperson sucks.

    //also it's my crappy code and I can do what I want >:3

    private UUID uuid;
    private String name;
    private int lives;
    private boolean hadRoleLastSession = false;
    private RoleType role = RoleType.VILLAGER;
    private HashMap<RoleData, Object> data = new HashMap<>();

    public void setUuid(UUID newID) { this.uuid = newID; }
    public UUID getUuid() {return this.uuid;}

    public void setName(String name) { this.name = name;}
    public String getName() { return name; }

    public void setLives(int newLives) {
        this.lives = newLives;
        ScoreboardManager.UpdatePlayerScore(this);
    }
    public void addLife() {
        this.lives++;
        ScoreboardManager.UpdatePlayerScore(this);
    }
    public void addLife(Location respawnLoc) {
        this.lives++;
        Player p = Bukkit.getPlayer(this.uuid);
        if(p == null) {return;}
        p.teleport(respawnLoc);
        p.setGameMode(GameMode.SURVIVAL);

        ScoreboardManager.UpdatePlayerScore(this);
    }
    public void removeLife() {
        this.lives--;
        ScoreboardManager.UpdatePlayerScore(this);

        if(this.lives < 1) {
            Player p = Bukkit.getPlayer(this.uuid);
            if(p == null) { return; }

            // Gather the contents of the player's inventory and drop them
            Inventory inv = p.getInventory();
            Location loc = p.getLocation();
            World world = loc.getWorld();
            double power = 0.2D;
            for(ItemStack item : inv.getContents()) {
                if(item != null) {
                    double xVel = -power + (Math.random() * (power*2));
                    double zVel = -power + (Math.random() * (power*2));
                    System.out.println("xVel: " + xVel + " zVel: " + zVel);
                    Entity dropped = world.dropItem(loc, item);
                    dropped.setVelocity(new Vector(xVel, 0.3, zVel));
                }
            }
            inv.clear();

            // die
            p.setGameMode(GameMode.SPECTATOR);
            p.sendTitle(ChatColor.RED + "You are out of lives!", "", 10, 70, 20);
        }
    }
    public int getLives() { return this.lives; }


    public void setHadRoleLastSession(boolean hadRole) { this.hadRoleLastSession = hadRole; }
    public boolean getHadRoleLastSession() { return this.hadRoleLastSession; }

    public void setRole(RoleType type) { this.role = type;}
    public RoleType getRole() { return this.role; }

    public void setData(HashMap<RoleData, Object> newData) { this.data = newData; }
    public HashMap<RoleData, Object> getData() { return this.data; }

    @Override
    public String toString() { return " \nName: " + this.getName() + "\nLives: " + this.getLives() + "\nRole: " + role + "\nOther Data: " + this.getData(); }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serializedData = new HashMap<>();
        serializedData.put("name", this.name);
        serializedData.put("uuid", this.uuid.toString());
        serializedData.put("lives", this.lives);
        serializedData.put("role", this.role.toString()); //.valueOf to get back
        serializedData.put("data", this.data);
        serializedData.put("hadRoleLastSession", this.hadRoleLastSession);
        return serializedData;
    }

    @NotNull
    public static Nerd deserialize(ConfigurationSection serializedData) {
        if (serializedData == null) {
            System.out.println("Serialized data is null! Cannot load Nerd");
            return new Nerd();
        }

        Nerd nerd = new Nerd();
        nerd.name = serializedData.getString("name");
        nerd.uuid = UUID.fromString(serializedData.getString("uuid"));
        nerd.lives = serializedData.getInt("lives");
        nerd.role = RoleType.valueOf(serializedData.getString("role"));
        ConfigurationSection dataSection = serializedData.getConfigurationSection("data");
        HashMap<RoleData, Object> data = new HashMap<>();
        for (String key : dataSection.getKeys(false)) {
            data.put(RoleData.valueOf(key), dataSection.get(key));
        }
        nerd.data = data;
        nerd.hadRoleLastSession = serializedData.getBoolean("hadRoleLastSession");
        return nerd;
    }

}
