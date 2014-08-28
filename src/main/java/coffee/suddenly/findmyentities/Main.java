package coffee.suddenly.findmyentities;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
    
    private static int entityLimit;
    
    @Override
    public void onEnable() {
        saveDefaultConfig();
        entityLimit = this.getConfig().getInt("entityAmount", 50);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Findmyentities command
	if(cmd.getName().equalsIgnoreCase("findmyentities")) {
            if(args.length == 1) {
                if(sender instanceof Player) {
                    if(args[0].equalsIgnoreCase("entities") || args[0].equalsIgnoreCase("tileentities")) {
                        String type = args[0];
                        Chunk[] chunks = ((Player) sender).getWorld().getLoadedChunks();
                        actuallyDoIt(sender, chunks, type);
                    } else {
                        sender.sendMessage("Please specify whether to search for entities or tileentities.");
                    }
                } else {
                    sender.sendMessage("Please specify a world.");
                }
            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("entities") || args[0].equalsIgnoreCase("tileentities")) {
                    if(getServer().getWorld(args[1]) != null) {
                        String type = args[0];
                        Chunk[] chunks = getServer().getWorld(args[1]).getLoadedChunks();
                        actuallyDoIt(sender, chunks, type);
                    } else {
                        sender.sendMessage("Please specify an actual world.");
                    }
                } else {
                    sender.sendMessage("Please specify whether to search for entities or tileentities.");
                }
            } else {
                return false;
            }
            
        //Teleporttochunk command
        } else if (cmd.getName().equalsIgnoreCase("teleporttochunk")) {
            if(sender instanceof Player) {
                if(args.length == 2) {
                    try {
                        double x = Double.parseDouble(args[0]) * 16;
                        double z = Double.parseDouble(args[1]) * 16;
                        ((Player) sender).teleport(new Location(((Player) sender).getWorld(), x, 256, z));
                        sender.sendMessage("Teleporting you to chunk " + args[0] + ", " + args[1]);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                } else if (args.length == 3) {
                    try {
                        double x = Double.parseDouble(args[0]) * 16;
                        double z = Double.parseDouble(args[1]) * 16;
                        if(getServer().getWorld(args[2]) == null) {
                            sender.sendMessage("Invalid world specified.");
                        } else {
                            ((Player) sender).teleport(new Location(getServer().getWorld(args[2]), x, 256, z));
                            sender.sendMessage("Teleporting you to chunk " + args[0] + ", " + args[1] + " in world " + args[2]);
                        }
                    } catch (NumberFormatException e) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }
    
    private void actuallyDoIt(CommandSender sender, Chunk[] chunks, String type) {
        if(type.equalsIgnoreCase("tileentities")) {
            for(Chunk chunk : chunks) {
                if(chunk.getTileEntities().length >= entityLimit) {
                    sender.sendMessage(chunk.getTileEntities().length + " tile entities were found in the chunk at " + chunk.getX() + ", " + chunk.getZ());
                }
            }
        } else if(type.equalsIgnoreCase("entities")) {
            for(Chunk chunk : chunks) {
                if(chunk.getEntities().length >= entityLimit) {
                    sender.sendMessage(chunk.getEntities().length + " tile entities were found in the chunk at " + chunk.getX() + ", " + chunk.getZ());
                }
            }
        }
        sender.sendMessage("Finished searching.");
    }
}