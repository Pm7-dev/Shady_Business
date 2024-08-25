package me.pm7.shady_business.Commands;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

// intellij keeps telling me to capitalize the L in givelife and I don't want to. >:(
public class givelife implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {

        // I hate you if you run the command from console
        // on second thought, death threat via console is not a good idea
        if(!(sender instanceof Player)) {
            System.out.println("why");
            return true;
        }
        if(args.length == 1) {
            Player receiverPlayer = Bukkit.getPlayer(args[0]);
            if(receiverPlayer == null) { sender.sendMessage(ChatColor.RED + "Please specify a valid player"); return true; }
            Nerd receiverNerd = plugin.getNerd(receiverPlayer.getUniqueId());
            if(receiverNerd == null) { sender.sendMessage(ChatColor.RED + "Uhh... Has the game not started yet?"); return true; }

            Player giverPlayer = (Player) sender;
            Nerd giverNerd = plugin.getNerd(giverPlayer.getUniqueId());
            if(giverNerd == null) { sender.sendMessage(ChatColor.RED + "Uhh... Has the game not started yet?"); return true; }

            // I don't want to deal with this case, so I will not.
            if(receiverNerd.getRole() == RoleType.TWINS || giverNerd.getRole() == RoleType.TWINS) {
                sender.sendMessage(ChatColor.RED + "Twins cannot give/receive lives.");
                return true;
            }

            // Make sure the giver has lives to give
            if(giverNerd.getLives() > 1) {
                if(receiverNerd.getLives() > 0) {
                    receiverNerd.addLife();
                    giverNerd.removeLife();

                    giverPlayer.sendMessage(ChatColor.GREEN + "You gave a life to " + args[0]);
                    receiverPlayer.sendMessage(ChatColor.GREEN + "You received a life from " + giverPlayer.getName());

                } else {
                    // If the receiver is a ghost, check if the giver is a necromancer and if they have completed their objective or not
                    if(giverNerd.getRole() == RoleType.NECROMANCER && !(Boolean) giverNerd.getData().get(RoleData.NECROMANCER_USED)) {
                        receiverNerd.addLife(giverPlayer.getLocation());
                        giverNerd.removeLife();
                        giverNerd.getData().put(RoleData.NECROMANCER_USED, true);
                        plugin.saveData();

                        giverPlayer.sendMessage(ChatColor.GOLD + "You revived " + args[0]);
                        receiverPlayer.sendMessage(ChatColor.GOLD + "You have been revived by " + giverPlayer.getName());

                        for (Player p : Bukkit.getOnlinePlayers()) {

                            // play sp00ky sounds
                            p.playSound(p, Sound.ENTITY_WITHER_SPAWN, 500, 0.75f);
                            p.playSound(p, Sound.ENTITY_WITHER_SPAWN, 500, 1.5f);

                            if(p != giverPlayer && p != receiverPlayer) {
                                p.sendMessage(ChatColor.GOLD + receiverPlayer.getName() + " has been revived!");
                            }
                        }
                    } else {
                        giverPlayer.sendMessage(ChatColor.RED + "You are either not the necromancer, or you have already revived someone");
                    }
                }
            } else {
                giverPlayer.sendMessage(ChatColor.RED + "You cannot commit suicide via generosity.");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Please specify a valid player");
        }
        return true;
    }
}
