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
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SuckerBomb extends SubWeapon {

	public SuckerBomb() {
		super(BombType.SuckerBomb, "bullet_suckerbomb");
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
		ThrownExpBottle expBottle = player.launchProjectile(ThrownExpBottle.class, player.getLocation().getDirection());
		expBottle.setCustomName(bulletname);
		player.playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT,1,1);
	}
	@Override
	public void doOnExex(ExpBottleEvent e, PlayerData data, Player pe) {
		//かつてはTNTにNoGravityを設定できなかったのでアーマースタンドに乗せる事にしていたようですが、
		//動作が不安定なので変えました sesamugi
		//ArmorStand stand = (ArmorStand) pe.getWorld().spawnEntity(e.getEntity().getLocation().add(0,-1,0), EntityType.ARMOR_STAND);
		TNTPrimed tntprimed = (TNTPrimed) pe.getLocation().getWorld().spawnEntity(e.getEntity().getLocation().add(0, 1, 0), EntityType.PRIMED_TNT);
		tntprimed.setFuseTicks(2*20);
		tntprimed.setYield(0);
		tntprimed.setCustomName(bulletname);
		tntprimed.setSource(pe);
		tntprimed.setGravity(false);
	}

	public boolean checkOnExplode(ExplosionPrimeEvent e){
		if(isMyBullet(e.getEntity())) {
			if(e.getEntity() instanceof TNTPrimed) {
				TNTPrimed tnt= (TNTPrimed) e.getEntity();
				PlayerData data = DataStore.getPlayerData(tnt.getSource().getName());

				if (data.getArena() != null) {
					if (this.isMyWeaponType(data)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@EventHandler
	public void onExplode(ExplosionPrimeEvent e) {
		if(checkOnExplode(e)){
			doOnExplode(e);
		}
	}

	public void doOnExplode(ExplosionPrimeEvent e) {
		//System.out.println("call");
		Entity entity = e.getEntity();
		//Suckerbombは、ArmorStandにプレイヤーの名前を付けて管理する
		/*
		Entity stand = entity.getVehicle();
		if(stand == null)
			return;
		String name = stand.getCustomName();
		if(name == null)
			return;
		stand.remove();*/
		if(entity instanceof TNTPrimed) {
			TNTPrimed tnt= (TNTPrimed) entity;
			PlayerData data = DataStore.getPlayerData(tnt.getSource().getName());
			SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(data.getWeapon()).getSubWeapon());
			Paint.SpherePaint(entity.getLocation(), subweapon.getExplRadius(), data);
			MainGame.SphereDamager(data, entity.getLocation(), subweapon, subweapon.getExplRadius(), false);
		}
	}
}
