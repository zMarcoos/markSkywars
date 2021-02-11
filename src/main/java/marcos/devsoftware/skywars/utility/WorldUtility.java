package marcos.devsoftware.skywars.utility;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class WorldUtility {

    public World createWorld(World originalWorld, String newWorldName) {
        copyWorld(originalWorld.getWorldFolder(), new File(Bukkit.getWorldContainer(), newWorldName));

        World world = new WorldCreator(newWorldName).createWorld();
        world.setTime(0);
        world.setDifficulty(Difficulty.NORMAL);
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setStorm(false);
        world.setAutoSave(false);

        return world;
    }

    @SneakyThrows
    private void copyWorld(File src, File dest) {
        if (src.getName().equalsIgnoreCase("uid.dat")) return;

        if (src.isDirectory()) {
            if (!dest.exists()) {
                dest.mkdir();
            }

            String[] files = src.list();

            if (files != null) {
                for (String file : files) {
                    File srcFile = new File(src, file);
                    File destFile = new File(dest, file);
                    copyWorld(srcFile, destFile);
                }
            }
        } else {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dest);

            byte[] buffer = new byte[1024];

            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }

            in.close();
            out.close();
        }
    }

    private boolean worldDelete(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    worldDelete(file);
                } else {
                    file.delete();
                }
            }
        }

        return path.delete();
    }

    public void deleteWorld(World world) {
        if (world == null) return;
        Bukkit.getServer().unloadWorld(world, false);

        File deleteFolder = world.getWorldFolder();
        worldDelete(deleteFolder);
    }

    public String worldNameGenerator(String worldName) {
        return worldName + "-" + UUID.randomUUID();
    }

    public Map<Location, String> getChestFromWorld(Location locationOne, Location locationTwo) {
        Map<Location, String> chestList = new HashMap<>();

        int minX = Math.min(locationOne.getBlockX(), locationTwo.getBlockX());
        int minY = Math.min(locationOne.getBlockY(), locationTwo.getBlockY());
        int minZ = Math.min(locationOne.getBlockZ(), locationTwo.getBlockZ());
        int maxX = Math.max(locationOne.getBlockX(), locationTwo.getBlockX());
        int maxY = Math.max(locationOne.getBlockY(), locationTwo.getBlockY());
        int maxZ = Math.max(locationOne.getBlockZ(), locationTwo.getBlockZ());

        for (int x = minX; x < maxX; x++) {
            for (int y = minY; y < maxY; y++) {
                for (int z = minZ; z < maxZ; z++) {
                    Location location = new Location(locationOne.getWorld(), x, y, z);
                    if (location.getBlock().getType() == Material.CHEST) {
                        chestList.put(location, "chest0");
                    }
                }
            }
        }

        return chestList;
    }
}