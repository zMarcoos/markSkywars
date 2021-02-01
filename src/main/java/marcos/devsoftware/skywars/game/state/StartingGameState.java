package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.GameState;
import marcos.devsoftware.skywars.game.state.task.StartingGameTask;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StartingGameState extends GameState {

    private StartingGameTask startingGameTask;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        this.startingGameTask = new StartingGameTask(getGame(), skywarsPlugin);
        this.startingGameTask.runTaskTimer(skywarsPlugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.startingGameTask != null) this.startingGameTask.cancel();
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;
        if (getGame().getWorld().getName().equalsIgnoreCase(event.getPlayer().getWorld().getName())) {
            if (getGame().getPlayers().size() == getGame().getMaxPlayers() && getGame().getGameController().getTimeUntilStartMatch() < 10) {
                getGame().getGameController().setTimeUntilStartMatch(10);
                getGame().getGameController().sendGameMessage(MessageUtility.getMessageByTitle("match_all_players", getGame().getSkywarsPlugin()));
            }

        } else {
            getGame().getGameController().removeMatch(event.getPlayer());

            if (getGame().getPlayers().size() < getGame().getMinPlayers()) {
                getGame().getGameController().setState(new WaitingGameState(), getGame().getSkywarsPlugin());
            }
        }
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;
        getGame().getGameController().removeMatch(event.getPlayer());

        if (getGame().getPlayers().size() < getGame().getMinPlayers()) {
            getGame().getGameController().setState(new WaitingGameState(), getGame().getSkywarsPlugin());
        }
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