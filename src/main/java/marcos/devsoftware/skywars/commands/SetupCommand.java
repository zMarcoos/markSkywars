package marcos.devsoftware.skywars.commands;

import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.game.Game;
import marcos.devsoftware.skywars.game.GameMapSetup;
import marcos.devsoftware.skywars.utility.MessageUtility;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SetupCommand extends SkywarsModel implements Listener {

    private final SkywarsPlugin skywarsPlugin;
    private final Map<UUID, GameMapSetup> playerSetupMap;

    public SetupCommand(SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
        this.playerSetupMap = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, skywarsPlugin);
    }

    @Override
    public void execute(Player player, String[] args) {
        if (args.length == 0) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("type_anything", skywarsPlugin));
            return;
        }

        if (playerSetupMap.containsKey(player.getUniqueId())) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("already_in_setup", skywarsPlugin));
            return;
        }

        String mapName = args[0];

        Optional<Game> optionalGame = skywarsPlugin.getGameManager().findGame(mapName);
        GameMapSetup gameMapSetup = optionalGame.map(game -> new GameMapSetup(game, skywarsPlugin)).orElseGet(() -> new GameMapSetup(mapName, skywarsPlugin));

        playerSetupMap.put(player.getUniqueId(), gameMapSetup);

        ItemStack confirmItem = ToolsUtility.createItem(Material.SLIME_BALL, MessageUtility.getMessageByTitle("confirm_itemName", skywarsPlugin), null);
        ItemStack cancelItem = ToolsUtility.createItem(Material.BARRIER, MessageUtility.getMessageByTitle("cancel_itemName", skywarsPlugin), null);
        ItemStack spawnItem = ToolsUtility.createItem(Material.EGG, MessageUtility.getMessageByTitle("spawn_itemName", skywarsPlugin), null);
        ItemStack borderItem = ToolsUtility.createItem(Material.NETHER_STAR, MessageUtility.getMessageByTitle("border_itemName", skywarsPlugin), null);
        ItemStack chestItem = ToolsUtility.createItem(Material.CHEST, MessageUtility.getMessageByTitle("chest_itemName", skywarsPlugin), "chest0");

        player.getInventory().clear();
        player.getInventory().addItem(confirmItem, cancelItem, spawnItem, borderItem, chestItem);
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        if (!playerSetupMap.containsKey(event.getPlayer().getUniqueId())) return;
        if (!event.hasItem() || event.getItem() == null || !event.getItem().hasItemMeta()) return;

        Player player = event.getPlayer();
        GameMapSetup gameMapSetup = playerSetupMap.get(player.getUniqueId());

        switch (event.getItem().getType()) {
            case SLIME_BALL:
                gameMapSetup.toGame();

                playerSetupMap.remove(player.getUniqueId());
                player.getInventory().clear();

                MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("confirm_sucess", skywarsPlugin));
                MessageUtility.sendMessage(player, "&eEsta arena possui " + gameMapSetup.getChestPoints().size() + " baús e " + gameMapSetup.getSpawnPoints().size() + " spawnPoints. ");
                break;
            case BARRIER:
                playerSetupMap.remove(player.getUniqueId());
                player.getInventory().clear();

                MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("cancel_sucess", skywarsPlugin));
                break;
            case EGG:
                gameMapSetup.addSpawnPoint(player.getLocation());
                MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("spawn_sucess", skywarsPlugin));
                break;
            case NETHER_STAR:
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    gameMapSetup.setMaxLocation(player.getLocation());
                } else if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    gameMapSetup.setMinLocation(player.getLocation());
                }

                MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("border_sucess", skywarsPlugin));
                break;
            default:
                return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    private void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        if (!playerSetupMap.containsKey(event.getPlayer().getUniqueId())) return;

        String message = event.getMessage();
        if (message.startsWith("/")) return;
        if (event.getPlayer().getItemInHand().getType() != Material.CHEST) {
            MessageUtility.sendMessage(event.getPlayer(), MessageUtility.getMessageByTitle("chest_on_hand", skywarsPlugin));
            event.setCancelled(true);
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemStack = event.getPlayer().getItemInHand();

        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(Collections.singletonList(MessageUtility.format(message)));
        itemStack.setItemMeta(itemMeta);

        MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("chest_set", skywarsPlugin));

        event.setCancelled(true);
    }

    @EventHandler
    private void onBlockBreak(BlockBreakEvent event) {
        if (!playerSetupMap.containsKey(event.getPlayer().getUniqueId())) return;
        if (event.getPlayer().getItemInHand().getType() != Material.CHEST || event.getBlock().getType() != Material.CHEST)
            return;
        Player player = event.getPlayer();
        GameMapSetup gameMapSetup = playerSetupMap.get(player.getUniqueId());

        ItemStack itemStack = player.getItemInHand();
        ItemMeta itemMeta = itemStack.getItemMeta();
        List<String> lore = itemMeta.getLore();

        gameMapSetup.setChestType(event.getBlock().getLocation(), lore.get(0));
        MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("chest_set", skywarsPlugin));

        event.setCancelled(true);
    }
}