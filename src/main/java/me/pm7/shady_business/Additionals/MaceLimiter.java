package me.pm7.shady_business.Additionals;

import net.minecraft.world.item.ItemMapEmpty;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MaceLimiter implements Listener {

    List<Enchantment> blocked = Arrays.asList(Enchantment.UNBREAKING, Enchantment.MENDING);

    @EventHandler
    public void onCraft(CraftItemEvent e) {
        if(e.getRecipe().getResult().getType() == Material.MACE) {
            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            ((Damageable) meta).setDamage(497);
            item.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onAnvilUse(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if(p.getOpenInventory().getTopInventory().getType() == InventoryType.ANVIL) {
            Inventory anvilInv = p.getOpenInventory().getTopInventory();
            if(anvilInv.getItem(2) != null) {
                ItemStack item = anvilInv.getItem(2);

                Map<Enchantment, Integer> newEnchants = new java.util.HashMap<>(Collections.emptyMap());
                for (Enchantment ench : item.getEnchantments().keySet()) {
                    if (!blocked.contains(ench)){
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
            if (!blocked.contains(ench)){
                newEnchants.put(ench, event.getEnchantsToAdd().get(ench));
            }
        }
        event.getEnchantsToAdd().clear();
        event.getEnchantsToAdd().putAll(newEnchants);
    }
}
