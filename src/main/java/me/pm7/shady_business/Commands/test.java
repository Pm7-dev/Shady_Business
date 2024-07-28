package me.pm7.shady_business.Commands;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import net.minecraft.network.protocol.PlayerConnectionUtils;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.network.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class test implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        CraftPlayer cp = (CraftPlayer) Bukkit.getPlayer("_Pm7");
        cp.setHealth(3);
        EntityPlayer nmsPlayer = cp.getHandle();
        PacketPlayOutAnimation packet = new PacketPlayOutAnimation(nmsPlayer, 0);
        PlayerConnection pc = nmsPlayer.c;
        pc.sendPacket(packet);
        pc.sendPacket(packet2);

        /*
        Player pm7 = Bukkit.getPlayer("_Pm7");
        Player pm8 = Bukkit.getPlayer("_Pm8");

        pm7.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30.0d);
        pm8.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(30.0d);

        Nerd np7 = plugin.getNerd(pm7.getUniqueId());
        Nerd np8 = plugin.getNerd(pm8.getUniqueId());

        np7.setLives(4);
        np8.setLives(4);
        np7.setRole(RoleType.TWINS);
        np8.setRole(RoleType.TWINS);

        List<Object> dat7 = new ArrayList<>();
        List<Object> dat8 = new ArrayList<>();

        dat7.add(np8.getName());
        dat8.add(np7.getName());

        dat7.add(true);
        dat8.add(false);

        dat7.add(30.0d);
        dat8.add(-77.0d);

        np7.setData(dat7);
        np8.setData(dat8);

        plugin.saveData();

         */

        return true;
    }
}
