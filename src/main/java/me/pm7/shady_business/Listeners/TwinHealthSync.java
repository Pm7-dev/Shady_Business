package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class TwinHealthSync implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(!(e.getEntity() instanceof Player)) { return; }
        if(e.getCause() == DamageCause.CUSTOM) { return; }
        Player p = (Player) e.getEntity();
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd.getRole() != RoleType.TWINS) { return; }
        List<Object> data = nerd.getData();
        if(data.get(1) == null) {
            System.out.println("error - twin is not set up correctly?");
            return;
        }

        Nerd prime;
        Nerd second;
        if((boolean) data.get(1)) {
            prime = nerd;
            second = plugin.getNerd((String) data.get(0));
        } else {
            prime = plugin.getNerd((String) data.get(0));
            second = nerd;
        }

        syncHealth(prime, second, e.getFinalDamage());
    }

    @EventHandler
    public void onPlayerHeal(EntityRegainHealthEvent e) {
        if(!(e.getEntity() instanceof Player)) { return; }
        Player p = (Player) e.getEntity();
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd.getRole() != RoleType.TWINS) { return; }
        List<Object> data = nerd.getData();
        if(data.get(1) == null) {
            System.out.println("error - twin is not set up correctly?");
            return;
        }

        Nerd prime;
        Nerd second;
        if((boolean) data.get(1)) {
            prime = nerd;
            second = plugin.getNerd((String) data.get(0));
        } else {
            prime = plugin.getNerd((String) data.get(0));
            second = nerd;
        }

        syncHealth(prime, second, e.getAmount() * -1);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd.getRole() != RoleType.TWINS) { return; }
        List<Object> data = nerd.getData();
        if(data.get(1) == null) {
            System.out.println("error - twin is not set up correctly?");
            return;
        }

        Nerd prime;
        Nerd second;
        if((boolean) data.get(1)) {
            prime = nerd;
            second = plugin.getNerd((String) data.get(0));
        } else {
            prime = plugin.getNerd((String) data.get(0));
            second = nerd;
        }

        syncHealth(prime, second, 0.0d);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        Player p = e.getPlayer();
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd.getRole() != RoleType.TWINS) { return; }
        List<Object> data = nerd.getData();
        if(data.get(2) == null) {
            System.out.println("error - twin is not set up correctly?");
            return;
        }

        Nerd prime;
        Nerd second;
        if((boolean) data.get(1)) {
            prime = nerd;
            second = plugin.getNerd((String) data.get(0));
        } else {
            prime = plugin.getNerd((String) data.get(0));
            second = nerd;
        }

        List<Object> primeData = prime.getData();
        if((Double) primeData.get(2) <= 0.0d) { primeData.set(2, 30.0d); }
        syncHealth(prime, second, 0.0d);
    }

    void syncHealth(Nerd prime, Nerd second, Double damage) {
        List<Object> data = prime.getData();
        Double health = (Double) data.get(2);
        health = health - damage;
        if(health < 0.0d) { health = 0.0d; }
        if(health > 30.0d) { health = 30.0d ; }
        data.set(2, health);
        prime.setData(data);
        plugin.saveData();

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Double currentHealth = (Double) prime.getData().get(2);
            Player primePlayer = Bukkit.getPlayer(prime.getUuid());
            Player secondPlayer = Bukkit.getPlayer(second.getUuid());

            Double primeHealth = primePlayer.getHealth();
            if(primeHealth > currentHealth) { primePlayer.damage(primeHealth - currentHealth); }
            else { primePlayer.setHealth(currentHealth); }

            Double secondHealth = secondPlayer.getHealth();
            if(secondHealth > currentHealth) { secondPlayer.damage(secondHealth - currentHealth); }
            else { secondPlayer.setHealth(currentHealth); }
        }, 1L);
    }
}
