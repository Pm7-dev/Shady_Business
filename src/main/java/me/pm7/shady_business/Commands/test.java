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
import org.bukkit.ChatColor;
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

import java.util.*;

public class test implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player pm7 = Bukkit.getPlayer("_Pm7");
        Player pm8 = Bukkit.getPlayer("_Pm8");

        Nerd n7 = plugin.getNerd(pm7.getUniqueId());
        Nerd n8 = plugin.getNerd(pm8.getUniqueId());

        n7.setRole(RoleType.CONDEMNED);
        n8.setRole(RoleType.CONDEMNED);

        n7.getData().put(RoleData.CONDEMNED_VOTE_LIST, new LinkedList<>(Arrays.asList("UwU", "Abdron", "Udtorn", ">w<", ">w<", ">w<", ">w<", ">w<", ">w<", ">w<")));
        n8.getData().put(RoleData.CONDEMNED_VOTE_LIST, new LinkedList<>(Arrays.asList("a", "ab", "an", "OwO", ",a", "ga", "wa", "wa", "wa", "wa", "wa", "wa", "wa", "wa", "wa", "wa")));


        return true;
    }
}
