package marcos.devsoftware.skywars.game.state;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.state.task.WaitingGameTask;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class WaitingGameState extends GameState {

    private WaitingGameTask waitingGameTask;

    @Override
    public void onEnable(SkywarsPlugin skywarsPlugin) {
        super.onEnable(skywarsPlugin);

        this.waitingGameTask = new WaitingGameTask(getGame(), skywarsPlugin);
        this.waitingGameTask.runTaskTimer(skywarsPlugin, 0, 20);
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (this.waitingGameTask != null) this.waitingGameTask.cancel();
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
    private void onInteract(PlayerInteractEvent event) {
        if (!getGame().getGameController().inGame(event.getPlayer())) return;
        if (!event.hasItem() || event.getItem() == null || !event.getItem().hasItemMeta()) return;

        if (getGame().getGameController().isSpectator(event.getPlayer())) {
            event.setCancelled(true);
        }

        String itemName = ChatColor.stripColor(event.getItem().getItemMeta().getDisplayName());

        if (itemName.equalsIgnoreCase("Kits (Clique direito)")) {
            getGame().getGameController().getKitManager().openKitsInventory(event.getPlayer());
        }

        event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event) {
        if (!getGame().getGameController().inGame((Player) event.getWhoClicked())) return;
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) return;
        if (!event.getInventory().getTitle().equalsIgnoreCase("Kits")) return;

        Player player = (Player) (event.getWhoClicked());
        String itemName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

        getGame().getGameController().getKitManager().setKit(player, itemName);

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
}