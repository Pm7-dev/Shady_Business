package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class JoinListener implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(plugin.getConfig().getBoolean("started")) {
            Nerd nerd = plugin.getNerd(e.getPlayer().getUniqueId());
            if(nerd == null) {
                Player p = e.getPlayer();
                Nerd newNerd = new Nerd();
                newNerd.setUuid(p.getUniqueId());
                newNerd.setName(p.getName());
                newNerd.setLives(4);
                newNerd.setRole(RoleType.VILLAGER);
                newNerd.setData(new HashMap<>());

                plugin.getNerds().add(newNerd);
            } else {
                if(nerd.getRole() != RoleType.TWINS) {
                    Player p = e.getPlayer();
                    p.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0d);
                    p.setHealth(20.0d);
                }
            }
        }
    }
}
