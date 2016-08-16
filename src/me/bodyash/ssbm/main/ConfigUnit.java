package me.bodyash.ssbm.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigUnit {
	private File configFile;
	private YamlConfiguration config;
	private String noPermMessage = "You don't have permissions to do that!";
	private String notifyMessage = "Someone tries to move SlieBlock at:";
	private String noPermMessagePath = "NoPermMessage";
	private String notifyMessagePath = "notifyMessage";

	public ConfigUnit(Main main) {
		this.configFile = new File(main.getDataFolder(), "config.yml");
		this.config = YamlConfiguration.loadConfiguration((File) this.configFile);
		this.loadConfig();
	}

	public void loadConfig() {
            if (!this.configFile.exists()) {
                System.out.println("[StopSlimeBlockMoving] " + "... Starting config creation ...");
                this.createConfig(); 
            }else{
            	 if (this.config.getString(this.noPermMessagePath).isEmpty()) {
                     System.err.println("[StopSlimeBlockMoving]" + "... Something went wrong while setting the \"NoPermMessage\", using default message (You don't have permissions to do that!). ...");
                     this.noPermMessage = "You don't have permissions to do that!";
                 } else {
                     this.noPermMessage = this.config.getString(noPermMessagePath);
                 }
            	 if (this.config.getString(this.notifyMessagePath).isEmpty()) {
                     System.err.println("[StopSlimeBlockMoving]" + "... Something went wrong while setting the \"notifyMessage\", using default message (Someone tries to move SlieBlock at:). ...");
                     this.notifyMessage = "Someone tries to move SlieBlock at:";
                 } else {
                     this.notifyMessage = this.config.getString(this.notifyMessagePath);
                 }
            	 
            }
	}

	private void createConfig() {
		this.config.options().header(
				"by Bodyash");
		this.config.set(this.notifyMessagePath, (Object) this.notifyMessage);
		this.config.set(this.noPermMessagePath, (Object) this.noPermMessage);
		try {
			this.config.save(this.configFile);
			System.out.println("[StopSlimeBlockMoving] " + "... Finished config creation!");
		} catch (IOException e) {
			System.err.println("[StopSlimeBlockMoving] " + "Can't create config file, see info below:");
			e.printStackTrace();
		}
	}

	public String getNoPermMessage() {
		return noPermMessage;
	}

	public String getNotifyMessage() {
		return notifyMessage;
	}
}
