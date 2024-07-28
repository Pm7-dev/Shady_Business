package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class JoinListener implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    private ConfigurationSection config = plugin.getConfig();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(config.getBoolean("started")) {
            if(plugin.getNerd(e.getPlayer().getUniqueId()) == null) {
                Nerd nerd = new Nerd();
                nerd.setUuid(e.getPlayer().getUniqueId());
                nerd.setName(e.getPlayer().getName());
                nerd.setLives(4);
                nerd.setObjectiveCompleted(false);
                nerd.setRole(RoleType.VILLAGER);
                nerd.setData(new ArrayList<>());

                plugin.getNerds().add(nerd);
            }
        }
    }
}
