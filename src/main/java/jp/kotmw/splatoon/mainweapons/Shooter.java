package jp.kotmw.splatoon.mainweapons;

import java.util.Random;

import jp.kotmw.splatoon.maingame.threads.SquidRunnable;
import jp.kotmw.splatoon.mainweapons.threads.ShooterBulletRunnable;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.threads.ShooterRunnable;
import jp.kotmw.splatoon.manager.Paint;

public class Shooter implements Listener {

	public static final String bulletname="bullet_shooter";

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
		//System.out.println("interact");
		Player player = e.getPlayer();
		ItemStack item = player.getInventory().getItemInMainHand();
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Shooter)
			return;
		if(data.isAllCancel()
				|| item == null
				|| item.getType() != DataStore.getWeapondata(data.getWeapon()).getItemtype()
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(data.getWeapon()))
			return;
		WeaponData weapondata = DataStore.getWeapondata(data.getWeapon());
		if(player.getExp() < weapondata.getCost()) {
			MainGame.sendActionBar(data, ChatColor.RED+"インクがありません!");
			return;
		}
		int tick = 1;
		if(weapondata.getFirespeed() < 5)
			tick=tick+(5-weapondata.getFirespeed());
		if(data.getTask() == null || data.getTask().isCancelled()) {
			BukkitRunnable task = new ShooterRunnable(player.getName());
			task.runTaskTimer(Main.main, 0, weapondata.getFirespeed());
			data.setTask(task);
		}
		data.setTick(tick);
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
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Shooter)
			return;
		WeaponData weapon=DataStore.getWeapondata(data.getWeapon());
		double radius=weapon.getRadius() * getDecayRate(e.getEntity(),weapon);
		//System.out.println("radius: "+radius);
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
				&&(e.getDamager().getCustomName().equalsIgnoreCase(bulletname))
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
			if(data.getType() != WeaponType.Shooter)
				return;
			//System.out.println("call5");

			e.setDamage(data.getDamage()*getDecayRate(ball,data));
			((LivingEntity) e.getEntity()).setMaximumNoDamageTicks(1);
			shooter.playSound(e.getEntity().getLocation(), Sound.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS,1,1);

		}
	}

	public static double getDecayRate(Projectile ball,WeaponData weapon){
		if(weapon.getFlyDecayTick()==0)return 1;
		double decay=1-((ball.getTicksLived()-weapon.getFlyDecayTick())*weapon.getFlyDecayRatio()/100);
		return decay<0?0:decay;
	}

	public static void shoot(PlayerData data) {
		Player player = Bukkit.getPlayer(data.getName());
		WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
		player.setExp((float) (player.getExp()-weapon.getCost()));
		Paint.SpherePaint(player.getLocation(), DataStore.getWeapondata(data.getWeapon()).getRadius(), data);
		double x,z;
		//精度
		if(weapon.getAngle()>0){
			Random random = new Random();
			int angle = weapon.getAngle()*100;
			x= Math.toRadians((random.nextInt(angle)/100)-((weapon.getAngle()-1)/2));
			z = Math.toRadians((random.nextInt(angle)/100)-((weapon.getAngle()-1)/2));
		}else{
			x=0;
			z=0;
		}

		Vector direction = player.getLocation().getDirection().clone();
		MainGame.sync(() -> {
			Snowball snowball = player.launchProjectile(Snowball.class);
			snowball.setCustomName(bulletname);
			Vector vec = new Vector(x,0,z), vec2 = new Vector(direction.getX()*weapon.getSpeed(), direction.getY()*weapon.getSpeed(), direction.getZ()*weapon.getSpeed());
			vec2.add(vec);
			snowball.setVelocity(vec2);
			BukkitRunnable task = new ShooterBulletRunnable(player.getName(),snowball);
			task.runTaskTimer(Main.main, 0, 1);
		});
		//MessageUtil.playSoundAt(player,weapon.getSoundId(),weapon.getSoundVolume(),0.05f,weapon.getSoundPitch());
		player.getWorld().playSound(player.getLocation(),weapon.getSoundId(), SoundCategory.PLAYERS,weapon.getSoundVolume(),weapon.getSoundPitch());
		//player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,0.5f,1.2f);
	}

	/*
	 * シューター仕様のまとめ
	 * ・tick指定で連射速度を指定可能
	 * ・着弾地点&発射地点の着色の半径も指定可能
	 *
	 * [発射の角度の乱数関係のまとめ]
	 *   テストとして12°の角度の範囲内での乱数とする
	 *   正面が半分になるようにするとなると、12を半分に割った6°:6°で左右に分ける(分けないと片方だけ飛んでいくって感じになる)
	 *   -6～6の範囲内を乱数で取る
	 *   それぞれの角度をそれぞれのベクトルに変換し、XとZのベクトルに乗算(加算？)する
	 *
	 */
}
