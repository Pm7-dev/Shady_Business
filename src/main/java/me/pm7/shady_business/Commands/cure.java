package me.pm7.shady_business.Commands;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/*
    songs listened to while writing this file:
    (I'm punishing myself for how slow I am by revealing my trash music taste)

    Silentroom - nhelv
    LemKuuja - DONTHOLDBACK
    Garlagan - What You Love
    Tally Hall - Turn the Lights Off

    it really should not have taken this long, this is incredibly simple.
 */

public class cure implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    // not me forgetting to add a manual cure boogey command until now
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) {
            System.out.println("haiiiii :3"); //fddddddddasfsdjkfhuuhehjhjkedhjehjhjhjdhduheheuf
            return true;
        }
        Nerd nerd = plugin.getNerd(p.getUniqueId());
        if(nerd == null) {
            p.sendMessage(ChatColor.RED + "Can't find your player data! has the game started yet?"); // I hate writing friendly messages like this. I want to insult you for being stupid
            return true;
        }

        if(nerd.getRole() != RoleType.BOOGEYMAN) {
            p.sendMessage(ChatColor.RED + "You're not a boogeyman, loser"); // this is more like it
            return true;
        }

        HashMap<RoleData, Object> data = nerd.getData();
        if((boolean)data.get(RoleData.BOOGEYMAN_CURED)) {
            p.sendMessage(ChatColor.RED + "You've already been cured");
            return true;
        }

        // surely there are no other stupidity cases to handle

        data.put(RoleData.BOOGEYMAN_CURED, true);
        for(Player b : Bukkit.getOnlinePlayers()) { // b for... boogeyman, I guess
            Nerd nb = plugin.getNerd(b.getUniqueId()); // insert some nonbinary pun I'm not clever enough for here
            if(nb == null) { continue; }
            if(nb.getRole() == RoleType.BOOGEYMAN) {
                b.sendMessage(ChatColor.RED + p.getName() + " has been cured!");
            }
        }

        return true;
    }
}
