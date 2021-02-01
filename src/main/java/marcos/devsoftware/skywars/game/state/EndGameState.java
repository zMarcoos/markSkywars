package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.GameState;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

public class EndGameState extends GameState {

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        for (UUID uuid : getGame().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            player.setAllowFlight(true);
            player.setFlying(true);
        }

        Bukkit.getScheduler().runTaskLater(skywarsPlugin, () -> gameUnload(skywarsPlugin), 20 * 15);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!getGame().getGameController().inGame((Player) event.getEntity())) return;

        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            Location location = getGame().getGameController().getPlayerSpawnMap().get(event.getEntity().getUniqueId());
            if (location == null) {
                event.getEntity().teleport(getGame().getMaxLocation());
            } else {
                event.getEntity().teleport(location);
            }
        }

        event.setCancelled(true);
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!getGame().getGameController().inGame((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    private void gameUnload(SkywarsPlugin skywarsPlugin) {
        for (UUID uuid : getGame().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            player.teleport(new Location(Bukkit.getWorld("world"), -103, 41, 1286));
            player.setAllowFlight(false);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }

        for (UUID uuid : getGame().getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            player.teleport(new Location(Bukkit.getWorld("world"), -103, 41, 1286));
            player.setAllowFlight(false);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
        }

        WorldUtility.deleteWorld(getGame().getWorld());

        Game game = new Game(getGame().getGameFile(), skywarsPlugin);
        skywarsPlugin.getGameManager().getGames().removeIf(gameListed -> gameListed.getWorld().getName().equalsIgnoreCase(getGame().getWorld().getName()));
        skywarsPlugin.getGameManager().getGames().add(game);
    }
}