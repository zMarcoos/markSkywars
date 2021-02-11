package marcos.devsoftware.skywars.configuration;

import lombok.experimental.UtilityClass;
import marcos.devsoftware.skywars.SkywarsPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public void saveLocation(ConfigurationFile gameFile, String sectionName, Location location) {
        gameFile.getConfiguration().set(sectionName, locationToString(location));
    }

    public String locationToString(Location location) {
        return "x:" + location.getX() + ", y:" + location.getY() + ", z:" + location.getZ() + ", yaw:" + location.getYaw() + ", pitch:" + location.getPitch();
    }

    public List<String> locationToStringList(List<Location> locationList) {
        List<String> newLocationList = new ArrayList<>();
        locationList.forEach(location -> newLocationList.add("x:" + location.getX() + ", y:" + location.getY() + ", z:" + location.getZ() + ", yaw:" + location.getYaw() + ", pitch:" + location.getPitch()));

        return newLocationList;
    }

    public List<String> locationChestToList(Map<Location, String> chestMap) {
        List<String> chestList = new ArrayList<>();
        chestMap.forEach((location, type) -> chestList.add("x:" + location.getX() + ", y:" + location.getY() + ", z:" + location.getZ() + ", yaw:" + location.getYaw() + ", pitch:" + location.getPitch() + ", type:" + type));

        return chestList;
    }

    public void serializeItems(ConfigurationFile file, String path, List<ItemStack> itemStackList) {
        YamlConfiguration configuration = file.getConfiguration();

        List<String> list = new ArrayList<>();

        for (ItemStack itemStack : itemStackList) {
            String type = itemStack.getType().toString();
            int amount = itemStack.getAmount();

            if (itemStack.getEnchantments().size() == 0) {
                list.add(type + "," + amount);
            } else {
                Object[] enchantment = itemStack.getEnchantments().keySet().stream().map(Enchantment::getName).toArray();
                list.add(type + "," + amount + "," + enchantment[0]);
            }
        }

        configuration.set(path, list);
        file.save();
    }

    public List<ItemStack> deserializeItems(ConfigurationFile file, String path) {
        YamlConfiguration configuration = file.getConfiguration();

        List<ItemStack> itemStackList = new ArrayList<>();

        for (String itemText : configuration.getStringList(path)) {
            String[] itemArray = itemText.split(",");

            Material material = Material.getMaterial(itemArray[0].toUpperCase());
            int amount = Integer.parseInt(itemArray[1]);

            if (itemArray.length == 4) {
                Enchantment enchantment = Enchantment.getByName(itemArray[2].toUpperCase());
                int level = Integer.parseInt(itemArray[3]);

                ItemStack itemStack = new ItemStack(material, amount);
                itemStack.addEnchantment(enchantment, level);

                itemStackList.add(itemStack);
            } else {
                itemStackList.add(new ItemStack(material, amount));
            }
        }

        return itemStackList;
    }

    public ConfigurationFile createFile(String fileName, SkywarsPlugin skywarsPlugin) {
        File file = new File(skywarsPlugin.getDataFolder(), fileName);
        if (!file.exists()) {
            skywarsPlugin.saveResource(fileName, true);
        }

        return new ConfigurationFile(fileName, skywarsPlugin.getDataFolder());
    }

    public File createDirectory(String folderName, File folder) {
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File file = new File(folder, folderName);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }
}