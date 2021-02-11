package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.state.task.EndGameTask;
import marcos.devsoftware.skywars.utility.MessageUtility;
import marcos.devsoftware.skywars.utility.Replacer;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

public class EndGameState extends GameState {

    private EndGameTask endGameTask;
    private BukkitTask task;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        Player winner = (getGame().getEachPlayer().get(0) == null ? getGame().getEachSpectator().get(0) : getGame().getEachPlayer().get(0));
        winner.setAllowFlight(true);
        winner.setFlying(true);

        Replacer replacer = new Replacer();
        replacer.add("%winner", winner.getName());

        getGame().getEachSpectator().forEach(player -> {
            player.setAllowFlight(true);
            player.setFlying(true);

            if (!player.equals(winner)) {
                MessageUtility.sendTitle(player, MessageUtility.getMessageByTitle("end_game_title", skywarsPlugin), replacer.replace(MessageUtility.getMessageByTitle("end_game_subtitle", skywarsPlugin)), 20, 40, 20);
            }
        });

        MessageUtility.sendTitle(winner, MessageUtility.getMessageByTitle("victory_title", skywarsPlugin), MessageUtility.getMessageByTitle("victory_subtitle", skywarsPlugin), 20, 40, 20);
        getGame().getGameController().sendGameMessage(replacer.replace(MessageUtility.getMessageByTitle("victory_message", skywarsPlugin)));

        this.endGameTask = new EndGameTask(getGame(), skywarsPlugin);
        this.endGameTask.runTaskTimer(skywarsPlugin, 0, 20);
        this.task = Bukkit.getScheduler().runTaskTimer(skywarsPlugin, () -> ToolsUtility.spawnFirework(winner), 0, 20);

        Bukkit.getScheduler().runTaskLater(skywarsPlugin, () -> gameReload(skywarsPlugin), 20 * 15);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.endGameTask != null) this.endGameTask.cancel();
        if (this.task != null) this.task.cancel();
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
    private void onInteract(PlayerInteractEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        event.setCancelled(true);
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!getGame().getGameController().inGame((Player) event.getEntity())) return;

        event.setCancelled(true);
    }

    private void gameReload(SkywarsPlugin skywarsPlugin) {
        for (Player player : getGame().getWorld().getPlayers()) {
            if (player == null) continue;

            if (getGame().getGameController().inGame(player)) {
                getGame().getGameController().removeMatch(player);
            }

            player.teleport(new Location(Bukkit.getWorld("world"), -103, 41, 1286));

            player.setAllowFlight(false);
            player.getInventory().clear();
            player.getInventory().setArmorContents(null);
            player.setAllowFlight(false);
            player.setFlying(false);

            player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));
        }

        WorldUtility.deleteWorld(getGame().getWorld());

        Game game = new Game(getGame().getGameFile(), skywarsPlugin);
        skywarsPlugin.getGameManager().getGames().removeIf(gameListed -> gameListed.getWorld().getName().equalsIgnoreCase(getGame().getWorld().getName()));
        skywarsPlugin.getGameManager().getGames().add(game);

        onDisable();
    }
}