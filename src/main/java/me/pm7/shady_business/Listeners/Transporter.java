package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.HashMap;

public class Transporter implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent e) {
        if(!e.getHand().equals(EquipmentSlot.HAND)) { return; }
        if(!(e.getRightClicked() instanceof Player pClicked)) { return; }
        Player p = e.getPlayer();
        if(!p.isSneaking()) { return; }
        if(p.getItemInUse() != null) { return; }
        Nerd nerd = plugin.getNerd(p.getName());
        if(nerd == null) { return; }
        if(nerd.getRole() != RoleType.TRANSPORTER) { return; }
        if(nerd.getLives() <= 1) { return; }
        Nerd clicked = plugin.getNerd(pClicked.getName());
        if(clicked == null) { return; }
        HashMap<RoleData, Object> data = nerd.getData();
        int teleportsLeft = (int) data.get(RoleData.TRANSPORTER_TELEPORTS_LEFT);
        if(teleportsLeft <= 0) { return; }

        if(data.get(RoleData.TRANSPORTER_SELECTED_PLAYER) == null) {
            data.put(RoleData.TRANSPORTER_SELECTED_PLAYER, clicked.getName());

            p.playSound(p, Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, 2, 2);
            p.sendMessage(ChatColor.AQUA + "Selected " + clicked.getName());
        } else {
            if(data.get(RoleData.TRANSPORTER_SELECTED_PLAYER) == clicked.getName()) {
                data.put(RoleData.TRANSPORTER_SELECTED_PLAYER, null);

                p.playSound(p, Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_OFF, 2, 2);
                p.sendMessage(ChatColor.AQUA + "Deselected " + clicked.getName());
            } else {
                Player saved = Bukkit.getPlayer((String) data.get(RoleData.TRANSPORTER_SELECTED_PLAYER));
                if(saved == null) {
                    p.sendMessage(ChatColor.AQUA + (String) data.get(RoleData.TRANSPORTER_SELECTED_PLAYER) + " is offline, and will be replaced with this player.");
                    data.put(RoleData.TRANSPORTER_SELECTED_PLAYER, clicked.getName());

                    p.playSound(p, Sound.BLOCK_BAMBOO_WOOD_BUTTON_CLICK_ON, 2, 2);
                    p.sendMessage(ChatColor.AQUA + "Selected " + clicked.getName());
                    return;
                }

                // actually do the teleportation
                Location savedPLoc = saved.getLocation();
                Location clickedPLoc = pClicked.getLocation();

                saved.teleport(clickedPLoc);
                pClicked.teleport(savedPLoc);

                saved.sendMessage(ChatColor.AQUA + "Whoosh!");
                pClicked.sendMessage(ChatColor.AQUA + "Whoosh!");

                saved.getWorld().playSound(saved, Sound.ENTITY_BREEZE_LAND, 2, 0.9f);
                pClicked.getWorld().playSound(pClicked, Sound.ENTITY_BREEZE_LAND, 2, 0.9f);

                saved.getWorld().spawnParticle(Particle.GUST_EMITTER_SMALL, saved.getLocation(), 1);
                pClicked.getWorld().spawnParticle(Particle.GUST_EMITTER_SMALL, pClicked.getLocation(), 1);

                data.put(RoleData.TRANSPORTER_TELEPORTS_LEFT, teleportsLeft - 1);
                data.put(RoleData.TRANSPORTER_SELECTED_PLAYER, null);
                if(teleportsLeft - 1 == 1) { p.sendMessage(ChatColor.AQUA + "You have 1 teleport left."); }
                else { p.sendMessage(ChatColor.AQUA + "You have " + (teleportsLeft - 1) + " teleports left."); }
            }
        }
    }
}
