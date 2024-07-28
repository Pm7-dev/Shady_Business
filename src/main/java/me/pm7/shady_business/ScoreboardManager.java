package me.pm7.shady_business;

import me.pm7.shady_business.Objects.Nerd;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager implements Listener {
    private static final ShadyBusiness plugin = ShadyBusiness.getPlugin();
    static FileConfiguration config = plugin.getConfig();

    static org.bukkit.scoreboard.ScoreboardManager manager = Bukkit.getScoreboardManager();
    public static Scoreboard board = manager.getNewScoreboard();

    static Team dark_green = board.registerNewTeam("1 Dark Green");
    static Team green = board.registerNewTeam("2 Green");
    static Team yellow = board.registerNewTeam("3 Yellow");
    static Team red = board.registerNewTeam("4 Red");
    static Team gray = board.registerNewTeam("5 Gray");

    public static void UpdatePlayerScore(Nerd nerd) {
        if(nerd == null) { System.out.println("nerd was null, cannot give them a name color :("); return; }
        Player p = Bukkit.getPlayer(nerd.getUuid());
        if(!config.getBoolean("started") || p == null) { return; }


        p.setScoreboard(board);

        if(green != board.getTeam("1 Dark Green")) { //erm, what the scallop

            dark_green = board.getTeam("1 Dark Green");
            green = board.getTeam("2 Green");
            yellow = board.getTeam("3 Yellow");
            red = board.getTeam("4 Red");
            gray = board.getTeam("5 Gray");

            dark_green.setColor(ChatColor.DARK_GREEN);
            green.setColor(ChatColor.GREEN);
            yellow.setColor(ChatColor.YELLOW);
            red.setColor(ChatColor.RED);
            gray.setColor(ChatColor.GRAY);

            dark_green.setAllowFriendlyFire(true);
            green.setAllowFriendlyFire(true);
            yellow.setAllowFriendlyFire(true);
            red.setAllowFriendlyFire(true);
            gray.setAllowFriendlyFire(true);

            dark_green.setCanSeeFriendlyInvisibles(false);
            green.setCanSeeFriendlyInvisibles(false);
            yellow.setCanSeeFriendlyInvisibles(false);
            red.setCanSeeFriendlyInvisibles(false);
            gray.setCanSeeFriendlyInvisibles(false);
        }

        String name = nerd.getName();
        dark_green.removeEntry(name);
        green.removeEntry(name);
        yellow.removeEntry(name);
        red.removeEntry(name);
        gray.removeEntry(name);

        int lives = nerd.getLives();
        switch (lives) {
            case -1:
            case 0:
                p.setDisplayName(ChatColor.GRAY + name + ChatColor.RESET);
                gray.addEntry(name);
                break;
            case 1:
                p.setDisplayName(ChatColor.RED + name + ChatColor.RESET);
                red.addEntry(name);
                break;
            case 2:
                p.setDisplayName(ChatColor.YELLOW + name + ChatColor.RESET);
                yellow.addEntry(name);
                break;
            case 3:
                p.setDisplayName(ChatColor.GREEN + name + ChatColor.RESET);
                green.addEntry(name);
                break;
            default:
                p.setDisplayName(ChatColor.DARK_GREEN + name + ChatColor.RESET);
                dark_green.addEntry(name);
                break;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(config.getBoolean("started")) {
            UpdatePlayerScore(plugin.getNerd(e.getPlayer().getUniqueId()));
        }
    }
}
