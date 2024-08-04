package me.pm7.shady_business.Commands;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import net.minecraft.network.protocol.PlayerConnectionUtils;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class test implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        Player pm7 = Bukkit.getPlayer("_Pm7");

        Inventory inv = pm7.getInventory();

        ItemStack ponderingOrb = new ItemStack(Material.SPLASH_POTION);

        PotionMeta meta = (PotionMeta) ponderingOrb.getItemMeta();
        meta.setItemName("Orb of Pondering");
        meta.setRarity(ItemRarity.RARE);
        meta.setCustomModelData(2);
        meta.setColor(Color.RED);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.LUCK, PotionEffect.INFINITE_DURATION, 1, true, false, false), true);
        ponderingOrb.setItemMeta(meta);
        inv.addItem(ponderingOrb);

        return true;
    }
}
