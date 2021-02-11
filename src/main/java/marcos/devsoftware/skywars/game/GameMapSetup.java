package marcos.devsoftware.skywars.game;

import lombok.Getter;
import lombok.Setter;
import marcos.devsoftware.skywars.SkywarsPlugin;
import marcos.devsoftware.skywars.utility.ToolsUtility;
import marcos.devsoftware.skywars.utility.WorldUtility;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GameMapSetup {

    private final SkywarsPlugin skywarsPlugin;

    private String fileName;
    private String displayName;
    private World world;
    private int maxPlayers;
    private int minPlayers;
    private Location maxLocation;
    private Location minLocation;
    private List<Location> spawnPoints;
    private Map<Location, String> chestPoints;

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
        this.chestPoints = new HashMap<>();
    }

    public GameMapSetup(Game game, SkywarsPlugin skywarsPlugin) {
        this.skywarsPlugin = skywarsPlugin;
        this.fileName = game.getGameFile().getName();
        this.displayName = game.getDisplayName();
        this.world = null;
        this.maxPlayers = game.getMaxPlayers();
        this.minPlayers = game.getMinPlayers();
        this.maxLocation = game.getMaxLocation();
        this.minLocation = game.getMinLocation();
        this.spawnPoints = game.getSpawnPoints();
        this.chestPoints = game.getChestPoints();
    }

    public void addSpawnPoint(Location location) {
        if (world == null) {
            setWorld(location.getWorld());
        }

        if (getChestPoints().isEmpty()) {
            Map<Location, String> newChestPoints = WorldUtility.getChestFromWorld(maxLocation, minLocation);
            setChestPoints(newChestPoints);
        }

        spawnPoints.add(location);
    }

    public void setChestType(Location location, String chestType) {
        if (chestPoints.isEmpty()) return;
        if (world == null) {
            setWorld(location.getWorld());
        }

        Map<Location, String> chestPointCopyMap = new HashMap<>(getChestPoints());
        chestPointCopyMap.forEach((chestLocation, type) -> {
            if (ToolsUtility.sameLocation(chestLocation, location)) {
                chestPoints.remove(chestLocation, type);
                System.out.println(chestPoints.size());

                chestPoints.put(location, chestType);
                System.out.println(chestPoints.size());

            }
        });
    }

    public void toGame() {
        skywarsPlugin.getGameManager().saveGame(this);
    }
}