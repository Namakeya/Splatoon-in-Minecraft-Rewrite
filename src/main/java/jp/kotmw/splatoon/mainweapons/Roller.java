package jp.kotmw.splatoon.mainweapons;

import jp.kotmw.splatoon.maingame.threads.SquidRunnable;
import jp.kotmw.splatoon.mainweapons.threads.RollerBulletRunnable;
import jp.kotmw.splatoon.mainweapons.threads.RollerRollRunnable;
import jp.kotmw.splatoon.mainweapons.threads.ShooterBulletRunnable;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.threads.RollerRunnable;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.Polar_coodinates;

public class Roller extends MainWeapon {

	public Roller() {
		super("bullet_roller", WeaponType.Roller);
	}

	@Override
	public void doOnInteract(PlayerInteractEvent e, PlayerData pd, Player pe) {
		WeaponData weapon = DataStore.getWeapondata(pd.getWeapon());
		if(pe.getExp() < weapon.getInkSplashCost()) {
			MainGame.sendActionBar(pd, ChatColor.RED+"インクがありません!");
			return;
		}
		if(pd.getTask() == null) {
			BukkitRunnable task = new RollerRunnable(pe.getName(),this);
			task.runTaskTimer(Main.main, 0, 1);
			pd.setTask(task);
		}
		//System.out.println("tick : "+pd.getTick());
		if(pd.getTick()<5){
			pd.setTick(weapon.getFirespeed()+5);
			shoot(pd);
			pe.setExp((float) (pe.getExp()-weapon.getInkSplashCost()));
			pe.getWorld().playSound(pe.getLocation(), weapon.getSoundId(), SoundCategory.PLAYERS, weapon.getSoundVolume(), weapon.getSoundPitch());

		}

		if(!pd.isPaint()) {
			pd.setPaint(true);
			//p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 5));

		}
	}

	@Override
	public void doOnHit(ProjectileHitEvent e, PlayerData pd, Player pe) {

		WeaponData weapon=DataStore.getWeapondata(pd.getWeapon());
		//System.out.println("radius : "+weapon.getInkSplashPaintRadius()+" decay : "+getDecayRate(e.getEntity(),weapon));
		double radius=weapon.getInkSplashPaintRadius() * getDecayRate(e.getEntity(),weapon);

		radius=radius<0.8?0.8:radius;
		if(e.getHitBlock()!=null && SquidRunnable.isSlipBlock(e.getHitBlock().getLocation())){
			radius+=1.5;
		}
		Paint.SpherePaint(e.getEntity().getLocation(),radius, pd);
	}

	public boolean checkOnMove(PlayerMoveEvent e){
		if(getArena(e.getPlayer())!=null) {

			Player player = e.getPlayer();
			PlayerData data = DataStore.getPlayerData(player.getName());
			//System.out.println("suitable action");
			if (canShoot(data)) {
				return true;

			}
		}
		return false;
	}

	public void doOnMove(PlayerMoveEvent e,PlayerData pd,Player pe){
		WeaponData weapon = DataStore.getWeapondata(pd.getWeapon());
		//System.out.println(pd.getMotion());
		//System.out.println(pe.getLocation().getDirection());
		//System.out.println(pd.getMotion().dot(pe.getLocation().getDirection()));
		if(pd.getMotion().normalize().dot(pe.getLocation().getDirection())>0.5){
			//向いている方向と移動している方向が同じ = 前に進んでいるなら
			float cost=weapon.getCost();
			if(!pe.isOnGround()){
				cost*=4;
			}
			if(pe.getExp() < cost) {
				MainGame.sendActionBar(pd, ChatColor.RED+"インクがありません!");
				return;
			}
			pe.setExp((float) (pe.getExp()-cost));
			BukkitRunnable task = new RollerRollRunnable(pd.getName(),this,pe.getLocation().clone());
			task.runTaskTimer(Main.main, 0, 1);
		}

	}

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();

		PlayerData data = DataStore.getPlayerData(player.getName());

