package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.game.state.ActiveGameState;
import marcos.devsoftware.skywars.game.state.EndGameState;
import marcos.devsoftware.skywars.hologram.Hologram;
import marcos.devsoftware.skywars.utility.MessageUtility;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class GameController {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;

    @Getter
    private final Map<Chest, Hologram> chestHologramMap;
    @Getter
    private final Map<UUID, List<Location>> playerCageMap;
    @Getter
    private final Map<UUID, Integer> playerKillMap;
    @Getter
    private final Map<UUID, Location> playerSpawnMap;

    @Getter
    @Setter
    private int timeUntilStartMatch;
    @Getter
    @Setter
    private int refilTime;

    public GameController(Game game, SkywarsPlugin skywarsPlugin) {
        this.game = game;
        this.skywarsPlugin = skywarsPlugin;

        this.chestHologramMap = new HashMap<>();
        this.playerCageMap = new HashMap<>();
        this.playerKillMap = new HashMap<>();
        this.playerSpawnMap = new HashMap<>();

        this.timeUntilStartMatch = 30;
        this.refilTime = 180000;
    }

    public void joinMatch(Player player) {
        if (isState(new ActiveGameState()) || isState(new EndGameState())) {
            activeSpectatorSettings(player);
        } else {
            if (skywarsPlugin.getGameManager().findGameByPlayer(player).isPresent()) {
                MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("already_in_game", skywarsPlugin));
                return;
            }

            activePlayerSettings(player);
            playerToCage(player);

            sendGameMessage(MessageUtility.getMessageByTitle("join_match", skywarsPlugin).replace("%player", player.getName()).replace("%current", String.valueOf(game.getPlayers().size())).replace("%max", String.valueOf(game.getMaxPlayers())));
        }
    }

    public void removeMatch(Player player) {
        activeSpectatorSettings(player);

        if (isState(new EndGameState()) || isSpectator(player)) return;

        String leaveMessage = MessageUtility.getMessageByTitle("leave_match", skywarsPlugin);
        if (isState(new ActiveGameState())) {
            leaveMessage = MessageUtility.getMessageByTitle("abandon_match", skywarsPlugin);
        }

        sendGameMessage(leaveMessage.replace("%player", player.getName()).replace("%current", String.valueOf(game.getPlayers().size())).replace("%max", String.valueOf(game.getMaxPlayers())));
    }

    public void playerToCage(Player player) {
        game.getSpawnPoints().forEach(spawnPoint -> {
            if (playerSpawnMap.containsValue(spawnPoint) || playerSpawnMap.containsKey(player.getUniqueId())) return;

            playerSpawnMap.put(player.getUniqueId(), spawnPoint);
            player.teleport(spawnPoint);

            constructCage(player);
        });
    }

    public void constructCage(Player player) {
        List<Location> playerCoordinatesList = new ArrayList<>();

        ConfigurationFile cageFile = new ConfigurationFile("default.yml", skywarsPlugin.getConfigurationManager().getCagesFolder());
        Material material = Material.getMaterial(cageFile.getConfiguration().getString("material"));
        cageFile.getConfiguration().getStringList("coordinates").forEach(coordinate -> {
            Location newLocation = player.getLocation().clone();
            String[] coordinateArray = coordinate.split(",");

            newLocation.add(Integer.parseInt(coordinateArray[0]), Integer.parseInt(coordinateArray[1]), Integer.parseInt(coordinateArray[2]));
            newLocation.getWorld().getBlockAt(newLocation).setType(material);

            playerCoordinatesList.add(newLocation);
        });

        playerCageMap.put(player.getUniqueId(), playerCoordinatesList);
    }

    public void sendGameTitle(String title, String subTitle, int fadeIn, int displayTime, int fadeOut) {
        for (UUID uuid : game.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            MessageUtility.sendTitle(player, title, subTitle, fadeIn, displayTime, fadeOut);
        }

        for (UUID uuid : game.getSpectators()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            MessageUtility.sendTitle(player, title, subTitle, fadeIn, displayTime, fadeOut);
        }
    }

    public void sendGameMessage(String message) {
        for (UUID uuid : game.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            MessageUtility.sendMessage(player, message);
        }

        for (UUID uuid : game.getSpectators()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;

            MessageUtility.sendMessage(player, message);
        }
    }

    public void activePlayerSettings(Player player) {
        game.getSpectators().removeIf(uuid -> uuid.equals(player.getUniqueId()));
        game.getPlayers().removeIf(uuid -> uuid.equals(player.getUniqueId()));
        game.getPlayers().add(player.getUniqueId());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20.0);
        player.setFoodLevel(20);
    }

    public void activeSpectatorSettings(Player player) {
        game.getPlayers().removeIf(uuid -> uuid.equals(player.getUniqueId()));
        game.getSpectators().removeIf(uuid -> uuid.equals(player.getUniqueId()));
        game.getSpectators().add(player.getUniqueId());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.SURVIVAL);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealth(20.0);
        player.setFoodLevel(20);

        if (playerCageMap.containsKey(player.getUniqueId())) {
            playerCageMap.get(player.getUniqueId()).forEach(location -> location.getWorld().getBlockAt(location).setType(Material.AIR));
            playerCageMap.remove(player.getUniqueId());
        }

        if (playerSpawnMap.containsKey(player.getUniqueId())) {
            player.teleport(playerSpawnMap.get(player.getUniqueId()));
        } else {
            if (game.getSpawnPoints().get(0) == null) {
                player.teleport(game.getMaxLocation());
            } else {
                player.teleport(game.getSpawnPoints().get(0));
            }
        }
    }

    public boolean isPlayer(Player player) {
        return game.getPlayers().contains(player.getUniqueId());
    }

    public boolean isSpectator(Player player) {
        return game.getSpectators().contains(player.getUniqueId());
    }

    public boolean inGame(Player player) {
        return game.getPlayers().contains(player.getUniqueId()) || game.getSpectators().contains(player.getUniqueId());
    }

    public boolean isState(GameState gameState) {
        return game.getGameState().getClass() == gameState.getClass();
    }

    public void setState(GameState gameState, SkywarsPlugin skywarsPlugin) {
        if (game.getGameState() != null) {
            if (isState(gameState)) return;

            game.getGameState().onDisable();
        }

        game.setGameState(gameState);
        game.getGameState().setGame(game);
        game.getGameState().onEnable(skywarsPlugin);
    }
}