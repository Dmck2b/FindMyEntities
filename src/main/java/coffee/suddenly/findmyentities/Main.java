package coffee.suddenly.findmyentities;

import static java.lang.Integer.parseInt;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
    
    private static int entityLimit;
    private boolean argCheck = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        entityLimit = this.getConfig().getInt("entityAmount", 50);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Findmyentities command
	if(cmd.getName().equalsIgnoreCase("findmyentities")) {
            if(args.length == 1) { // Normal loaded chunks check
                if(sender instanceof Player) {
                    if(args[0].equalsIgnoreCase("entities") || args[0].equalsIgnoreCase("tileentities")) {
                        String type = args[0];
                        Chunk[] chunks = ((Player) sender).getWorld().getLoadedChunks();
                        actuallyDoItLoadedChunk(sender, chunks, type);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please specify whether to search for entities or tileentities.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Please specify a world.");
                }
            } else if(args.length == 2) { // Loaded chunks in a specified world
                if(args[0].equalsIgnoreCase("entities") || args[0].equalsIgnoreCase("tileentities")) {
                    if(getServer().getWorld(args[1]) != null) {
                        String type = args[0];
                        Chunk[] chunks = getServer().getWorld(args[1]).getLoadedChunks();
                        actuallyDoItLoadedChunk(sender, chunks, type);
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please specify an actual world.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Please specify whether to search for entities or tileentities.");
                }
            } else if (args.length == 3) { // Specified radius from 0,0 in the specified world the world
                if(args[2].matches(("\\d+")) ) { // Is it a number? 
                    if(args[0].equalsIgnoreCase("entities") || args[0].equalsIgnoreCase("tileentities")) {
                        if(getServer().getWorld(args[1]) != null) {
                            String type = args[0];
                            World world = getServer().getWorld(args[1]);
                            Integer radius = parseInt(args[2]);
                            sender.sendMessage(ChatColor.GOLD + "Begining search. If you set the radius too large this may take a long time or cause your server to crash.");
                            doItForUnloadedChunks(world, type, sender, radius);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Please specify an actual world.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please specify whether to search for entities or tileentities.");
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
            
        //Teleporttochunk command
        } else if (cmd.getName().equalsIgnoreCase("teleporttochunk")) {
            if(sender instanceof Player) {
                if(args.length > 1) {
                    argCheck = false;
                    if(args[0].contains("-") || args[1].contains("-")) {
                        argCheck = true;
                    }
                    if((args[0].matches("\\d+") && args[1].matches("\\d+")) || argCheck) { // Are they numbers?
                        if(args.length == 2) {
                            double x = Double.parseDouble(args[0]) * 16;
                            double z = Double.parseDouble(args[1]) * 16;
                            ((Player) sender).teleport(new Location(((Player) sender).getWorld(), x, 256, z));
                            sender.sendMessage(ChatColor.GREEN + "Teleporting you to chunk " + args[0] + ", " + args[1]);
                        } else if (args.length == 3) {
                            double x = Double.parseDouble(args[0]) * 16;
                            double z = Double.parseDouble(args[1]) * 16;
                            if(getServer().getWorld(args[2]) == null) {
                                sender.sendMessage(ChatColor.RED + "Invalid world specified.");
                            } else {
                                ((Player) sender).teleport(new Location(getServer().getWorld(args[2]), x, 256, z));
                                sender.sendMessage(ChatColor.GREEN + "Teleporting you to chunk " + args[0] + ", " + args[1] + " in world " + args[2]);
                            }
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please specify numbers for the chunks.");
                    }
                } else {
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Please ensure you are a player to use this command.");
            }
        }
        return true;
    }
    
    private void actuallyDoItLoadedChunk(CommandSender sender, Chunk[] chunks, String type) {
        if(type.equalsIgnoreCase("tileentities")) {
            for(Chunk chunk : chunks) {
                if(chunk.getTileEntities().length >= entityLimit) {
                    sender.sendMessage(ChatColor.GRAY + "" + chunk.getTileEntities().length + ChatColor.GOLD +  " tile entities were found in the chunk at " + chunk.getX() + ", " + chunk.getZ());
                }
            }
        } else if(type.equalsIgnoreCase("entities")) {
            for(Chunk chunk : chunks) {
                if(chunk.getEntities().length >= entityLimit) {
                    sender.sendMessage(ChatColor.GRAY + "" + chunk.getEntities().length + ChatColor.GOLD +  " entities were found in the chunk at " + chunk.getX() + ", " + chunk.getZ());
                }
            }
        }
        sender.sendMessage(ChatColor.GREEN + "Finished searching.");
    }
    
    private void actuallyDoItUnloadedChunk(CommandSender sender, Chunk chunk, String type) {
        if(type.equalsIgnoreCase("tileentities")) {
            if(chunk.getTileEntities().length >= entityLimit) {
                sender.sendMessage(ChatColor.GRAY + "" + chunk.getTileEntities().length + ChatColor.GREEN +  " tile entities were found in the chunk at " + chunk.getX() + ", " + chunk.getZ());
            }
        } else if(type.equalsIgnoreCase("entities")) {
            if(chunk.getEntities().length >= entityLimit) {
                sender.sendMessage(ChatColor.GRAY + "" + chunk.getEntities().length + ChatColor.GREEN + " entities were found in the chunk at " + chunk.getX() + ", " + chunk.getZ());
            }
        }
    }
    
    private void doItForUnloadedChunks(World world, String type, CommandSender sender, Integer radius) {
        int chunkX;
        int chunkZ;

        for(chunkX = 0; chunkX < radius; chunkX++) {
            for(chunkZ = 0; chunkZ < radius; chunkZ++) {
                Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                chunk.load(false);
                actuallyDoItUnloadedChunk(sender, chunk, type);
            }
            for(chunkZ = 0; chunkZ > -radius; chunkZ--) {
                Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                chunk.load(false);
                actuallyDoItUnloadedChunk(sender, chunk, type);
            }
        }
        
        for(chunkX = 0; chunkX > -radius; chunkX--) {
            for(chunkZ = 0; chunkZ > -radius; chunkZ--) {
                Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                chunk.load(false);
                actuallyDoItUnloadedChunk(sender, chunk, type);
            }
            for(chunkZ = 0; chunkZ < radius; chunkZ++) {
                Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                chunk.load(false);
                actuallyDoItUnloadedChunk(sender, chunk, type);
            }
        }
        sender.sendMessage(ChatColor.GREEN + "Finished searching.");
    }
}