package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.UUID;

public class Twins implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    static FileConfiguration config = plugin.getConfig();

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if(!config.getBoolean("started")) {return;}
        if(!(e.getEntity() instanceof Player p)) { return; }
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) { return; }
        if(nerd.getRole() != RoleType.TWINS) { return; }

        syncHealth(nerd, e.getFinalDamage());
    }

    @EventHandler
    public void onPlayerHeal(EntityRegainHealthEvent e) {
        if(!config.getBoolean("started")) {return;}
        if(!(e.getEntity() instanceof Player p)) { return; }
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) { return; }
        if(nerd.getRole() != RoleType.TWINS) { return; }

        syncHealth(nerd, e.getAmount() * -1);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(!config.getBoolean("started")) {return;}
        Player p = e.getPlayer();
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) { return; }
        if(nerd.getRole() != RoleType.TWINS) { return; }

        syncHealth(nerd, 0.0d);
    }

    void syncHealth(Nerd damaged, Double damage) {

        HashMap<RoleData, Object> data = damaged.getData();

        // Figure out who the prime twin is and load the prime twin's data
        Nerd prime;
        Nerd second;
        Nerd mimic = null;
        if((boolean) data.get(RoleData.TWIN_IS_PRIME)) {
            prime = damaged;
            second = plugin.getNerd((String) data.get(RoleData.TWIN_PARTNER_NAME));
        } else if (data.get(RoleData.TWIN_MIMIC_NAME) == damaged.getName()) {
            mimic = damaged;
            prime = plugin.getNerd((String) data.get(RoleData.TWIN_PARTNER_NAME));
            second = plugin.getNerd((String) prime.getData().get(RoleData.TWIN_PARTNER_NAME));
        } else {
            second = damaged;
            prime = plugin.getNerd((String) data.get(RoleData.TWIN_PARTNER_NAME));
        }
        if(mimic == null && data.get(RoleData.TWIN_MIMIC_NAME) != null) {
            mimic = plugin.getNerd((String) data.get(RoleData.TWIN_MIMIC_NAME));
        }
        HashMap<RoleData, Object> primeData = prime.getData();

        // kguyit5dctvuyr
        Player damagedP = Bukkit.getPlayer(damaged.getUuid());

        if(damagedP.getNoDamageTicks() > 10) {
            if(damage > damagedP.getLastDamage()) {
                damage = damage - damagedP.getLastDamage();
            } else {
                return;
            }
        }

        // Calculate the new health and save it to data
        Double health = (Double) primeData.get(RoleData.TWIN_SHARED_HEALTH);
        health = health - damage;
        if(health < 0.0d) { health = 0.0d; }
        if(health > 30.0d) { health = 30.0d; }
        primeData.put(RoleData.TWIN_SHARED_HEALTH, health);

        // Get both twins as players
        Player primePlayer = Bukkit.getPlayer(prime.getUuid());
        Player secondPlayer = Bukkit.getPlayer(second.getUuid());
        Player mimicPlayer;
        if(mimic != null) {mimicPlayer = Bukkit.getPlayer(mimic.getUuid());} else {
            mimicPlayer = null;
        }

        if(damagedP == primePlayer) {
            if(secondPlayer != null && !secondPlayer.isDead()) {
                sendHealthChangePacket(secondPlayer, damage, health);
                if (damage >= 0) {
                    secondPlayer.playSound(secondPlayer, Sound.ENTITY_PLAYER_HURT, 1, 1);
                    secondPlayer.playHurtAnimation(0);
                    if (damagedP.getNoDamageTicks() <= 10) {
                        secondPlayer.setNoDamageTicks(20);
                        secondPlayer.setLastDamage(damage);
                    }
                }
                secondPlayer.setHealth(health);
            }
            if(mimicPlayer != null && !mimicPlayer.isDead()) {
                sendHealthChangePacket(mimicPlayer, damage, health);
                if (damage >= 0) {
                    mimicPlayer.playSound(mimicPlayer, Sound.ENTITY_PLAYER_HURT, 1, 1);
                    mimicPlayer.playHurtAnimation(0);
                    if (damagedP.getNoDamageTicks() <= 10) {
                        mimicPlayer.setNoDamageTicks(20);
                        mimicPlayer.setLastDamage(damage);
                    }
                }
                mimicPlayer.setHealth(health);
            }
        } else if (damagedP == secondPlayer) {
            if(primePlayer != null && !primePlayer.isDead()) {
                sendHealthChangePacket(primePlayer, damage, health);
                if (damage >= 0) {
                    primePlayer.playSound(primePlayer, Sound.ENTITY_PLAYER_HURT, 1, 1);
                    primePlayer.playHurtAnimation(0);
                    if (damagedP.getNoDamageTicks() <= 10) {
                        primePlayer.setNoDamageTicks(20);
                        primePlayer.setLastDamage(damage);
                    }
                }
                primePlayer.setHealth(health);
            }
            if(mimicPlayer != null && !mimicPlayer.isDead()) {
                sendHealthChangePacket(mimicPlayer, damage, health);
                if (damage >= 0) {
                    mimicPlayer.playSound(mimicPlayer, Sound.ENTITY_PLAYER_HURT, 1, 1);
                    mimicPlayer.playHurtAnimation(0);
                    if (damagedP.getNoDamageTicks() <= 10) {
                        mimicPlayer.setNoDamageTicks(20);
                        mimicPlayer.setLastDamage(damage);
                    }
                }
                mimicPlayer.setHealth(health);
            }
        } else if (damagedP == mimicPlayer) {
            if(secondPlayer != null && !secondPlayer.isDead()) {
                sendHealthChangePacket(secondPlayer, damage, health);
                if (damage >= 0) {
                    secondPlayer.playSound(secondPlayer, Sound.ENTITY_PLAYER_HURT, 1, 1);
                    secondPlayer.playHurtAnimation(0);
                    if (damagedP.getNoDamageTicks() <= 10) {
                        secondPlayer.setNoDamageTicks(20);
                        secondPlayer.setLastDamage(damage);
                    }
                }
                secondPlayer.setHealth(health);
            }
            if(primePlayer != null && !primePlayer.isDead()) {
                sendHealthChangePacket(primePlayer, damage, health);
                if (damage >= 0) {
                    primePlayer.playSound(primePlayer, Sound.ENTITY_PLAYER_HURT, 1, 1);
                    primePlayer.playHurtAnimation(0);
                    if (damagedP.getNoDamageTicks() <= 10) {
                        primePlayer.setNoDamageTicks(20);
                        primePlayer.setLastDamage(damage);
                    }
                }
                primePlayer.setHealth(health);
            }
        }

        Double finalHealth = health;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            Double currentHealth = (Double) prime.getData().get(RoleData.TWIN_SHARED_HEALTH);
            if(!primePlayer.isDead()) { primePlayer.setHealth(currentHealth); }
            if(!secondPlayer.isDead()) { secondPlayer.setHealth(currentHealth); }
            if(!mimicPlayer.isDead()) { mimicPlayer.setHealth(currentHealth); }

            if(finalHealth == 0.0d) { primeData.put(RoleData.TWIN_SHARED_HEALTH, 30.0d); }
        }, 1L);
    }

    // First time using NMS, this code just sends the packet to show the flashing hearts on the client before setting health the boring way
    void sendHealthChangePacket(Player p, Double damage, Double health) {
        if (0.5d <= damage || damage <= -0.5d) {
            EntityPlayer entitySecond = ((CraftPlayer) p).getHandle();
            PacketPlayOutUpdateHealth packetSecond = new PacketPlayOutUpdateHealth(health.floatValue(), entitySecond.gi().a, entitySecond.gi().b);
            PlayerConnection pcSecond = entitySecond.c;
            pcSecond.b(packetSecond);
        }
    }
}
