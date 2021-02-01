package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.GameState;
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
        if (!getGame().getGameController().inGame(event.getPlayer())) return;
        getGame().getGameController().removeMatch(event.getPlayer());

        if (!getGame().getWorld().getName().equalsIgnoreCase(event.getPlayer().getWorld().getName())) return;

        if (getGame().getPlayers().size() == getGame().getMinPlayers()) {
            getGame().getGameController().setState(new StartingGameState(), getGame().getSkywarsPlugin());
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        getGame().getGameController().removeMatch(event.getPlayer());
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    private void onBreakBlock(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }
}

//TODO: adicionar, por exemplo, caso alguém entre no mundo sem utilizar o comando