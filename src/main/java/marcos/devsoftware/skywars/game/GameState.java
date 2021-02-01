package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@Getter @Setter
public abstract class GameState implements Listener {

    private Game game;

    public void onEnable(SkywarsPlugin skywarsPlugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, skywarsPlugin);
    }

    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public String getName() {
        return getClass().getSimpleName();
    }
}