package jp.kotmw.splatoon.maingame.threads;

import jp.kotmw.splatoon.gamedatas.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.manager.SplatColorManager;

public class DamageHealthRunnable extends BukkitRunnable {

	private String name;
	PotionEffect slowness = new PotionEffect(PotionEffectType.SLOW, 10, 2, false, false);
	
	public DamageHealthRunnable(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		//System.out.println("call");
		if(!DataStore.hasPlayerData(name)) {
			this.cancel();
			return;
		}
		PlayerData data = DataStore.getPlayerData(name);
		Player player = Bukkit.getPlayer(name);
		if(SplatColorManager.isBelowBlockTeamColor(player, true)) {
			double health=player.getHealth()+3.0;
			health=health<20?health:20;
			if(player.getHealth() < 20.0 && data.isSquidMode()){
				player.setHealth(health);
			}
		}
		else if(SplatColorManager.isBelowBlockTeamColor(player, false)) {
			if(player.getHealth() >= 14.0) player.damage(2.0);
			player.addPotionEffect(slowness);
		}
	}
}
