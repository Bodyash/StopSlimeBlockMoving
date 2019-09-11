package me.bodyash.ssbm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
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
		if (e.getBlocks().stream()
				.anyMatch(singleEventBlock -> materials.stream().anyMatch(mat -> mat == singleEventBlock.getType()))
				|| compareWithDefault(e.getBlocks())) {
			this.alarm(e);
			e.setCancelled(true);
		}
	}

	private boolean compareWithDefault(List<Block> blocks) {
		for (Block block : blocks) {
			if (block.getType() == Material.SLIME_BLOCK || block.getType() == Material.RAIL
					|| block.getType() == Material.ACTIVATOR_RAIL || block.getType() == Material.DETECTOR_RAIL
					|| block.getType() == Material.POWERED_RAIL) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void BlockPistonEvent(BlockPistonRetractEvent e) {
		if (e.getBlocks().stream()
				.anyMatch(singleEventBlock -> materials.stream().anyMatch(mat -> mat == singleEventBlock.getType()))
				|| compareWithDefault(e.getBlocks())) {
			this.alarm(e);
			e.setCancelled(true);
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
					convertMaterials();
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
