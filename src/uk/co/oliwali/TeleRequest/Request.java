package uk.co.oliwali.TeleRequest;

import org.bukkit.entity.Player;

public class Request {
	
	private Player from = null;
	private Player to = null;
	private long startTime;
	
	public Request(Player from, Player to) {
		this.from = from;
		this.to = to;
		this.startTime = System.nanoTime();
	}
	
	public Player getFrom() {
		return from;
	}
	public Player getTo() {
		return to;
	}
	public long getStartTime() {
		return startTime;
	}

}
