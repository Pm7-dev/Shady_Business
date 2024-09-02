package me.pm7.shady_business.Additionals;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.List;

public class PotionLimiter implements Listener {
    List<PotionType> blockList = Arrays.asList( //list of potions to be blocked
            PotionType.STRENGTH,
            PotionType.HARMING
    );
    @EventHandler
    public void onPlayerBrew(BrewEvent e) {
        List<ItemStack> list = e.getResults();
        for (ItemStack item: list) {
            if (item.getType() == Material.POTION) {
                PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
                if(potionMeta != null) {
                    if (blockList.contains(potionMeta.getBasePotionType())) { // Check if the potion is a Strength potion
                        e.setCancelled(true); // Cancel the event to prevent the potion from being brewed
                        return;
                    }
                    if(e.getContents().getIngredient().getType() == Material.GLOWSTONE) { //Limit all potions to level one
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
}
