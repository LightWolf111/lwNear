package lightwolf.lwnear;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class lwNear extends JavaPlugin implements CommandExecutor {
    public void onEnable() {
        this.saveDefaultConfig();
        ((PluginCommand)Objects.requireNonNull(this.getCommand("near"))).setExecutor(this);
        this.getLogger().info("&blwNear &fv1.0"); // Изменил § на &
        this.getLogger().info("&fРазработчик: &bLightWolf"); // Изменил § на &
    }

    private boolean hasSpecialPermission(Player player) {
        return player.isOp() && this.getConfig().getBoolean("hideOp") || player.hasPermission("lwnear.nosee");
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (!player.hasPermission("lwNear.use")) {
                player.sendMessage(Objects.requireNonNull(this.getConfig().getString("noPermissions")).replace('&', '§'));
                return true;
            }

            double radius = player.hasPermission("lwNear.donate") ? this.getConfig().getDouble("radiusDonate") : this.getConfig().getDouble("radius");
            List<String> playerNames = new ArrayList<>();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (!p.equals(player) && player.getWorld() == p.getWorld() && !this.hasSpecialPermission(p)) {
                    Location loc = player.getLocation();
                    Location targetLoc = p.getLocation();
                    double distance = loc.distance(targetLoc);
                    if (distance <= radius) {
                        String playerName = p.getName();
                        playerNames.add("&7- &3" + playerName + " &f" + direction(player, p) + "&e" + Math.round(distance) + "&fm");
                    }
                }
            }

            if (playerNames.isEmpty()) {
                player.sendMessage(Objects.requireNonNull(this.getConfig().getString("noPlayers")).replace('&', '§'));
            } else {
                player.sendMessage(Objects.requireNonNull(this.getConfig().getString("nearPlayers")).replace('&', '§'));
                for (String playerName : playerNames) {
                    player.sendMessage(playerName.replace('&', '§'));
                }
            }
        }

        return true;
    }

    public static String direction(Player playerya, Player playerty) {
        String arrow = "↑";
        double angleDiff = getAngleDiff(playerya, playerty);
        if (!(angleDiff < 22.5D) && !(angleDiff > 337.5D)) {
            if (angleDiff < 67.5D) {
                arrow = "↘";
            } else if (angleDiff < 112.5D) {
                arrow = "↓";
            } else if (angleDiff < 157.5D) {
                arrow = "↙";
            } else if (angleDiff < 202.5D) {
                arrow = "←";
            } else if (angleDiff < 247.5D) {
                arrow = "↖";
            } else if (angleDiff < 292.5D) {
                arrow = "↑";
            } else if (angleDiff < 337.5D) {
                arrow = "↗";
            }
        } else {
            arrow = "→";
        }

        return arrow;
    }

    private static double getAngleDiff(Player playerya, Player playerty) {
        Location loc1 = playerya.getLocation();
        Location loc2 = playerty.getLocation();
        Vector dir = playerya.getEyeLocation().getDirection();
        double rotY = Math.atan2(dir.getZ(), dir.getX());
        ++rotY;
        double angle = Math.atan2(loc2.getZ() - loc1.getZ(), loc2.getX() - loc1.getX());
        angle = Math.toDegrees(angle);
        rotY = Math.toDegrees(rotY);
        return (angle - rotY + 360.0D) % 360.0D;
    }
}
