package uk.co.oliwali.TeleRequest;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import uk.co.oliwali.DataLog.util.DataLogAPI;

public class TeleRequest extends JavaPlugin {
	
	public String name;
	public String version;
	private boolean usingDataLog;
	private List<Request> requests = new CopyOnWriteArrayList<Request>();

	public void onDisable() {
		Util.info("Version " + version + " disabled!");
	}

	public void onEnable() {
		
		//Set up the basics
		name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        new Permission(this);
        
        //DataLog
        Plugin dl = getServer().getPluginManager().getPlugin("DataLog");
        if (dl != null) {
            usingDataLog = true;
            Util.info("DataLog detected, logging actions to DataLog database");
        }
        
        Util.info("Version " + version + " enabled!");
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {
		
		if (!(sender instanceof Player))
			return false;
		String prefix = cmd.getName();
		Player player = (Player) sender;
		
		if (prefix.equalsIgnoreCase("tpr") && Permission.request(player)) {
			
			if (args.length < 1) {
				 Util.sendMessage(player, "&cYou must supply a player to send a request to!");
				 return true;
			}
			List<Player> matches = getServer().matchPlayer(args[0]);
			if (matches.size() == 0) {
				Util.sendMessage(player, "&cNo players matching that name are online!");
				return true;
			}
			Player target = matches.get(0);
			if (!Permission.respond(target)) {
				Util.sendMessage(player, "&7" + target.getName() + " does not have permission to accept requests!");
				return true;
			}
			for (Request req : requests) {
				if (req.getFrom() == player || req.getTo() == target)
					requests.remove(req);
			}
			requests.add(new Request(player, target));
			Util.sendMessage(player, "&7TelePort request sent to &c" + target.getName());
			Util.sendMessage(target, "&7You have received a teleport request from &c" + player.getName());
			Util.sendMessage(target, "&8 - Type &c/tpa &8to tp them to you or &c/tpd &8to decline");
			if (usingDataLog)
				 DataLogAPI.addEntry(this, "Request", player, player.getLocation(), target.getName());
			return true;
			
		}
		else if (prefix.equalsIgnoreCase("tpa") && Permission.respond(player)) {
			
			Request request = null;
			for (Request req : requests) {
				if (req.getTo() == player)
					request = req;
			}
			if (request == null) {
				Util.sendMessage(player, "&cYou do not have any requests to accept");
				return true;
			}
			if (!request.getFrom().isOnline()) {
				Util.sendMessage(player, "&7" + request.getFrom().getName() + " &cis no longer online, unable to accept request");
				requests.remove(request);
				return true;
			}
			Util.sendMessage(request.getFrom(), "&c" + player.getName() + "&7 accepted your tp request!");
			request.getFrom().teleport(player);
			Util.sendMessage(request.getFrom(), "&7 you have been teleported to them");
			Util.sendMessage(player, "&7Request accepted, teleporting &c" + request.getFrom().getName() + " &7to you");
			requests.remove(request);
			if (usingDataLog)
				 DataLogAPI.addEntry(this, "Accept", player, player.getLocation(), request.getFrom().getName());
			return true;
			
		}
		else if (prefix.equalsIgnoreCase("tpd") && Permission.respond(player)) {
			
			Request request = null;
			for (Request req : requests) {
				if (req.getTo() == player)
					request = req;
			}
			if (request == null) {
				Util.sendMessage(player, "&cYou do not have any requests to decline");
				return true;
			}
			Util.sendMessage(request.getFrom(), "&c" + player.getName() + "&7 declined your tp request!");
			Util.sendMessage(player, "&7Request from &c" + request.getFrom().getName() + " &7declined");
			requests.remove(request);
			if (usingDataLog)
				 DataLogAPI.addEntry(this, "Decline", player, player.getLocation(), request.getFrom().getName());
			return true;
			
		}
		else if (prefix.equalsIgnoreCase("tph")) {
			Util.sendMessage(player, "&c------------------- &7TeleRequest &c-------------------");
			if (Permission.request(player)) Util.sendMessage(player, "&7- &c/tpr <player> &7 <- request a teleport to a player");
			if (Permission.respond(player)) Util.sendMessage(player, "&7- &c/tpa &7<- accept a teleport request");
			if (Permission.respond(player)) Util.sendMessage(player, "&7- &c/tpd &7<- decline a teleport request");
			return true;
			
		}
		
		return false;
		
	}

}
