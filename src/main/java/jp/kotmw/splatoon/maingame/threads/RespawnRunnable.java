package jp.kotmw.splatoon.maingame.threads;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.util.Title;

public class RespawnRunnable extends BukkitRunnable {

	private int second;
	private Player player;

	public RespawnRunnable(int second, Player player) {
		this.second = second;
		this.player = player;
	}

	@Override
	public void run() {
		if(second > 0) {
			if(!DataStore.getPlayerData(player.getName()).isDead()) {
				this.cancel();
				return;
			}
			Title.sendActionBar(player,
					ChatColor.DARK_GREEN.toString()+ChatColor.BOLD+"復活まで "+ChatColor.WHITE+" [ "+ChatColor.DARK_AQUA+
					ChatColor.BOLD+second+ChatColor.WHITE+" ]");
		} else {
			PlayerData data = DataStore.getPlayerData(player.getName());
			Location loc = DataStore.getArenaData(data.getArena()).getTeamPlayerPosision(data.getTeamid(), 1).convertLocation();
			player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
			player.getInventory().setHeldItemSlot(0);//1.2.8 復帰した時に武器スロットに設定されなかったため
			player.teleport(loc);
			player.setGameMode(GameMode.ADVENTURE);
			player.setVelocity(new Vector());
			player.setExp(0.99f);
			data.setDead(false);
			Title.sendActionBar(player, " ");
			DataStore.getArenaData(data.getArena()).getBossBar().updateLifeBar();
			this.cancel();
		}
		second--;
	}

}
