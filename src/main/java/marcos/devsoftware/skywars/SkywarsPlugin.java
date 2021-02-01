package marcos.devsoftware.skywars;

import lombok.Getter;
import marcos.devsoftware.skywars.commands.SkywarsCommand;
import marcos.devsoftware.skywars.configuration.ConfigurationManager;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.GameManager;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SkywarsPlugin extends JavaPlugin {

    private ConfigurationManager configurationManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        this.configurationManager = new ConfigurationManager(this);
        this.gameManager = new GameManager(this);

        getCommand("skywars").setExecutor(new SkywarsCommand(this));
    }

    @Override
    public void onDisable() {
        for (Game game : gameManager.getGames()) {
            if (game == null) continue;

            game.getWorld().getPlayers().forEach(player -> player.teleport(new Location(Bukkit.getWorld("world"), -103, 41, 1286)));
            WorldUtility.deleteWorld(game.getWorld());
        }

    }
}