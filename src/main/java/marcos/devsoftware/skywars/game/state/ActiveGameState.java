package marcos.devsoftware.skywars.game.state;

import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.kit.Kit;
import marcos.devsoftware.skywars.game.state.task.ActiveGameTask;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Team;

public class ActiveGameState extends GameState {

    private ActiveGameTask activeGameTask;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        getGame().getEachPlayer().forEach(gamePlayer -> {
            gamePlayer.setGameMode(GameMode.SURVIVAL);
            gamePlayer.getInventory().clear();

            BPlayerBoard board = getGame().getGameController().getBoardManager().getBoards().get(gamePlayer);
            board.getScoreboard().getTeams().forEach(team -> team.removePlayer(gamePlayer));

            if (getGame().getGameController().getKitManager().hasKit(gamePlayer)) {
                Kit kit = getGame().getGameController().getKitManager().getPlayerKit(gamePlayer);
                gamePlayer.getInventory().addItem(kit.getItems().toArray(new ItemStack[0]));
            }

            getGame().getEachPlayer().forEach(player -> {
                Team greenTeam = board.getScoreboard().getTeam("alive");
                Team redTeam = board.getScoreboard().getTeam("enemy");

                if (player == gamePlayer) {
                    greenTeam.addPlayer(player);
                } else {
                    redTeam.addPlayer(player);
                }
            });
        });

        this.activeGameTask = new ActiveGameTask(getGame(), skywarsPlugin);
        this.activeGameTask.runTaskTimer(skywarsPlugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.activeGameTask != null) this.activeGameTask.cancel();
    }

    @EventHandler
    private void onChangeWorld(PlayerChangedWorldEvent event) {
        if (getGame().getGameController().inGame(event.getPlayer())) {
            getGame().getGameController().removeMatch(event.getPlayer());

            if (getGame().getPlayers().size() == 1) {
                getGame().getGameController().setState(new EndGameState(), getGame().getSkywarsPlugin());
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

        if (getGame().getPlayers().size() == 1) {
            getGame().getGameController().setState(new EndGameState(), getGame().getSkywarsPlugin());
        }
    }

    @EventHandler
    private void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!getGame().getGameController().inGame((Player) event.getEntity())) return;
        Player player = (Player) event.getEntity();

        if (event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            if (!getGame().getGameController().inGame(damager)) return;

            if (getGame().getGameController().isSpectator(damager)) {
                event.setCancelled(true);
                return;
            }

            getGame().getGameController().getGameKillController().getPlayerKillCacheMap().put(player.getUniqueId(), damager.getUniqueId());

        } else {
            if (!(event.getDamager() instanceof Projectile)) return;
            Projectile projectile = (Projectile) event.getDamager();

            Player damager = (Player) projectile.getShooter();
            if (damager.equals(player)) return;

            getGame().getGameController().getGameKillController().getPlayerKillCacheMap().put(player.getUniqueId(), damager.getUniqueId());

            if (projectile.getName().equalsIgnoreCase("arrow")) {
                MessageUtility.sendMessage(damager, "&e" + player.getName() + " está com &c" + ((int) player.getHealth() - event.getDamage()) + " &ede HP!");
            }
        }

        if (event.getDamage() >= player.getHealth()) {
            getGame().getGameController().getGameKillController().killPlayer(player);

            if (getGame().getPlayers().size() == 1) {
                getGame().getGameController().setState(new EndGameState(), getGame().getSkywarsPlugin());
            }
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!getGame().getGameController().inGame((Player) event.getEntity())) return;
        if (activeGameTask.getTimeUntilDamage() != 0) {
            event.setCancelled(true);
        }

        Player player = (Player) event.getEntity();
        if (getGame().getGameController().isSpectator(player)) {
            Location location = getGame().getGameController().getPlayerSpawnMap().get(player.getUniqueId());
            if (location == null) {
                player.teleport(getGame().getMaxLocation());
            } else {
                player.teleport(location);
            }

            event.setCancelled(true);
            return;
        }

        if (event.getDamage() >= player.getHealth()) {
            getGame().getGameController().getGameKillController().killPlayer(player);

            if (getGame().getPlayers().size() == 1) {
                getGame().getGameController().setState(new EndGameState(), getGame().getSkywarsPlugin());
            }
        }
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        if (getGame().getGameController().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        if (getGame().getGameController().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;

        if (getGame().getGameController().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }

        if (event.getBlock().getType() == Material.CHEST) {
            MessageUtility.sendMessage(event.getPlayer(), MessageUtility.getMessageByTitle("cant_break", getGame().getSkywarsPlugin()));
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onFood(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!getGame().getGameController().inGame((Player) event.getEntity())) return;

        if (getGame().getGameController().isSpectator((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}