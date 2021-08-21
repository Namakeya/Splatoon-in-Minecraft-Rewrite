package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.mainweapons.Blaster;
import jp.kotmw.splatoon.mainweapons.MainWeapon;
import jp.kotmw.splatoon.mainweapons.Shooter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BlasterRunnable extends BukkitRunnable {

	String name;
	MainWeapon weapon;

	public BlasterRunnable(String name,MainWeapon weapon) {

		this.name = name;
		this.weapon=weapon;
	}

	@Override
	public void run() {
		PlayerData data = DataStore.getPlayerData(name);
		int tick = data.getTick();

		Player player = Bukkit.getPlayer(name);

		if(tick > 0 && weapon.canShoot(data)) {
			if(!data.isPaint())
				data.setPaint(true);
			tick--;
			data.setTick(tick);
			if(player.getExp() < DataStore.getWeapondata(data.getWeapon()).getCost())
				return;
			weapon.shoot(data);
		} else {
			this.cancel();
			if(data.isPaint())
				data.setPaint(false);
			return;
		}
	}
}
