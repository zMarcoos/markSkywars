package marcos.devsoftware.skywars.game;

import lombok.Getter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.configuration.ConfigurationUtility;
import marcos.devsoftware.skywars.game.state.StartingGameState;
import marcos.devsoftware.skywars.game.state.WaitingGameState;
import marcos.devsoftware.skywars.game.listeners.AlwaysActiveListener;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Getter
public class GameManager {

    private final List<Game> games;

    private final SkywarsPlugin skywarsPlugin;
    private final AlwaysActiveListener alwaysActiveListener;

    public GameManager(SkywarsPlugin skywarsPlugin) {
        this.games = new ArrayList<>();

        this.skywarsPlugin = skywarsPlugin;
        this.alwaysActiveListener = new AlwaysActiveListener();

        for (File singleGameFile : Objects.requireNonNull(skywarsPlugin.getConfigurationManager().getGamesFolder().listFiles())) {
            ConfigurationFile gameFile = new ConfigurationFile(singleGameFile.getName(), skywarsPlugin.getConfigurationManager().getGamesFolder());
            Game game = new Game(gameFile, skywarsPlugin);
            games.add(game);
        }

        Bukkit.getPluginManager().registerEvents(alwaysActiveListener, skywarsPlugin);
    }

    public void onDisable() {
        for (Game game : games) {
            if (game == null) continue;

            for (Player player : game.getGameController().getAllPlayers()) {
                player.teleport(new Location(Bukkit.getWorld("world"), -103, 41, 1286));
                game.getGameController().getBoardManager().remove(player);
            }

            WorldUtility.deleteWorld(game.getWorld());
        }
    }

    public void saveGame(GameMapSetup gameMapSetup) {
        ConfigurationFile gameFile = new ConfigurationFile(gameMapSetup.getFileName(), skywarsPlugin.getConfigurationManager().getGamesFolder());
        YamlConfiguration configuration = gameFile.getConfiguration();

        configuration.set("displayName", gameMapSetup.getDisplayName());
        configuration.set("originalWorld", gameMapSetup.getWorld().getName());
        configuration.set("maxPlayers", gameMapSetup.getMaxPlayers());
        configuration.set("minPlayers", gameMapSetup.getMinPlayers());
        ConfigurationUtility.saveLocation(gameFile, "maxLocation", gameMapSetup.getMaxLocation());
        ConfigurationUtility.saveLocation(gameFile, "minLocation", gameMapSetup.getMinLocation());
        configuration.set("spawnPoints", ConfigurationUtility.locationToStringList(gameMapSetup.getSpawnPoints()));
        configuration.set("chestPoints", ConfigurationUtility.locationChestToList(gameMapSetup.getChestPoints()));

        gameFile.save();

        Game game = new Game(gameFile, skywarsPlugin);
        games.removeIf(singleGame -> singleGame.getGameFile().getFile().getName().equalsIgnoreCase(game.getGameFile().getFile().getName()));
        games.add(game);
    }

    public Optional<Game> findGame(String gameDisplayName) {
        return games.stream().filter(game -> game.getDisplayName().equalsIgnoreCase(gameDisplayName)).findAny();
    }

    public Optional<Game> findGameByPlayer(Player player) {
        return games.stream().filter(game -> game.getGameController().isPlayer(player)).findAny();
    }

    public Optional<Game> findOpenGame() {
        return games.stream().filter(game -> game.getGameController().isState(new WaitingGameState()) || game.getGameController().isState(new StartingGameState())).findAny();
    }
}