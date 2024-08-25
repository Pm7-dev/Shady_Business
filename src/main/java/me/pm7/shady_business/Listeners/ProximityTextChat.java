package me.pm7.shady_business.Listeners;

import me.pm7.shady_business.ShadyBusiness;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Arrays;
import java.util.List;

import static java.lang.Character.toUpperCase;

public class ProximityTextChat implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    private final FileConfiguration config = plugin.getConfig();

    List<Character> vowels = Arrays.asList('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');
    List<Character> numbers = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    int fadeDist = config.getInt("textChatFade");
    int cutDist = config.getInt("textChatCut");

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {

        //ghost prevention
        Player p = e.getPlayer();
        if(e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            p.sendMessage(ChatColor.RED + "There is nobody to talk to but the abyss. You are alone.");
            e.setCancelled(true);
            return;
        }

        String message = e.getMessage();
        if(!message.startsWith(".")) { return; }
        e.setCancelled(true);

        Location senderLocation = p.getLocation();
        Double sX = senderLocation.getX(); //hahahahahaha sex
        Double sY = senderLocation.getY();
        Double sZ = senderLocation.getZ();

        for(Player receiver : Bukkit.getOnlinePlayers()) {
            message = message.substring(1);

            Location receiverLocation = receiver.getLocation();
            Double rX = receiverLocation.getX();
            Double rY = receiverLocation.getY();
            Double rZ = receiverLocation.getZ();

            //Math for finding the distance between two objects in a 3D space
            double distance = Math.abs(Math.sqrt(Math.pow(sX-rX, 2) + Math.pow(sY-rY, 2) + Math.pow(sZ-rZ, 2)));

            if (distance > cutDist) {continue;}
            if (distance > fadeDist) {message = obfuscateMessage(message, ((distance - fadeDist) / (cutDist - fadeDist)) * 100);}

            receiver.sendMessage("<" + p.getDisplayName() + "> " + message);
        }
    }

    String obfuscateMessage(String message, Double power) {

        // Cut out select words
        String newMsg = "";
        String[] words = message.split(" ");
        for(String word : words) {
            if ((Math.random() * ((100) + 1)) < (power/4)) {
                newMsg += "...";
            } else {
                newMsg += word;
            }
            newMsg += " ";
        }

        // Obfuscator
        String obfuscatedMessage = "";
        for (int i = 0; i < newMsg.length(); i++) {

            // Obfuscate vowels
            if(vowels.contains(newMsg.charAt(i)) && (Math.random() * ((100) + 1)) < (power/1.5)) {
                char charToAdd = switch ((int) Math.floor(Math.random() * ((6) + 1))) {
                    case 2 -> 'e';
                    case 3 -> 'i';
                    case 4 -> 'o';
                    case 5 -> 'u';
                    default -> 'a';
                };
                if(Character.isUpperCase(newMsg.charAt(i))) {
                    charToAdd = toUpperCase(charToAdd);
                }
                obfuscatedMessage += charToAdd;
            } else

                // Obfuscate numbers
                if(numbers.contains(newMsg.charAt(i)) && (Math.random() * ((100) + 1)) < (power)) {
                    char charToAdd = numbers.get((int) Math.floor(Math.random() * ((10))));
                    obfuscatedMessage += charToAdd;
                } else {
                    obfuscatedMessage += newMsg.charAt(i);
                }
        }
        return obfuscatedMessage;
    }
}
