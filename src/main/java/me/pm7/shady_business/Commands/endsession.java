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

public class endsession implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // I am keeping this above comment forever

        // nvm I removed it
        // there was context I swear
        // :p

        if(!commandSender.isOp()) { return true; }

        boolean boogeyFailed = false;
        for (Nerd nerd : plugin.getNerds()) {
            if(nerd.getLives() <= 1) { continue; }

            if(nerd.getRole() == RoleType.MIMIC) {
                nerd.removeLife();
                Player mimic = Bukkit.getPlayer(nerd.getUuid());
                if(mimic != null) {
                    mimic.sendTitle(ChatColor.LIGHT_PURPLE + "You Failed!", ChatColor.LIGHT_PURPLE + "You lost one life!");
                }
                continue;
            }

            if(!boogeyFailed && nerd.getRole() == RoleType.BOOGEYMAN) {
                if(!(boolean) nerd.getData().get(RoleData.BOOGEYMAN_CURED)) {
                    // Make sure there are still people that can be killed by the boogeymen
                    for(Nerd nerd1 : plugin.getNerds()) {
                        if(nerd1.getLives() > 1 && nerd1.getRole() != RoleType.BOOGEYMAN) {
                            boogeyFailed = true;
                            break;
                        }
                    }
                }
            }
        }

        if(boogeyFailed) {
            Bukkit.broadcastMessage(ChatColor.RED + "The boogeymen have failed! Each of them will now lose one life.");
            for (Nerd nerd : plugin.getNerds()) {
                if(nerd.getRole() == RoleType.BOOGEYMAN && nerd.getLives() > 1) {
                    nerd.removeLife();
                }
            }
        }

        return true;
    }
}
