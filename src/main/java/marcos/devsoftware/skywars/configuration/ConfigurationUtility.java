package marcos.devsoftware.skywars.configuration;

import lombok.experimental.UtilityClass;
import marcos.devsoftware.skywars.SkywarsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@UtilityClass
public class ConfigurationUtility {

    public Location readLocation(World world, String locationString) {
        String[] locationArray = locationString.split(",");

        double x = Double.parseDouble(locationArray[0].split(":")[1]);
        double y = Double.parseDouble(locationArray[1].split(":")[1]);
        double z = Double.parseDouble(locationArray[2].split(":")[1]);
        float yaw = (float) Double.parseDouble(locationArray[3].split(":")[1]);
        float pitch = (float) Double.parseDouble(locationArray[4].split(":")[1]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public String locationToString(Location location) {
        return "x:" + location.getX() + ", y:" + location.getY() + ", z:" + location.getZ() + ", yaw:" + location.getYaw() + ", pitch:" + location.getPitch();
    }

    public void saveLocation(ConfigurationFile gameFile, String sectionName, Location location) {
        gameFile.getConfiguration().set(sectionName, locationToString(location));
    }

    public ConfigurationFile createFile(String fileName, SkywarsPlugin skywarsPlugin) {
        File file = new File(skywarsPlugin.getDataFolder(), fileName);
        if (!file.exists()) {
            skywarsPlugin.saveResource(fileName, true);
        }

        return new ConfigurationFile(fileName, skywarsPlugin.getDataFolder());
    }

    public File createDirectory(String folderName, SkywarsPlugin skywarsPlugin) {
        File file = new File(skywarsPlugin.getDataFolder(), folderName);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }
}