		//if(!data.isPaint())return;
		if(this.checkOnMove(e)){
			doOnMove(e,data,player);
		}

	}

	@Override
	public void doOnDamage(EntityDamageByEntityEvent e, PlayerData pd, Player pe) {
		Projectile ball= (Projectile) e.getDamager();
		WeaponData data = DataStore.getWeapondata(pd.getWeapon());
		//System.out.println("call5");
		e.setDamage(0);
		MainGame.damageTarget(pd,((LivingEntity) e.getEntity()),data.getInkSplashDamage()*getDecayRate(ball,data));
		((LivingEntity) e.getEntity()).setMaximumNoDamageTicks(1);
	}

	@Override
	public void shoot(PlayerData pd) {
		super.shoot(pd);
		WeaponData weapon=DataStore.getWeapondata(pd.getWeapon());
		Player player=Bukkit.getPlayer(pd.getName());
		for(int i = 0; i<weapon.getInkSplash(); i++) {
			Location loc=player.getLocation().clone();
			loc.setYaw(loc.getYaw()+(float)weapon.getInkSplashAngle()*(((float)i+0.5f)/weapon.getInkSplash()-0.5f));

			float ymotion;
			if(loc.getPitch()>0){
				ymotion=0;
			}else if(loc.getPitch()<-60){
				ymotion=-60;
			}else{
				ymotion=loc.getPitch();
			}

			float pitch;
			if(loc.getPitch()<0){
				pitch=0;
			}else{
				pitch=loc.getPitch();
			}
			//System.out.println(pitch);


			loc.setPitch(pitch);


			Vector direction = loc.getDirection().multiply(weapon.getSpeed());
			Location loc2=player.getLocation().clone();
			loc2.setPitch(0);
			loc2=loc2.add(0,2.5-ymotion*0.03,0);
			Snowball ball=player.getWorld().spawn(loc2,Snowball.class);
			Vector motion=pd.getMotion().multiply(2);
			if(motion.getY()>0.1)motion=motion.setY(0.1);
			//System.out.println(motion);
			ball.setVelocity(motion.add(direction));
			ball.setShooter(player);
			ball.setCustomName(bulletname);

			BukkitRunnable task = new RollerBulletRunnable(player.getName(),ball,this);
			task.runTaskTimer(Main.main, 0, 1);
			//System.out.println(ball.getLocation());



		}
		//Paint.SpherePaint(player.getLocation(),2, pd);
	}



	public static int PlayerDirectionID_Eight(Float dir) {
		int id = -1;
		// 16 = 22.5
		// 8 = 45
		if(((dir >= 0.0&&dir <= 22.55)||(dir >= 337.56&&dir <= 360.0))
				||((dir <= 0.0&&dir >= -22.55)||(dir <= -337.56&&dir >= -360.0)))
			id = 0;
		else if((dir >= 22.56&&dir <= 67.55)
				||(dir <= -292.56&&dir >= -337.55))
			id = 1;
		else if((dir >= 67.56&&dir <= 112.55)
				||(dir <= -247.56&&dir >= -292.55))
			id = 2;
		else if((dir >= 112.56&&dir <= 157.55)
				||(dir <= -202.56&&dir >= -247.55))
			id = 3;
		else if((dir >= 157.56&&dir <= 202.55)
				||(dir <= -157.56&&dir >= -202.55))
			id = 4;
		else if((dir >= 202.56&&dir <= 247.55)
				||(dir <= -112.56&&dir >= -157.55))
			id = 5;
		else if((dir >= 247.56&&dir <= 292.55)
				||(dir <= -67.56&&dir >= -112.55))
			id = 6;
		else if((dir >= 292.56&&dir <= 337.55)
				||(dir <= -22.56&&dir >= -67.55))
			id = 7;
		return id;
	}

	public static int PlayerDirectionID_Four(Float dir)
	{
		int Direction = 5;
		if(((dir >= 0.0&&dir <= 44.5)||(dir >= 314.6&&dir <= 360.0))
				||((dir <= -0.0&&dir >= -44.5)||(dir <= -314.6&&dir >= -360.0)))//0
			Direction = 0;
		else if ((dir >= 44.6&&dir <= 134.5)
				||(dir <= -224.6&&dir >= -314.5))//1
			Direction = 1;
		else if ((dir >= 134.6&&dir <= 224.5)
				||(dir <= -134.6&&dir >= -224.5))//2
			Direction = 2;
		else if ((dir >= 224.6&&dir <= 314.5)
				||(dir <= -44.6&&dir >= -134.5))//3
			Direction = 3;
		return Direction;
	}
}
