package marcos.devsoftware.skywars.game;

import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.game.kit.KitManager;
import marcos.devsoftware.skywars.game.scoreboard.BoardManager;
import marcos.devsoftware.skywars.game.state.ActiveGameState;
import marcos.devsoftware.skywars.game.state.EndGameState;
import marcos.devsoftware.skywars.game.state.GameState;
import marcos.devsoftware.skywars.hologram.Hologram;
import marcos.devsoftware.skywars.utility.MessageUtility;
import marcos.devsoftware.skywars.utility.Replacer;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

import java.util.*;

@Getter
@Setter
public class GameController {

    private final Game game;
    private final SkywarsPlugin skywarsPlugin;
    private final BoardManager boardManager;
    private final GameKillController gameKillController;
    private final KitManager kitManager;

    private final Map<Chest, Hologram> chestHologramMap;
    private final Map<UUID, List<Location>> playerCageMap;
    private final Map<UUID, Location> playerSpawnMap;

    private int timeUntilStartMatch;
    private int refilTime;

    public GameController(Game game) {
        this.game = game;
        this.skywarsPlugin = game.getSkywarsPlugin();
        this.boardManager = new BoardManager(game);
        this.gameKillController = new GameKillController(game, skywarsPlugin);
        this.kitManager = new KitManager(skywarsPlugin);

        this.chestHologramMap = new HashMap<>();
        this.playerCageMap = new HashMap<>();
        this.playerSpawnMap = new HashMap<>();

        this.timeUntilStartMatch = 30;
        this.refilTime = 180000;
    }

    public void joinMatch(Player player) {
        if (isState(new ActiveGameState()) || isState(new EndGameState())) {
            activeSpectatorSettings(player);
            return;
        }

        if (skywarsPlugin.getGameManager().findGameByPlayer(player).isPresent()) {
            MessageUtility.sendMessage(player, MessageUtility.getMessageByTitle("already_in_game", skywarsPlugin));
            return;
        }

        activePlayerSettings(player);
        playerToCage(player);

        player.getInventory().setItem(3, ToolsUtility.createItem(Material.NETHER_STAR, "&6Kits &7(Clique direito)", null));
        Replacer replacer = new Replacer();
        replacer.add("%player", player.getName());
        replacer.add("%current", game.getPlayers().size());
        replacer.add("%max", game.getMaxPlayers());

        sendGameMessage(replacer.replace(MessageUtility.getMessageByTitle("join_match", skywarsPlugin)));
    }

    public void removeMatch(Player player) {
        BPlayerBoard board = boardManager.getBoards().get(player);
        board.getScoreboard().getTeams().forEach(team -> team.removePlayer(player));

        boardManager.remove(player);
        if (isSpectator(player)) return;

        activeSpectatorSettings(player);

        if (isState(new EndGameState())) return;

        String leaveMessage = MessageUtility.getMessageByTitle("leave_match", skywarsPlugin);
        if (isState(new ActiveGameState())) {
            leaveMessage = MessageUtility.getMessageByTitle("abandon_match", skywarsPlugin);
        }

        Replacer replacer = new Replacer();
        replacer.add("%player", player.getName());
        replacer.add("%current", game.getPlayers().size());
        replacer.add("%max", game.getMaxPlayers());

        sendGameMessage(replacer.replace(leaveMessage));
    }

    public void playerToCage(Player player) {
        for (Location spawnPoint : game.getSpawnPoints()) {
            if (!playerSpawnMap.containsValue(spawnPoint) && !playerSpawnMap.containsKey(player.getUniqueId())) {
                playerSpawnMap.put(player.getUniqueId(), spawnPoint);

                player.teleport(spawnPoint);

                List<Location> playerCoordinatesList = new ArrayList<>();

                ConfigurationFile cageFile = new ConfigurationFile("default.yml", skywarsPlugin.getConfigurationManager().getCagesFolder());
                Material material = Material.getMaterial(cageFile.getConfiguration().getString("material"));
                for (String coordinate : cageFile.getConfiguration().getStringList("coordinates")) {
                    Location newLocation = player.getLocation().clone();
                    String[] coordinateArray = coordinate.split(",");

                    newLocation.add(Integer.parseInt(coordinateArray[0]), Integer.parseInt(coordinateArray[1]), Integer.parseInt(coordinateArray[2]));
                    newLocation.getWorld().getBlockAt(newLocation).setType(material);

                    playerCoordinatesList.add(newLocation);
                }

                playerCageMap.put(player.getUniqueId(), playerCoordinatesList);
            }
        }
    }

    public void activePlayerSettings(Player player) {
        game.getSpectators().removeIf(uuid -> uuid.equals(player.getUniqueId()));
        game.getPlayers().removeIf(uuid -> uuid.equals(player.getUniqueId()));
        game.getPlayers().add(player.getUniqueId());

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.ADVENTURE);
        player.setHealth(20.0);
        player.setFoodLevel(20);

        game.getEachPlayer().forEach(gamePlayer -> {
            BPlayerBoard board = boardManager.createBoard(gamePlayer);
            Team team = board.getScoreboard().getTeam("alive");

            if (!team.hasPlayer(gamePlayer)) {
                team.addPlayer(gamePlayer);
            }
        });
    }

    public void activeSpectatorSettings(Player player) {
        game.getPlayers().removeIf(uuid -> uuid.equals(player.getUniqueId()));
        game.getSpectators().removeIf(uuid -> uuid.equals(player.getUniqueId()));
        game.getSpectators().add(player.getUniqueId());

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

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setGameMode(GameMode.ADVENTURE);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        player.setAllowFlight(true);
        player.setFlying(true);
        player.setHealth(20.0);
        player.setFoodLevel(20);
    }

    public void sendGameMessage(String message) {
        getAllPlayers().forEach(player -> MessageUtility.sendMessage(player, message));
    }

    public void sendGameTitle(String title, String subTitle, int fadeIn, int displayTime, int fadeOut) {
        getAllPlayers().forEach(player -> MessageUtility.sendTitle(player, title, subTitle, fadeIn, displayTime, fadeOut));
    }

    public Set<Player> getAllPlayers() {
        Set<Player> allPlayersSet = new HashSet<>();

        allPlayersSet.addAll(game.getEachPlayer());
        allPlayersSet.addAll(game.getEachSpectator());

        return allPlayersSet;
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