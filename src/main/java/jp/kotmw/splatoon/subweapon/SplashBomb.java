package jp.kotmw.splatoon.subweapon;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BombType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.subweapon.threads.SplashBombRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownExpBottle;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SplashBomb extends SubWeapon {

	public SplashBomb() {
		super(BombType.SplashBomb, "bullet_splashbomb");
	}

	@Override
	public boolean doOnInteract(PlayerInteractEvent e, PlayerData player, Player p) {
		SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(player.getWeapon()).getSubWeapon());
		if(p.getExp() < subweapon.getCost()) {
			MainGame.sendActionBar(player, ChatColor.RED+"インクがありません!");
			return false;
		}
		launch(p, subweapon);
		e.setCancelled(true);
		return true;
	}




	private void launch(Player player, SubWeaponData data) {
		float ink = player.getExp();
		player.setExp((float) (ink-data.getCost()));
		TNTPrimed tntprimed = (TNTPrimed) player.getLocation().getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.PRIMED_TNT);
		tntprimed.setFuseTicks(120*20);
		tntprimed.setYield(0);
		tntprimed.setVelocity(player.getLocation().getDirection());
		tntprimed.setCustomName(bulletname);
		new SplashBombRunnable(DataStore.getPlayerData(player.getName()), tntprimed).runTaskTimer(Main.main, 0, 1);
		player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT,1,1);

	}
	@Override
	public void doOnExex(ExpBottleEvent e, PlayerData data, Player pe) {
	}


}
