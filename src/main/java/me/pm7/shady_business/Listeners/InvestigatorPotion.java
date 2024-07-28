package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


/*

    This entire class is just for the investigator potion lol
    probably was a much better way to do all of this but hey, it works (probably)

    written before I actually got it working ^

 */


public class InvestigatorPotion implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @EventHandler
    public void OnPotion(EntityPotionEffectEvent e) {

        if(e.getEntity() instanceof Player) {
            if(e.getNewEffect().getType() != PotionEffectType.LUCK) { return; }
            e.setCancelled(true);

            // Make sure nothing happens if the investigator is splashed
            Nerd nerd = plugin.getNerd(e.getEntity().getUniqueId());
            if(nerd == null || nerd.getRole() == RoleType.INVESTIGATOR) { return; }

            // Get the actual investigator
            Nerd investigator = null;
            for (Nerd i : plugin.getNerds()) {
                if(i.getRole() == RoleType.INVESTIGATOR) {
                    investigator = i;
                    break;
                }
            }
            if(investigator == null) { return; }
            Player p = Bukkit.getPlayer(investigator.getUuid());
            if(p == null) { return; }

            // Let the investigator know if they found the guy
            if(nerd.getRole() == RoleType.BOOGEYMAN) { p.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "■ Boogeyman! ■");}
            else { p.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "■ Not A Boogeyman! ■"); }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getInventory().getHolder();
        if(p != null) {return;}
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null || nerd.getRole() != RoleType.INVESTIGATOR) { return; }
        if(!((Boolean) nerd.getData().get(0))) { return; }
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
        inv.addItem(ponderingOrb);
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent e) {
        if(e.getDestination().getHolder() != e.getSource().getHolder()) {
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
