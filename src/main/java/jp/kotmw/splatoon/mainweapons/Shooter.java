package jp.kotmw.splatoon.mainweapons;

import java.util.Random;

import com.sk89q.worldedit.entity.metadata.EntityProperties;
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

public class Shooter extends MainWeapon {


	@Override
	public void doOnInteract(PlayerInteractEvent e, PlayerData pd, Player pe) {
		WeaponData weapondata = DataStore.getWeapondata(pd.getWeapon());
		if(pe.getExp() < weapondata.getCost()) {
			MainGame.sendActionBar(pd, ChatColor.RED+"インクがありません!");
			return;
		}
		int tick = 1;
		if(weapondata.getFirespeed() < 5)
			tick=tick+(5-weapondata.getFirespeed());
		if(pd.getTask() == null || pd.getTask().isCancelled()) {
			BukkitRunnable task = new ShooterRunnable(pe.getName(),this);
			task.runTaskTimer(Main.main, 0, weapondata.getFirespeed());
			pd.setTask(task);
		}
		pd.setTick(tick);
	}

	@Override
	public void doOnHit(ProjectileHitEvent e, PlayerData pd, Player pe) {
		WeaponData weapon=DataStore.getWeapondata(pd.getWeapon());
		double radius=weapon.getRadius() * getDecayRate(e.getEntity(),weapon);
		//System.out.println("radius: "+radius);
		radius=radius<0.8?0.8:radius;
		if(e.getHitBlock()!=null && SquidRunnable.isSlipBlock(e.getHitBlock().getLocation())){
			radius+=1.5;
		}
		Paint.SpherePaint(e.getEntity().getLocation(),radius, pd);
		e.getEntity().remove();
	}

	@Override
	public void doOnDamage(EntityDamageByEntityEvent e, PlayerData pd, Player pe) {
		Projectile ball= (Projectile) e.getDamager();
		WeaponData data = DataStore.getWeapondata(pd.getWeapon());

		double damage=data.getDamage()*getDecayRate(ball,data);
		e.setDamage(damage);
		System.out.println(ball.toString()+" damage : "+damage);
		((LivingEntity) e.getEntity()).setMaximumNoDamageTicks(1);
		pe.playSound(e.getEntity().getLocation(), Sound.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS,1,1);
	}

@Override
	public void shoot(PlayerData data) {
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
			System.out.println(bulletname);
			Vector vec = new Vector(x,0,z), vec2 = new Vector(direction.getX()*weapon.getSpeed(), direction.getY()*weapon.getSpeed(), direction.getZ()*weapon.getSpeed());
			vec2.add(vec);
			snowball.setVelocity(vec2);
			BukkitRunnable task = new ShooterBulletRunnable(player.getName(),snowball,this);
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
