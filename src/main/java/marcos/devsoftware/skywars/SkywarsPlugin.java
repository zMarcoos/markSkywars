package marcos.devsoftware.skywars;

import lombok.Getter;
import marcos.devsoftware.skywars.commands.SkywarsCommand;
import marcos.devsoftware.skywars.configuration.ConfigurationManager;
import marcos.devsoftware.skywars.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class SkywarsPlugin extends JavaPlugin {

    private ConfigurationManager configurationManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskLater(this, () -> {
            this.configurationManager = new ConfigurationManager(this);
            this.gameManager = new GameManager(this);

            getCommand("skywars").setExecutor(new SkywarsCommand(this));
        }, 100);
    }

    @Override
    public void onDisable() {
        if (gameManager != null) gameManager.onDisable();
    }
}