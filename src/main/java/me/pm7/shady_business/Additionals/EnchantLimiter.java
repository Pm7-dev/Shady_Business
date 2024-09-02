package me.pm7.shady_business.Additionals;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EnchantLimiter implements Listener {
    List<Enchantment> limitList = Arrays.asList( //list of enchants to be limited to level 1
            Enchantment.PROTECTION,
            Enchantment.FEATHER_FALLING,
            Enchantment.FIRE_PROTECTION,
            Enchantment.BLAST_PROTECTION,
            Enchantment.PROJECTILE_PROTECTION,
            Enchantment.SHARPNESS,
            Enchantment.POWER
    );
    List<Enchantment> blockList = Arrays.asList( //list of enchants to block entirely
            Enchantment.FIRE_ASPECT,
            Enchantment.FLAME
    );

    @EventHandler
    public void onAnvilUse(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(p.getOpenInventory().getTopInventory().getType() == InventoryType.ANVIL) {
            Inventory anvilInv = p.getOpenInventory().getTopInventory();
            if(anvilInv.getItem(2) != null) {
                ItemStack item = anvilInv.getItem(2);

                Map<Enchantment, Integer> newEnchants = new java.util.HashMap<>(Collections.emptyMap());
                for (Enchantment ench : item.getEnchantments().keySet()) {
                    if (limitList.contains(ench)) {
                        newEnchants.put(ench, 1);                                   // lock enchantment level to 1
                    } else if (!blockList.contains(ench)){
                        newEnchants.put(ench, item.getEnchantments().get(ench));   // allow all normal enchantments
                    }
                    item.removeEnchantment(ench);                                  // the thing
                }
                for(Enchantment ench : newEnchants.keySet()) {
                    item.addEnchantment(ench, newEnchants.get(ench));
                }
            }
        }
    }

    @EventHandler
    public void onEnchant(EnchantItemEvent event) {
        Map<Enchantment, Integer> newEnchants = new java.util.HashMap<>(Collections.emptyMap());
        for (Enchantment ench : event.getEnchantsToAdd().keySet()) {
            if (limitList.contains(ench)) {
                newEnchants.put(ench, 1); // lock enchantment level to 1
            } else if (!blockList.contains(ench)){
                newEnchants.put(ench, event.getEnchantsToAdd().get(ench));
            }
        }
        event.getEnchantsToAdd().clear();
        event.getEnchantsToAdd().putAll(newEnchants);
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        if(e.getCaught() != null) {
            if (e.getCaught().getType() == EntityType.ITEM) {
                Item item = (Item) e.getCaught();
                ItemStack stack = item.getItemStack();

                if(stack.getType() == Material.ENCHANTED_BOOK) {stack.setType(Material.BOOK); return;}

                Map<Enchantment, Integer> newEnchants = new java.util.HashMap<>(Collections.emptyMap());
                for (Enchantment ench : stack.getEnchantments().keySet()) {
                    if (limitList.contains(ench)) {
                        newEnchants.put(ench, 1);                                   // lock enchantment level to 1
                    } else if (!blockList.contains(ench)){
                        newEnchants.put(ench, stack.getEnchantments().get(ench));   // allow all normal enchantments
                    }
                    stack.removeEnchantment(ench);                                  // the thing
                }
                for(Enchantment ench : newEnchants.keySet()) {
                    stack.addEnchantment(ench, newEnchants.get(ench));
                }
            }
        }
    }
}
