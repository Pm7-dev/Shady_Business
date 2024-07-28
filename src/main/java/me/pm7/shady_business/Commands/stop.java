package me.pm7.shady_business.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class stop implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        // I hate you udtorn
        // I am keeping this above comment forever

        if(commandSender instanceof Player) {
            if(!commandSender.isOp()) { return true; }

        } else {
            System.out.println("hi console user");
        }
        return true;
    }
}
