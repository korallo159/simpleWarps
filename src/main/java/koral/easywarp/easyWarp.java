package koral.easywarp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class easyWarp extends JavaPlugin implements Listener, CommandExecutor {

File warpsFile;
YamlConfiguration warps;


    @Override
    public void onEnable() {
            File file = new File(getDataFolder() + File.separator + "config.yml"); //This will get the config file
            if (!file.exists())
            {
                getConfig().options().copyDefaults(true);
                saveConfig();
            }
            else
                {
                saveConfig();
                reloadConfig();
            }




     getServer().getPluginManager().registerEvents(this,this);
     this.getCommand("setwarp");
     this.getCommand("warp");
        this.getCommand("warp").setTabCompleter(new easyWarpTabCompletion(this));
        this.getCommand("delwarp").setTabCompleter(new easyWarpTabCompletion(this));
        this.getCommand("delwarp");
        this.getCommand("warplist");
    }







    @Override
    public void onDisable() {
        this.saveWarpsFile();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
          if(label.equalsIgnoreCase("setwarp") && args.length > 0)
          {
              final String name = player.getDisplayName();
              final String warpname = args[0].toLowerCase();
              final double x = player.getLocation().getX();
              final double y = player.getLocation().getY();
              final double z = player.getLocation().getZ();
              final float yaw = player.getLocation().getYaw();
              final float pitch = player.getLocation().getPitch();
              final String worldName = player.getWorld().getName();
              this.warps.set("Warps." + warpname + "." + ".X", (Object) x);
              this.warps.set("Warps." + warpname + "." + ".Y", (Object) y);
              this.warps.set("Warps." + warpname + "." + ".Z", (Object) z);
              this.warps.set("Warps." + warpname + "." + ".YAW", (Object) yaw);
              this.warps.set("Warps." + warpname + "." + ".PITCH", (Object) pitch);
              this.warps.set("Warps." + warpname + "." + ".WORLD", (Object) worldName);
              this.warps.set("Warps." + warpname + "." + ".warpCreator", (Object) name);
              this.warps.set("Warps." + warpname + "." + ".warpname", (Object) warpname);
              this.saveWarpsFile();
              player.sendMessage(ChatColor.GRAY + getConfig().getString("warpset") + ChatColor.RED + warpname);
          }


        if(label.equalsIgnoreCase("setwarp") && args.length == 0) {
            player.sendMessage(ChatColor.RED +  getConfig().getString("setwarpusage"));
            return true;
        }


          if(label.equalsIgnoreCase("warp") && args.length > 0)
        {

            final String warpname = args[0].toLowerCase();
            if(warpname.equals(this.warps.getString("Warps." + warpname + "." + ".warpname"))) {
                final double x = this.warps.getDouble("Warps." + warpname + "." + ".X");
                final double y = this.warps.getDouble("Warps." + warpname + "." + ".Y");
                final double z = this.warps.getDouble("Warps." + warpname + "." + ".Z");
                final float yaw = (float) this.warps.getLong("Warps." + warpname + "." + ".YAW");
                final float pitch = (float) this.warps.getLong("Warps." + warpname + "." + ".PITCH");
                final World world = Bukkit.getWorld(this.warps.getString("Warps." + warpname + "." + ".WORLD"));
                final Location warp = new Location(world, x, y, z, yaw, pitch);

                player.teleport(warp);
                player.sendMessage(ChatColor.GRAY + getConfig().getString("successteleport") + ChatColor.DARK_RED + warpname);
            }
            else
                player.sendMessage(ChatColor.DARK_RED + args[0].toLowerCase() + ChatColor.RED + getConfig().getString("wrongwarp"));
        }

        if(label.equalsIgnoreCase("warp") && args.length == 0)
        {
            ConfigurationSection cfgList = this.warps.getConfigurationSection("Warps.");
            List<String> list = new ArrayList<>();

            if(cfgList == null || cfgList.getKeys(false).size() == 0)
            {
                player.sendMessage(ChatColor.RED + getConfig().getString("nullwarps"));
                return true;
            }
            for(String warp : cfgList.getKeys(false))
            {
                list.add(warp);
            }
            player.sendMessage(ChatColor.DARK_RED + getConfig().getString("warplist") + ChatColor.RED + list.toString());
        }


        if(label.equalsIgnoreCase("warplist") && args.length == 0)
        {
            ConfigurationSection cfgList = this.warps.getConfigurationSection("Warps.");
            List<String> list = new ArrayList<>();

            if(cfgList == null || cfgList.getKeys(false).size() == 0)
            {
                player.sendMessage(ChatColor.RED + getConfig().getString("nullwarps"));
                return true;
            }
            for(String warp : cfgList.getKeys(false))
            {
                list.add(warp);
            }
            player.sendMessage(ChatColor.DARK_RED + getConfig().getString("warplist") + ChatColor.RED + list.toString());
        }


          if(label.equalsIgnoreCase("delwarp")  && args.length > 0)
          {
              final String warpname = args[0].toLowerCase();
              if(warpname.equals(this.warps.getString("Warps." + warpname + "." + ".warpname")))
              {
                  this.warps.set("Warps." + warpname, (Object) null);
                  this.saveWarpsFile();
                  player.sendMessage(ChatColor.RED + getConfig().getString("warpdeleted"));
              }
              else
                  player.sendMessage(ChatColor.DARK_RED  + args[0] + getConfig().getString("wrongwarp"));
          }
        if(label.equalsIgnoreCase("delwarp") && args.length == 0)
        {
           player.sendMessage(ChatColor.RED +  getConfig().getString("delwarpusage"));
        }


        return true;
    }

    public easyWarp(){
       this.warpsFile = new File(this.getDataFolder(), "Warps.yml");
       this.warps = YamlConfiguration.loadConfiguration(this.warpsFile);
    }


   public void saveWarpsFile(){
        try {
           this.warps.save(this.warpsFile);
        } catch( IOException e ){
             e.printStackTrace();
        }
   }


}
