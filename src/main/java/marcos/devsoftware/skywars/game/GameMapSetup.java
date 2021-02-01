package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.configuration.ConfigurationUtility;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class GameMapSetup {

    private final SkywarsPlugin skywarsPlugin;

    private String fileName;
    private String displayName;
    private World world;
    private int maxPlayers;
    private int minPlayers;
    private String maxLocation;
    private String minLocation;
    private List<String> spawnPoints;
    private List<String> chestPoints;

    public GameMapSetup(String fileName, SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
        this.fileName = fileName;
        this.displayName = fileName.replace(".yml", "");
        this.world = null;
        this.maxPlayers = 12;
        this.minPlayers = 2;
        this.maxLocation = null;
        this.minLocation = null;
        this.spawnPoints = new ArrayList<>();
        this.chestPoints = new ArrayList<>();
    }

    public GameMapSetup(Game game, SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
        this.fileName = game.getGameFile().getFile().getName();
        this.displayName = game.getDisplayName();
        this.world = game.getWorld();
        this.maxPlayers = game.getMaxPlayers();
        this.minPlayers = game.getMinPlayers();
        this.maxLocation = null;
        this.minLocation = null;
        this.spawnPoints = new ArrayList<>();
        this.chestPoints = new ArrayList<>();
    }

    public void addSpawnPoint(Location location) {
        if (world == null) {
            setWorld(location.getWorld());
        }

        spawnPoints.add("x:" + location.getX() + ", y:" + location.getY() + ", z:" + location.getZ() + ", yaw:" + location.getYaw() + ", pitch:" + location.getPitch());
    }

    public Game toGame() {
        setChestPoints(WorldUtility.getChestFromWorld(ConfigurationUtility.readLocation(world, maxLocation), ConfigurationUtility.readLocation(world, minLocation)));

        return skywarsPlugin.getGameManager().saveGame(this);
    }
}