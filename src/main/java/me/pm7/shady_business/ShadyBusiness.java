package me.pm7.shady_business;

import me.pm7.shady_business.Objects.Nerd;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class ShadyBusiness extends JavaPlugin {
    private static ShadyBusiness plugin;
    FileConfiguration config = getConfig();

    @Override
    public void onEnable() {
        plugin = this;
        System.out.println("OwO wooks wike someonye's up t-to some shady businyess... >w<");

        getConfig().options().copyDefaults();
        saveDefaultConfig();

        registerListeners();

        loadConfigData();
    }

    private void registerListeners() {

    }

    private void loadConfigData() {
        if(config.getConfigurationSection("nerds") == null) {config.createSection("nerds"); return; }
        nerdList = (List<Nerd>) config.get("nerds");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Nerd getNerd(Player p) {
        for (Nerd nerd : nerdList) {
            if(nerd.getName().equals(p.getName())) { return nerd; }
        }
        System.out.println("Error! Nerd not found");
        return null;
    }

    private List<Nerd> nerdList = new ArrayList<>();
    public List<Nerd> getNerds() {return nerdList;}

    public static ShadyBusiness getPlugin() { return plugin; }
}
