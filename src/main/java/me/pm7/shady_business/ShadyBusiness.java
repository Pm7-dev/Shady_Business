package me.pm7.shady_business;

import me.pm7.shady_business.Commands.*;
import me.pm7.shady_business.Listeners.*;
import me.pm7.shady_business.Objects.Nerd;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class ShadyBusiness extends JavaPlugin {
    private static ShadyBusiness plugin;
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        System.out.println("Maybe the real shade was the businesses we made along the way...");

        plugin = this;

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        registerListeners();

        loadConfigData();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::saveData, 0L, 6000L);
    }

    private void registerListeners() {
        ConfigurationSerialization.registerClass(Nerd.class);
        getServer().getPluginManager().registerEvents(new Twins(), this);
        getServer().getPluginManager().registerEvents(new DeathListener(), this);
        getServer().getPluginManager().registerEvents(new JoinListener(), this);
        getServer().getPluginManager().registerEvents(new ScoreboardManager(), this);
        getServer().getPluginManager().registerEvents(new Investigator(), this);
        getServer().getPluginManager().registerEvents(new Mimic(), this);
        getServer().getPluginManager().registerEvents(new Transporter(), this);
        this.getCommand("start").setExecutor(new start());
        this.getCommand("endsession").setExecutor(new endsession());
        this.getCommand("info").setExecutor(new info());
        this.getCommand("givelife").setExecutor(new givelife());
        this.getCommand("test").setExecutor(new test());

        System.out.println(":3 wistenyews and commands wegistewed >w<");
    }

    private void loadConfigData() {
        ConfigurationSection nerdSection = config.getConfigurationSection("nerds");
        if(nerdSection == null) {
            config.createSection("nerds");
            saveConfig();
            return;
        }

        for (String newNerd : nerdSection.getKeys(false)) {
            ConfigurationSection section = nerdSection.getConfigurationSection(newNerd);
            if(section == null) { System.out.println("idk how this happened"); return; }

            Nerd nerd = Nerd.deserialize(section);
            nerdList.add(nerd);
        }

        System.out.println("configuwation data woaded OwO~");
    }

    @Override
    public void onDisable() {
        saveData();
        System.out.println("bye bye....");
    }

    public Nerd getNerd(UUID uuid) {
        if(!config.getBoolean("started")) { return null; }
        for (Nerd nerd : nerdList) {
            if(nerd.getUuid().equals(uuid)) { return nerd; }
        }
        System.out.println("Error! Nerd not found");
        return null;
    }
    public Nerd getNerd(String name) {
        if(!config.getBoolean("started")) { return null; }
        for (Nerd nerd : nerdList) {
            if(nerd.getName().equals(name)) { return nerd; }
        }
        System.out.println("Error! Nerd not found");
        return null;
    }

    private List<Nerd> nerdList = new ArrayList<>();
    public List<Nerd> getNerds() {return nerdList;}
    public void saveData() {
        System.out.println("Saving plugin data");
        ConfigurationSection nerdsSection = config.createSection("nerds");
        for (Nerd nerd : nerdList) {
            ConfigurationSection nerdSection = nerdsSection.createSection(nerd.getName());
            for (Map.Entry<String, Object> entry : nerd.serialize().entrySet()) {
                if(!(entry.getValue() instanceof HashMap<?, ?> map)) {
                    nerdSection.set(entry.getKey(), entry.getValue());
                } else {
                    ConfigurationSection dataSection = nerdSection.createSection("data");
                    for (Map.Entry<?, ?> entry1 : map.entrySet()) {
                        dataSection.set((entry1.getKey()).toString(), entry1.getValue());
                    }
                }
            }
        }
        saveConfig();
    }

    public static ShadyBusiness getPlugin() { return plugin; }
}
