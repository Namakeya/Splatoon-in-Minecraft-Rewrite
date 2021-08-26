package jp.kotmw.splatoon.subweapon.threads;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;

public class SplashBombRunnable extends BukkitRunnable {
	private PlayerData data;
	private TNTPrimed bomb;

	public SplashBombRunnable(PlayerData data, TNTPrimed tnt) {
		this.data = data;
		this.bomb = tnt;
	}

	@Override
	public void run() {
		int tick = bomb.getFuseTicks();
		Player pe= Bukkit.getPlayer(data.getName());
		if(bomb.isOnGround() && tick > 20) {
			bomb.setFuseTicks(1*20);
			pe.getWorld().playSound(pe.getLocation(), Sound.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 2f,1f);
		}
		if(tick == 0) {
			SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(data.getWeapon()).getSubWeapon());
			Paint.SpherePaint(bomb.getLocation(), subweapon.getExplRadius(), data);
			MainGame.SphereDamager(data, bomb.getLocation(), subweapon, subweapon.getExplRadius(), false);
			this.cancel();
		}
	}
}
