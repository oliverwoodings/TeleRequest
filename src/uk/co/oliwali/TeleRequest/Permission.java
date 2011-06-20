package uk.co.oliwali.TeleRequest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class Permission {
	
	private TeleRequest plugin;
	private static PermissionPlugin handler = PermissionPlugin.OP;
	private static PermissionHandler permissionPlugin;
	
	public Permission(TeleRequest instance) {
		plugin = instance;
        Plugin permissions = plugin.getServer().getPluginManager().getPlugin("Permissions");
        
        if (permissions != null) {
        	permissionPlugin = ((Permissions)permissions).getHandler();
        	handler = PermissionPlugin.PERMISSIONS;
        	Util.info("Using Permissions for user permissions");
        }
        else {
        	Util.info("No permission handler detected, only ops can use commands");
        }
	}
	
	private static boolean hasPermission(CommandSender sender, String node) {
		if (!(sender instanceof Player))
			return true;
		Player player = (Player)sender;
		switch (handler) {
			case PERMISSIONS:
				return permissionPlugin.has(player, node);
			case OP:
				return player.isOp();
		}
		return false;
	}
	
	public static boolean request(CommandSender player) {
		return hasPermission(player, "telerequest.request");
	}
	
	public static boolean respond(CommandSender player) {
		return hasPermission(player, "telerequest.respond");
	}
	
	public static boolean ignore(CommandSender player) {
		return hasPermission(player, "telerequest.ignore");
	}
	
	private enum PermissionPlugin {
		PERMISSIONS,
		OP
	}

}
