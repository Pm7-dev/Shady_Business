package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeathListener implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    static FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {

        // No reason to do anything if we're not started yet
        if(!config.getBoolean("started")) { return; }

        // Death message generation
        List<String> names = new ArrayList<>();
        for(Player p: Bukkit.getOnlinePlayers()) {names.add(p.getName());}
        List<String> words = new ArrayList<>(Arrays.asList(e.getDeathMessage().split(" ")));
        for(int i=0; i<words.size(); i++) {
            if(names.contains(words.get(i))) {
                words.set(i, Bukkit.getPlayer(words.get(i)).getDisplayName());
            }
        }

        // Get the dead nerd
        Player p = e.getEntity();
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) { System.out.println("errror, nerd was null in death"); return; }

        // Sound effects
        if(nerd.getLives() < 1) {
            for(Player plr : Bukkit.getOnlinePlayers()) {plr.playSound(plr, "death.final", 1, 1);}
            p.setRespawnLocation(p.getLocation());
            p.sendTitle(ChatColor.RED + "You are out of lives!", "", 10, 70, 20);
        }
        else { for(Player plr : Bukkit.getOnlinePlayers()) {plr.playSound(plr, "death.nonfinal", 1, 1);} }

        // booger.
        Player pk = p.getKiller(); // i LOVE having inconsistent naming conventions
        if(pk != null) {
            Nerd killer = plugin.getNerd(pk.getUniqueId());
            if(killer != null) {
                if (killer.getRole() == RoleType.BOOGEYMAN) {
                    if (nerd.getRole() == RoleType.VICTIM) {
                        pk.sendTitle(ChatColor.RED + "You got tricked lol", "", 10, 70, 20);
                        nerd.setObjectiveCompleted(true);
                        nerd.addLife();
                        p.sendTitle(ChatColor.GREEN + "You gained a life!", "", 10, 70, 20);
                        return;
                    } else {
                        pk.sendTitle(ChatColor.GREEN + "You have been cured!", "", 10, 70, 20);
                        killer.setObjectiveCompleted(true);
                    }
                }
            }
        }

        nerd.removeLife();

        //fjjjjjjjjjjkl gonk
        plugin.saveData();

    }
}
