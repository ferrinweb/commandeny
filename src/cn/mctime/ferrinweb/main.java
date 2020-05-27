package cn.mctime.ferrinweb;

import java.io.File;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class main extends JavaPlugin implements Listener {
	public String message;
	public String ondeny;
	public String onreload;

	public List<String> cmdlist;

	public boolean inlist;
	
	public String[] cmdis;
	
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		if (!getDataFolder().exists()) {
		      getDataFolder().mkdir();
	    }
		CreateConfig();
		getLogger().info("[指令禁止] 已激活");
		this.getConfigCon();
	}

	public void onDisable() {
		getLogger().info("[指令禁止] 已禁用");
		saveDefaultConfig();
	}
	
	public void getConfigCon(){

		this.message = getConfig().getString("message");
		this.message = this.message.replace("&", "§");
		this.message = this.message.replace("''", "'");
		this.message = ("§c[§6指令禁止§c] " + this.message);
		
		this.ondeny = getConfig().getString("ondeny");
		this.ondeny = this.ondeny.replace("&", "§");
		this.ondeny = this.ondeny.replace("''", "'");

		this.onreload = getConfig().getString("onreload");
		this.onreload = this.onreload.replace("&", "§");
		this.onreload = this.onreload.replace("''", "'");
		this.onreload = ("§c[§6指令禁止§c] " + this.onreload);
		
		this.cmdlist = getConfig().getStringList("commands");
		
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (label.equalsIgnoreCase("cdreload")) {
			if(sender instanceof Player){
				if (sender.hasPermission("cd.reload")||sender.isOp()) {
					reloadConfig();
					saveDefaultConfig();
					this.getConfigCon();
					sender.sendMessage(this.onreload);
					getLogger().info("[指令禁止] 配置文件已重载!");
				} else {
					sender.sendMessage(this.ondeny);
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent event) {
		
        Player player = event.getPlayer();
        
        inlist = false;
        
		String cmd = event.getMessage().toLowerCase().replace(" ", "|");
		int cmdl = cmd.length();
		
        for (int i=0;i<cmdlist.size();i++){
        	
        	String cmdi = cmdlist.get(i).toLowerCase();
    		
        	if(cmdi.contains("^")){
        		cmdis = cmdi.split("\\^");
        		
            	int cmdil = cmdis[0].replace(" ", "|").length();
            	
            	if(cmdil >= cmdl){
            		continue;
            	}else if(cmdil+1 < cmdl){
            		boolean isblank = cmd.substring(cmdil+1, cmdil+2).equals("|");
            		if(!isblank){
            			continue;
            		}else{
            			if(inlist = cmd.substring(1, cmdil+1).equals(cmdis[0].replace(" ", "|"))){
            				dodeny(inlist, player, event, cmdis[1]);
                    		break;
                    	}
            		}
            	}else{
            		if(inlist = cmd.substring(1, cmdil+1).equals(cmdis[0].replace(" ", "|"))){
            			dodeny(inlist, player, event, cmdis[1]);
                		break;
                	}
            	}
            	
            	if(inlist = cmd.substring(1, cmdil+1).equals(cmdis[0].replace(" ", "|"))){
            		dodeny(inlist, player, event, cmdis[1]);
            		break;
            	}
        	}else{
        		int cmdil = cmdi.replace(" ", "|").length();
        		
        		if(cmdil >= cmdl){
            		continue;
            	}else if(cmdil+1 < cmdl){
            		boolean isblank = cmd.substring(cmdil+1, cmdil+2).equals("|");
            		if(!isblank){
            			continue;
            		}else{
            			if(inlist = cmd.substring(1, cmdil+1).equals(cmdi)){
            				dodeny(inlist, player, event, "");
                    		break;
                    	}
            		}
            	}else{
            		if(inlist = cmd.substring(1, cmdil+1).equals(cmdi)){
        				dodeny(inlist, player, event, "");
                		break;
                	}
            	}
            	
            	if(inlist = cmd.substring(1, cmdil+1).equals(cmdi)){
    				dodeny(inlist, player, event, "");
            		break;
            	}
        	}
        	
        }
        
	}
	
	
	public void dodeny(boolean in,Player p, Event e, String perm){
		if (in) {
			if(p.hasPermission("cd.bypass")){
				getLogger().info("[指令禁止] " + p.getName() + " 执行了该指令：" + ((PlayerCommandPreprocessEvent) e).getMessage().toLowerCase());
			}else{
				if(perm!=""){
					if(p.hasPermission(perm)){
						getLogger().info("[指令禁止] " + p.getName() + " 执行了该指令：" + ((PlayerCommandPreprocessEvent) e).getMessage().toLowerCase());
					}else{
						p.sendMessage(this.message);
						getLogger().info("[指令禁止] " + p.getName() + " 试图执行该指令：" + ((PlayerCommandPreprocessEvent) e).getMessage().toLowerCase());
						((PlayerCommandPreprocessEvent) e).setCancelled(true);
					}
				}else{
					p.sendMessage(this.message);
					getLogger().info("[指令禁止] " + p.getName() + " 试图执行该指令：" + ((PlayerCommandPreprocessEvent) e).getMessage().toLowerCase());
					((PlayerCommandPreprocessEvent) e).setCancelled(true);
				}
				
			}
		}
	}
	
	public void CreateConfig() {
	    if (!new File(getDataFolder() + File.separator + "config.yml").exists()) {
	      saveDefaultConfig();
	    }
	    try {
	      reloadConfig();
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	}
}