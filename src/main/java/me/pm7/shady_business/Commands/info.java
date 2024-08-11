package me.pm7.shady_business.Commands;

import me.pm7.shady_business.Objects.Nerd;
import me.pm7.shady_business.Objects.RoleData;
import me.pm7.shady_business.Objects.RoleType;
import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class info implements CommandExecutor {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender instanceof Player) {
            Player p = (Player) commandSender;
            Nerd nerd = plugin.getNerd(p.getUniqueId());
            if(nerd == null) { return true; }

            switch (nerd.getRole()) {
                case VILLAGER: {
                    p.sendMessage(ChatColor.WHITE + "You are a Villager! You have no special task to do today :D");
                    break;
                }
                case BOOGEYMAN: {

                    // waste of processing power
                    String boogies;
                    List<String> boogeymen = new ArrayList<>();
                    for(Nerd boogey : plugin.getNerds()) {
                        if(boogey.getRole() == RoleType.BOOGEYMAN && boogey.getUuid() != nerd.getUuid()) {
                            boogeymen.add(boogey.getName());
                        }
                    }
                    if(boogeymen.size() == 2) { boogies = "friends " + boogeymen.get(0) + ", and " + boogeymen.get(1); }
                    else { boogies = "friend " + boogeymen.getFirst(); }

                    p.sendMessage(ChatColor.RED + "You are a Boogeyman! You and your " + boogies + " must each get a kill before the end of the session, or each of you will lose one life.");
                    break;
                }
                case INVESTIGATOR: {
                    p.sendMessage(ChatColor.BLUE + "You are the Investigator! Your task is to figure out who the boogeymen are. You get one Orb of Pondering, which will put a red box in chat if it hits a boogeyman, and a green box in chat if it doesn't. Look for large groups to get the most people!");
                    break;
                }
                case NECROMANCER: {
                    p.sendMessage(ChatColor.GOLD + "You are the Necromancer! You are able to give one of your lives away to a ghost player with /givelife!");
                    break;
                }
                case VICTIM: {
                    p.sendMessage(ChatColor.GREEN + "You are the Victim! Your goal is to get killed by a boogeyman. If you are killed, you will gain a life instead of losing a life, and the boogeyman will not be cured.");
                    break;
                }
                case MIMIC: {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + "You are the Mimic! You must right click to copy someone else's role before the end of the session or you will lose a life! To select a player, crouch and right click them with an empty hand.");
                    break;
                }
                case TWINS: {
                    p.sendMessage(ChatColor.YELLOW + "You are one of the Twins! Your health will be linked to another players' for the entire session or until one of you turn red. Good luck!");
                    break;
                }
                case TRANSPORTER: {
                    p.sendMessage(ChatColor.AQUA + "You are the Transporter! You will be able to swap the locations of two players by right clicking them. Crouch and right click a player with an empty hand to select/deselect them, and do the same with a different player to swap their locations. You can use this power two times.");
                    break;
                }
            }
        }

        return true;
    }
}
