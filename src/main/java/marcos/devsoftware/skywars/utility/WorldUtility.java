package marcos.devsoftware.skywars.utility;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.*;

import java.io.*;
import java.util.*;

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
    private void copyWorld(File source, File target) {
        List<String> ignore = new ArrayList<>(Arrays.asList("uid.dat", "session.dat"));
        if (!ignore.contains(source.getName())) {
            if (source.isDirectory()) {
                if (!target.exists()) {
                    target.mkdirs();
                }

                String[] files = source.list();
                for (String file : Objects.requireNonNull(files)) {
                    File srcFile = new File(source, file);
                    File destFile = new File(target, file);
                    copyWorld(srcFile, destFile);
                }
            } else {
                InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }

                in.close();
                out.close();
            }
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

    public List<String> getChestFromWorld(Location locationOne, Location locationTwo) {
        List<String> chestList = new ArrayList<>();

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
                        chestList.add("x:" + location.getX() + ", y:" + location.getY() + ", z:" + location.getZ() + ", yaw:" + location.getYaw() + ", pitch:" + location.getPitch() + ", type:chest" + chestList.size());
                    }
                }
            }
        }

        return chestList;
    }
}