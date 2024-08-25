package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/*

    This entire class is just for the investigator potion lol
    probably was a much better way to do all of this but hey, it works (probably)

    written before I actually got it working ^



    got it working!!!! (8/2/24, like a week later)

    I broke it again!!! (8/5/24)

    I think I got it fixed at some point but now I don't remember and am worried (8/18/24)

 */


public class Investigator implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    private final Random random = new Random();

    @EventHandler
    public void OnPotion(EntityPotionEffectEvent e) {

        if(!(e.getEntity() instanceof Player)) { return; }
        if(e.getNewEffect() == null) { return; }
        if(e.getNewEffect().getType() != PotionEffectType.LUCK) { return; }
        e.setCancelled(true);

        // Make sure nothing happens if the investigator is splashed
        Nerd nerd = plugin.getNerd(e.getEntity().getUniqueId());
        if(nerd == null) { return; }

        // Get the actual investigator
        List<Nerd> investigators = new ArrayList<>();
        for (Nerd i : plugin.getNerds()) {
            if(i.getRole() == RoleType.INVESTIGATOR && i.getLives() > 1) {
                investigators.add(i);
                break;
            }
        }
        if(investigators.isEmpty()) { return; }
        for(Nerd investigator : investigators) {
            Player p = Bukkit.getPlayer(investigator.getUuid());
            if (p == null) {
                return;
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                p.playSound(p, Sound.ENTITY_SHULKER_BULLET_HIT, 500, 0.65f);

                // Let the investigator know if they found the guy
                if (nerd.getRole() == RoleType.BOOGEYMAN) {
                    p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "■ Boogeyman! ■");
                } else {
                    p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "■ Not A Boogeyman! ■");
                }
            }, random.nextInt(8) + 2);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!(e.getInventory().getHolder() instanceof Player p)) {
            if(e.getCurrentItem() == null) { return; }
            if(e.getCurrentItem().getType() == Material.SPLASH_POTION && e.getCurrentItem().getItemMeta().getItemName().equals("Orb of Pondering")) {
                e.setCancelled(true);
            }
            return;
        }

        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null || nerd.getRole() != RoleType.INVESTIGATOR) { return; }
        if(!((Boolean) nerd.getData().get(RoleData.INVESTIGATOR_NEEDS_POTION))) { return; }
        Inventory inv = p.getInventory();
        int index = inv.firstEmpty();
        if(index == -1) { return; }

        ItemStack ponderingOrb = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) ponderingOrb.getItemMeta();
        meta.setItemName("Orb of Pondering");
        meta.setRarity(ItemRarity.RARE);
        meta.setCustomModelData(2);
        meta.setColor(Color.RED);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.LUCK, PotionEffect.INFINITE_DURATION, 1, true, false, false), true);
        ponderingOrb.setItemMeta(meta);
        inv.addItem(ponderingOrb);
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent e) {
        if(e.getDestination().getType() != InventoryType.PLAYER) {
            if(e.getItem().getType() == Material.SPLASH_POTION && e.getItem().getItemMeta().getItemName().equals("Orb of Pondering")) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent e) {
        if(e.getItemDrop().getItemStack().getType() == Material.SPLASH_POTION && e.getItemDrop().getItemStack().getItemMeta().getItemName().equals("Orb of Pondering")) {
            e.setCancelled(true);
        }
    }
}
