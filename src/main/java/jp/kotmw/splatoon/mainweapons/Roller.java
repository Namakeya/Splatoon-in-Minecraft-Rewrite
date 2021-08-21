package jp.kotmw.splatoon.mainweapons;

import jp.kotmw.splatoon.maingame.threads.SquidRunnable;
import jp.kotmw.splatoon.mainweapons.threads.RollerBulletRunnable;
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

public class Roller implements Listener {

	public static final String bulletname="bullet_roller";

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		if(DataStore.getPlayerData(e.getPlayer().getName()).getArena() == null)
			return;
		Action action = e.getAction();
		if(action == Action.LEFT_CLICK_AIR
				|| action == Action.LEFT_CLICK_BLOCK
				|| action == Action.PHYSICAL)
			return;
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Roller)
			return;
		if(data.isAllCancel()
				|| item == null
				|| item.getType() != DataStore.getWeapondata(data.getWeapon()).getItemtype()
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(data.getWeapon()))
			return;
		WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
		if(player.getExp() < weapon.getInkSplashCost()) {
			MainGame.sendActionBar(data, ChatColor.RED+"インクがありません!");
			return;
		}
		if(data.getTask() == null) {
			BukkitRunnable task = new RollerRunnable(player.getName());
			task.runTaskTimer(Main.main, 0, 1);
			data.setTask(task);
		}
		data.setTick(5);
		player.setExp((float) (player.getExp()-weapon.getInkSplashCost()));
		if(!data.isPaint()) {
			data.setPaint(true);
			//p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 2, 5));
			player.getWorld().playSound(player.getLocation(),weapon.getSoundId(), SoundCategory.PLAYERS,weapon.getSoundVolume(),weapon.getSoundPitch());
			RollerSplash(player, weapon);
			/*
			Color color = DataStore.getArenaData(data.getArena()).getSplatColor(data.getTeamid()).getColor();
			Location location = player.getLocation();
			float yaw = -player.getLocation().getYaw();
			Polar_coodinates pc, pc2 = new Polar_coodinates(player.getWorld(), 2, Math.toRadians(yaw), 0);
			for(double i = -weapon.getRadius()/2; i <= weapon.getRadius()/2; i+=0.5) {
				for(int j = 0; j <= 1; j++) {
					pc = new Polar_coodinates(player.getWorld(), i, Math.toRadians(yaw)+(Math.PI/2), 0);
					Location judgeloc = location.clone().add(0, j-0.5, 0).add(pc2.convertLocation()).add(pc.convertLocation());
					Paint.PaintWool(data, judgeloc.getBlock());
					MainGame.Damager(data, judgeloc, DataStore.getWeapondata(data.getWeapon()).getDamage());
					player.getWorld().spawnParticle(Particle.REDSTONE,judgeloc,1,
							0.5,0.5,0.5, new Particle.DustOptions(color,1.0f));
				}
			}*/
		}
	}
	
	/*@EventHandler
	public void onInteract2(PlayerInteractEvent e) {
		Action action = e.getAction();
		if(action == Action.LEFT_CLICK_AIR
				|| action == Action.LEFT_CLICK_BLOCK
				|| action == Action.PHYSICAL)
			return;
		Player player = e.getPlayer();
		Location location = player.getLocation();
		float yaw = -player.getLocation().getYaw();
		Polar_coodinates pc, pc2 = new Polar_coodinates(player.getWorld(), 2, Math.toRadians(yaw), 0);
		for(double i = -2.5; i <= 2.5; i+=0.5) {
			for(int j = 0; j <= 1; j++) {
				pc = new Polar_coodinates(player.getWorld(), i, Math.toRadians(yaw)+(Math.PI/2), 0);
				new ParticleAPI.Particle(EnumParticle.REDSTONE, 
						location.clone().add(0, j-0.5, 0).add(pc2.convertLocation()).add(pc.convertLocation()),
						0.1f, 
						0.1f, 
						0.1f, 
						1,
						0).sendParticle(player);
			}
		}
	}*/

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player player = e.getPlayer();
		if(!DataStore.hasPlayerData(player.getName()))
			return;
		if(DataStore.getPlayerData(e.getPlayer().getName()).getArena() == null)
			return;
		PlayerData data = DataStore.getPlayerData(player.getName());
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
		WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
		if(weapon.getType() != WeaponType.Roller)
			return;
		if(data.isAllCancel()
				|| item == null
				|| item.getType() != weapon.getItemtype()
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(data.getWeapon()))
			return;
		//if(!data.isPaint())return;
		if(player.getExp() < weapon.getCost()) {
			MainGame.sendActionBar(data, ChatColor.RED+"インクがありません!");
			return;
		}
		player.setExp((float) (player.getExp()-weapon.getCost()));
		Color color = DataStore.getArenaData(data.getArena()).getSplatColor(data.getTeamid()).getColor();
		Location location = player.getLocation();
		float yaw = -player.getLocation().getYaw();
		Polar_coodinates pc, pc2 = new Polar_coodinates(player.getWorld(), weapon.getRadius()/2, Math.toRadians(yaw), 0);
		for(double i = -weapon.getRadius()/2; i <= weapon.getRadius()/2; i+=0.5) {
			for(int j = 0; j <= 1; j++) {
				pc = new Polar_coodinates(player.getWorld(), i, Math.toRadians(yaw)+(Math.PI/2), 0);
				Location judgeloc = location.clone().add(0, j-0.5, 0).add(pc2.convertLocation()).add(pc.convertLocation());
				Paint.PaintWool(data, judgeloc.getBlock());
				MainGame.Damager(data, judgeloc, DataStore.getWeapondata(data.getWeapon()).getDamage());

				player.getWorld().spawnParticle(Particle.REDSTONE,judgeloc,1,
						0.5,0.5,0.5, new Particle.DustOptions(color,1.0f));

			}
		}
	}

	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if(!(e.getEntity() instanceof Snowball)
				|| !(e.getEntity().getShooter() instanceof Player)
				|| !(e.getEntity().getCustomName().equalsIgnoreCase(bulletname)))
			return;
		Player player = (Player) e.getEntity().getShooter();
		if(!DataStore.hasPlayerData(player.getName()))
			return;
		if(DataStore.getPlayerData(player.getName()).getArena() == null)
			return;
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Roller)
			return;
		WeaponData weapon=DataStore.getWeapondata(data.getWeapon());

		double radius=weapon.getInkSplashPaintRadius() * getDecayRate(e.getEntity(),weapon);

		radius=radius<0.8?0.8:radius;
		if(e.getHitBlock()!=null && SquidRunnable.isSlipBlock(e.getHitBlock().getLocation())){
			radius+=1.5;
		}
		Paint.SpherePaint(e.getEntity().getLocation(),radius, data);
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		//System.out.println("call");
		if(e.getDamager() instanceof Snowball
				&& e.getDamager().getCustomName()!=null
				&& (e.getDamager().getCustomName().equalsIgnoreCase(bulletname))
				&&(e.getEntity() instanceof Player || e.getEntity() instanceof Creeper)) {
			Snowball ball = (Snowball) e.getDamager();
			//System.out.println("call2");
			if(!(ball.getShooter() instanceof Player))
				return;
			//System.out.println("call3");
			Player shooter = (Player) ball.getShooter();
			if(e.getEntity() instanceof Player) {
				Player player = (Player) e.getEntity();
				if (!DataStore.hasPlayerData(shooter.getName())
						|| player.getName() == shooter.getName()
						|| DataStore.getPlayerData(player.getName()).getTeamid() == DataStore.getPlayerData(shooter.getName()).getTeamid())
					return;
			}
			//System.out.println("call4");
			WeaponData data = DataStore.getWeapondata(DataStore.getPlayerData(shooter.getName()).getWeapon());
			if(data.getType() != WeaponType.Roller)
				return;
			//System.out.println("call5");
			e.setDamage(data.getInkSplashDamage()*getDecayRate(ball,data));
			((LivingEntity) e.getEntity()).setMaximumNoDamageTicks(1);


		}
	}

	public static void RollerSplash(Player player, WeaponData weapon) {
		for(int i = 0; i<weapon.getInkSplash(); i++) {
			Location loc=player.getLocation().clone();
			loc.setYaw(loc.getYaw()+(float)weapon.getInkSplashAngle()*((float)i/weapon.getInkSplash()-0.5f));
			loc.setPitch(-15);
			Vector direction = loc.getDirection().multiply(weapon.getSpeed());
			Snowball ball = player.launchProjectile(Snowball.class);

			ball.setVelocity(direction);
			ball.setShooter(player);
			ball.setCustomName(bulletname);
			Location loc2=player.getLocation().clone();
			loc2.setPitch(0);
			loc2.add(0,2,0);
			ball.teleport(loc2, PlayerTeleportEvent.TeleportCause.PLUGIN);
			BukkitRunnable task = new RollerBulletRunnable(player.getName(),ball);
			task.runTaskTimer(Main.main, 0, 1);
			//System.out.println(loc2);

		}
	}


	public static double getDecayRate(Projectile ball, WeaponData weapon){
		if(weapon.getFlyDecayTick()==0)return 1;
		double decay=1-((ball.getTicksLived()-weapon.getFlyDecayTick())*weapon.getFlyDecayRatio()/100);
		return decay<0?0:decay;
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
