package me.pm7.shady_business.Commands;

import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class start implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender.isOp()) {

        } else if(commandSender.getName().equals("Piffin380")) {

        }
        return true;
    }
}
