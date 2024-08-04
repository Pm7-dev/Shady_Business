package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
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

        // Get the dead nerd
        Player p = e.getEntity();
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) { System.out.println("errror, nerd was null in death"); return; }

        // Break the twin health link if one of them goes to red
        if(nerd.getRole() == RoleType.TWINS && nerd.getLives() - 1 <= 1) {
            for(Nerd n : plugin.getNerds()) {
                if(n.getRole() == RoleType.TWINS) {
                    n.setRole(RoleType.VILLAGER);
                    Player np = Bukkit.getPlayer(n.getUuid());
                    if(np != null) {
                        np.sendMessage(ChatColor.YELLOW + nerd.getName() + " has died, and only has one life left! Your health link is now broken.");
                        np.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0d);
                        np.setHealth(20.0d);
                    }
                }
            }
        }

        // booger.
        Player pk = p.getKiller(); // i LOVE having inconsistent naming conventions
        if(pk != null) {
            Nerd killer = plugin.getNerd(pk.getUniqueId());
            if(killer != null) {
                if (killer.getRole() == RoleType.BOOGEYMAN && killer.getLives() > 1) {
                    if(nerd.getLives() > 1) {
                        if (nerd.getRole() == RoleType.VICTIM && !(boolean) nerd.getData().get(RoleData.VICTIM_COMPLETED)) {
                            pk.sendTitle(ChatColor.RED + "You got tricked lol", "", 10, 70, 20);
                            nerd.getData().put(RoleData.VICTIM_COMPLETED, true);
                            nerd.addLife();
                            p.sendTitle(ChatColor.GREEN + "You gained a life!", "", 10, 70, 20);

                            plugin.saveData();
                            return;
                        } else {
                            pk.sendTitle(ChatColor.GREEN + "You have been cured!", "", 10, 70, 20);
                            nerd.getData().put(RoleData.BOOGEYMAN_CURED, true);
                        }
                    }
                }
            }
        }

        nerd.removeLife();

        //fjjjjjjjjjjkl gonk
        plugin.saveData();

        // Death message generation
        List<String> names = new ArrayList<>();
        for(Player i: Bukkit.getOnlinePlayers()) {names.add(i.getName());}
        List<String> words = new ArrayList<>(Arrays.asList(e.getDeathMessage().split(" ")));
        for(int i=0; i<words.size(); i++) {
            if(names.contains(words.get(i))) {
                words.set(i, Bukkit.getPlayer(words.get(i)).getDisplayName());
            }
        }

        // Sound effects
        if(nerd.getLives() < 1) {
            for(Player plr : Bukkit.getOnlinePlayers()) {plr.playSound(plr, "death.final", 1, 1);}
            p.setRespawnLocation(p.getLocation());
            p.sendTitle(ChatColor.RED + "You are out of lives!", "", 10, 70, 20);
        }
        else { for(Player plr : Bukkit.getOnlinePlayers()) {plr.playSound(plr, "death.nonfinal", 1, 1);} }
    }
}