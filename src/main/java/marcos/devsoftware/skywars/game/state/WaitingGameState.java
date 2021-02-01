package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.GameState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaitingGameState extends GameState {

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent event) {
        if (getGame().getGameController().inGame(event.getPlayer())) {
            if (getGame().getWorld().getName().equalsIgnoreCase(event.getPlayer().getWorld().getName())) {
                if (getGame().getPlayers().size() == getGame().getMinPlayers()) {
                    getGame().getGameController().setState(new StartingGameState(), getGame().getSkywarsPlugin());
                }

            } else {
                getGame().getGameController().removeMatch(event.getPlayer());
            }
        } else {
            if (getGame().getWorld().getName().equalsIgnoreCase(event.getPlayer().getWorld().getName())) {
                getGame().getGameController().activeSpectatorSettings(event.getPlayer());
            }
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;
        getGame().getGameController().removeMatch(event.getPlayer());
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!getGame().getGameController().inGame((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!getGame().getGameController().inGame((Player) event.getEntity())) return;

        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    private void onBreakBlock(BlockBreakEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        event.setCancelled(true);
    }
}