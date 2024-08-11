package me.pm7.shady_business.Commands;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class setlife implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.isOp()) { return true; }
        Nerd nerd = plugin.getNerd(args[0]);
        if(nerd == null) {
            sender.sendMessage(ChatColor.RED + "There is no player with that name.");
            return true;
        }

        // There's a more efficient way to do this, but really, is there a point?
        int lives = nerd.getLives();
        int newLives = Integer.parseInt(args[1]);
        while(newLives != lives) {
            if(lives > newLives) { nerd.removeLife(); }
            else { nerd.addLife(); }
            lives = nerd.getLives();
        }

        sender.sendMessage(ChatColor.GREEN + "Success!");
        return true;
    }
}
