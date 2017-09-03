package me.bodyash.ssbm;

import java.util.ArrayList;
import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import me.bodyash.ssbm.ConfigUnit;

public class Main extends JavaPlugin implements Listener {
	private ConsoleCommandSender console;
	private PluginDescriptionFile descFile;
	private ConfigUnit conf;
	private ArrayList<Material> materials = new ArrayList<Material>();

	public void onEnable() {

		
		this.console = this.getServer().getConsoleSender();
		this.descFile = this.getDescription();
		this.conf = new ConfigUnit(this);
		convertMaterials();

		this.console.sendMessage("[StopSlimeBlockMoving] " + (Object) ChatColor.GREEN + "Successfully enabled");
		System.out.println("[" + this.descFile.getName() + "] Version " + this.descFile.getVersion() + " by "
				+ this.descFile.getAuthors() + ".");
		
		getServer().getPluginManager().registerEvents(this, this);

	}

	public void onDisable() {
		this.console.sendMessage("[StopSlimeBlockMoving]" + (Object) ChatColor.GREEN + "Successfully disabled");
	}
	
	private void convertMaterials(){
		for (String textMaterial : this.conf.getBlockDontMoveList()) {
			try {
				Material m = Material.valueOf(textMaterial);
				materials.add(m);
			} catch (Exception e) {
				this.console.sendMessage("[StopSlimeBlockMoving] " + (Object) ChatColor.RED + "CANNOT ADD MATERIAL: " + textMaterial + (Object) ChatColor.GOLD + " use this material names: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void BlockPistonEvent(BlockPistonExtendEvent e) {
		for (org.bukkit.block.Block b : e.getBlocks()) {
			if (b.getType() == Material.SLIME_BLOCK || b.getType() == Material.RAILS
					|| b.getType() == Material.ACTIVATOR_RAIL || b.getType() == Material.DETECTOR_RAIL
					|| b.getType() == Material.POWERED_RAIL) {
				this.alarm(e);
				e.setCancelled(true);
				break;
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void BlockPistonEvent(BlockPistonRetractEvent e) {
		for (org.bukkit.block.Block b : e.getBlocks()) {
			if (b.getType() == Material.SLIME_BLOCK || b.getType() == Material.RAILS
					|| b.getType() == Material.ACTIVATOR_RAIL || b.getType() == Material.DETECTOR_RAIL
					|| b.getType() == Material.POWERED_RAIL) {
				this.alarm(e);
				e.setCancelled(true);
				break;
			}
		}

	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("ssbm") || label.equalsIgnoreCase("stopslimeblockmoving")) {
			this.ssbm(sender, command, label, args);
			return true;
		}
		return false;

	}

	private boolean ssbm(CommandSender sender, Command command, String label, String[] args) {
		if (args.length == 0) {
			if (sender.hasPermission("ssbm.help") || sender.isOp()) {
				sender.sendMessage("[StopSlimeBlockMoving] " + (Object) ChatColor.GOLD + "Plugin version: "
						+ descFile.getVersion() + " by " + descFile.getAuthors());
				return false;
			}
		}
		if (args.length == 1) {
			if (args[0].equalsIgnoreCase("reload")) {
				if (sender.isOp() || sender.hasPermission("ssbm.reload")) {
					conf.loadConfig();
					sender.sendMessage("[StopSlimeBlockMoving] " + (Object) ChatColor.GOLD + "Plugin version: "
							+ descFile.getVersion() + " by " + descFile.getAuthors() + (Object) ChatColor.GREEN
							+ " sucessfuly reloaded");
					return false;
				}
			} else {
				sender.sendMessage("[StopSlimeBlockMoving] " + (Object) ChatColor.DARK_RED + conf.getNoPermMessage());
			}
			if (args[0].equalsIgnoreCase("help")) {
				sender.sendMessage("[StopSlimeBlockMoving] " + (Object) ChatColor.GOLD + "Plugin version: "
						+ descFile.getVersion() + " by " + descFile.getAuthors());
				return false;
			} else {
				sender.sendMessage("[StopSlimeBlockMoving] " + (Object) ChatColor.DARK_RED + conf.getNoPermMessage());
				return false;
			}

		} else {
			sender.sendMessage(
					"[StopSlimeBlockMoving] " + (Object) ChatColor.DARK_RED + "try /ssbm help or /ssbm reload");
		}
		return false;
	}

	private void alarm(BlockEvent e) {
		if (conf.isNotify()) {
			Collection<? extends Player> arrplayer = Bukkit.getOnlinePlayers();
			for (Player player : arrplayer) {
				if (player.isOp() || player.hasPermission("ssbm.notify")) {
					player.sendMessage(
							(Object) ChatColor.YELLOW + "[SSBM] " + (Object) ChatColor.RED + conf.getNotifyMessage());
					player.sendMessage((Object) ChatColor.RED + "X: " + e.getBlock().getLocation().getBlockX() + " Y: "
							+ e.getBlock().getLocation().getBlockY() + " Z: " + e.getBlock().getLocation().getBlockZ()
							+ " in World: " + e.getBlock().getLocation().getWorld().getName());
				}
			}
		}
	}
}
