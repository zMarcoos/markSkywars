package marcos.devsoftware.skywars.game;

import lombok.Getter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.configuration.ConfigurationUtility;
import marcos.devsoftware.skywars.game.state.StartingGameState;
import marcos.devsoftware.skywars.game.state.WaitingGameState;
import marcos.devsoftware.skywars.listeners.AlwaysActiveListener;
import org.bukkit.Bukkit;
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

    public Game saveGame(GameMapSetup gameMapSetup) {
        ConfigurationFile gameFile = new ConfigurationFile(gameMapSetup.getFileName(), skywarsPlugin.getConfigurationManager().getGamesFolder());
        YamlConfiguration configuration = gameFile.getConfiguration();

        configuration.set("displayName", gameMapSetup.getDisplayName());
        configuration.set("originalWorld", gameMapSetup.getWorld().getName());
        configuration.set("maxPlayers", gameMapSetup.getMaxPlayers());
        configuration.set("minPlayers", gameMapSetup.getMinPlayers());
        ConfigurationUtility.saveLocation(gameFile, "maxLocation", ConfigurationUtility.readLocation(gameMapSetup.getWorld(), gameMapSetup.getMaxLocation()));
        ConfigurationUtility.saveLocation(gameFile, "minLocation", ConfigurationUtility.readLocation(gameMapSetup.getWorld(), gameMapSetup.getMinLocation()));
        configuration.set("spawnPoints", gameMapSetup.getSpawnPoints());
        configuration.set("chestPoints", gameMapSetup.getChestPoints());

        gameFile.save();

        Game game = new Game(gameFile, skywarsPlugin);
        games.removeIf(singleGame -> singleGame.getGameFile().getFile().getName().equalsIgnoreCase(game.getGameFile().getFile().getName()));
        games.add(game);

        return game;
    }

    public Optional<Game> findGame(String gameDisplayName) {
        return games.stream().filter(game -> game.getDisplayName().equalsIgnoreCase(gameDisplayName)).findAny();
    }

    public Optional<Game> findGameByPlayer(Player player) {
        return games.stream().filter(game -> game.getGameController().inGame(player)).findAny();
    }

    public Optional<Game> findOpenGame() {
        return games.stream().filter(game -> game.getGameController().isState(new WaitingGameState()) || game.getGameController().isState(new StartingGameState())).findAny();
    }
}