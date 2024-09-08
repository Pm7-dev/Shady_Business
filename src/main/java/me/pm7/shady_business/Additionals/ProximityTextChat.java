package me.pm7.shady_business.Additionals;

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
    int fadeDist = 50;//config.getInt("textChatFade");
    int cutDist = 65;//config.getInt("textChatCut");

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

        message = message.substring(1);
        p.sendMessage("<" + p.getDisplayName() + "> " + message);

        for(Player receiver : Bukkit.getOnlinePlayers()) {
            if(receiver == p) {continue;}

            String newMsg = message;

            Location receiverLocation = receiver.getLocation();
            Double rX = receiverLocation.getX();
            Double rY = receiverLocation.getY();
            Double rZ = receiverLocation.getZ();

            //finding the distance between two objects in a 3D space
            double distance = Math.abs(Math.sqrt(Math.pow(sX-rX, 2) + Math.pow(sY-rY, 2)/2 + Math.pow(sZ-rZ, 2)));

            if (distance > cutDist) {continue;}
            if (distance > fadeDist) {newMsg = obfuscateMessage(message, ((distance - fadeDist) / (cutDist - fadeDist)) * 100);}

            receiver.sendMessage("<" + p.getDisplayName() + "> " + newMsg);
        }
    }

    String obfuscateMessage(String message, Double power) {

        // Cut out random words
        StringBuilder newMsg = new StringBuilder();
        String[] words = message.split(" ");
        for(String word : words) {
            if ((Math.random() * ((100) + 1)) < (power/4)) {
                newMsg.append("...");
            } else {
                newMsg.append(word);
            }
            newMsg.append(" ");
        }

        // Change the vowels
        StringBuilder obfuscatedMessage = new StringBuilder();
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
                obfuscatedMessage.append(charToAdd);
            } else

                // Obfuscate numbers
                if(numbers.contains(newMsg.charAt(i)) && (Math.random() * ((100) + 1)) < (power)) {
                    char charToAdd = numbers.get((int) Math.floor(Math.random() * ((10))));
                    obfuscatedMessage.append(charToAdd);
                } else {
                    obfuscatedMessage.append(newMsg.charAt(i));
                }
        }
        return obfuscatedMessage.toString();
    }
}
