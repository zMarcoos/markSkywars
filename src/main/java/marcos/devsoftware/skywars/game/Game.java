package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationFile;
import marcos.devsoftware.skywars.configuration.ConfigurationUtility;
import marcos.devsoftware.skywars.game.state.WaitingGameState;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.*;

@Getter
public class Game {

    private final SkywarsPlugin skywarsPlugin;
    private final GameController gameController;

    private ConfigurationFile gameFile;
    private String displayName;
    private World world;
    private int maxPlayers;
    private int minPlayers;
    private List<Location> spawnPoints;
    private Map<Location, String> chestPoints;
    private Location maxLocation;
    private Location minLocation;

    private List<UUID> players;
    private List<UUID> spectators;
    @Setter private GameState gameState;

    public Game(ConfigurationFile gameFile, SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
        this.gameController = new GameController(this, skywarsPlugin);

        this.gameFile = gameFile;
        YamlConfiguration configuration = gameFile.getConfiguration();

        this.displayName = configuration.getString("displayName");
        this.maxPlayers = configuration.getInt("maxPlayers");
        this.minPlayers = configuration.getInt("minPlayers");
        this.spawnPoints = new ArrayList<>();
        this.chestPoints = new HashMap<>();

        String newWorld = WorldUtility.worldNameGenerator(displayName);
        this.world = WorldUtility.createWorld(Bukkit.getWorld(configuration.getString("originalWorld")), newWorld);

        configuration.set("temporaryWorld", world.getName());
        gameFile.save();

        this.maxLocation = ConfigurationUtility.readLocation(world, configuration.getString("maxLocation"));
        this.minLocation = ConfigurationUtility.readLocation(world, configuration.getString("minLocation"));

        configuration.getStringList("spawnPoints").forEach(spawnPoint -> {
            String[] spawnPointArray = spawnPoint.split(",");

            double x = Double.parseDouble(spawnPointArray[0].split(":")[1]);
            double y = Double.parseDouble(spawnPointArray[1].split(":")[1]);
            double z = Double.parseDouble(spawnPointArray[2].split(":")[1]);
            float yaw = (float) Double.parseDouble(spawnPointArray[3].split(":")[1]);
            float pitch = (float) Double.parseDouble(spawnPointArray[4].split(":")[1]);

            spawnPoints.add(new Location(world, x, y, z, yaw, pitch));
        });

        configuration.getStringList("chestPoints").forEach(chestPoint -> {
            String[] chestPointArray = chestPoint.split(",");

            double x = Double.parseDouble(chestPointArray[0].split(":")[1]);
            double y = Double.parseDouble(chestPointArray[1].split(":")[1]);
            double z = Double.parseDouble(chestPointArray[2].split(":")[1]);
            float yaw = (float) Double.parseDouble(chestPointArray[3].split(":")[1]);
            float pitch = (float) Double.parseDouble(chestPointArray[4].split(":")[1]);
            String chestType = chestPointArray[5].split(":")[1];

            chestPoints.put(new Location(world, x, y, z, yaw, pitch), chestType);
        });

        this.players = new ArrayList<>();
        this.spectators = new ArrayList<>();

        gameController.setState(new WaitingGameState(), skywarsPlugin);
    }
}