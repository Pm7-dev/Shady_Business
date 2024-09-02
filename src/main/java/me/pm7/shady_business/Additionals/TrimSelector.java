package me.pm7.shady_business.Additionals;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TrimSelector implements Listener {

    List<TrimMaterial> trimMats = Arrays.asList(
            TrimMaterial.AMETHYST,
            TrimMaterial.COPPER,
            TrimMaterial.DIAMOND,
            TrimMaterial.EMERALD,
            TrimMaterial.GOLD,
            TrimMaterial.IRON,
            TrimMaterial.LAPIS,
            TrimMaterial.NETHERITE,
            TrimMaterial.QUARTZ,
            TrimMaterial.REDSTONE
    );
    List<Material> trimMatsDisplay = Arrays.asList(
            Material.AMETHYST_SHARD,
            Material.COPPER_INGOT,
            Material.DIAMOND,
            Material.EMERALD,
            Material.GOLD_INGOT,
            Material.IRON_INGOT,
            Material.LAPIS_LAZULI,
            Material.NETHERITE_INGOT,
            Material.QUARTZ,
            Material.REDSTONE
    );
    List<TrimPattern> trimTypes = Arrays.asList(
            TrimPattern.COAST,
            TrimPattern.DUNE,
            TrimPattern.EYE,
            TrimPattern.RIB,
            TrimPattern.SENTRY,
            TrimPattern.SNOUT,
            TrimPattern.SPIRE,
            TrimPattern.TIDE,
            TrimPattern.VEX,
            TrimPattern.WARD,
            TrimPattern.WILD
    );
    List<Material> trimTypesDisplay = Arrays.asList(
            Material.COAST_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.DUNE_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.RIB_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.TIDE_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.VEX_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.WARD_ARMOR_TRIM_SMITHING_TEMPLATE,
            Material.WILD_ARMOR_TRIM_SMITHING_TEMPLATE
    );
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player p = e.getPlayer();
            if(p.getInventory().getItemInMainHand().getItemMeta() != null && p.getInventory().getArmorContents().length != 0) {
                if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Trim Selector")) {
                    e.setCancelled(true);
                    Inventory inv = Bukkit.createInventory(p, 18, ChatColor.BLUE + "Trim Material Selector");
                    for (int i = 0; i < trimMats.size(); i++) {
                        Material mat = trimMatsDisplay.get(i);
                        ItemStack item = new ItemStack(mat, 1);
                        ItemMeta meta = item.getItemMeta();
                        meta.setLore(Arrays.asList("Click to select trim material type"));
                        item.setItemMeta(meta);
                        inv.setItem(i, item);
                    }
                    p.openInventory(inv);
                }
            }
        }
    }

    HashMap<String, TrimMaterial> chosen = new HashMap<>();
    @EventHandler
    public void onPlayerInteractMenu(InventoryClickEvent e) {
        if(e.getCurrentItem() != null) {
            if (e.getView().getTitle().equals(ChatColor.BLUE + "Trim Material Selector")) {
                Player p = (Player) e.getWhoClicked();
                chosen.remove(p.getName());
                chosen.put(p.getName(), trimMats.get(trimMatsDisplay.indexOf(e.getCurrentItem().getType())));

                p.closeInventory();

                Inventory inv = Bukkit.createInventory(p, 18, ChatColor.BLUE + "Trim Type Selector");
                for (int i = 0; i < trimTypes.size(); i++) {
                    Material mat = trimTypesDisplay.get(i);
                    ItemStack item = new ItemStack(mat, 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Arrays.asList("Click to select trim type"));
                    item.setItemMeta(meta);
                    inv.setItem(i, item);
                }
                p.openInventory(inv);

            }

            if (e.getView().getTitle().equals(ChatColor.BLUE + "Trim Type Selector")) {
                Player p = (Player) e.getWhoClicked();
                for (ItemStack item : p.getInventory().getArmorContents()) {
                    if (item != null) {
                        ArmorMeta meta = (ArmorMeta) item.getItemMeta();
                        if (meta != null) {
                            meta.setTrim(new ArmorTrim(chosen.get(p.getName()), trimTypes.get(trimTypesDisplay.indexOf(e.getCurrentItem().getType()))));
                            item.setItemMeta(meta);
                        }
                    }
                }
                chosen.remove(p.getName());

                p.closeInventory();

            }
        }
    }
}
