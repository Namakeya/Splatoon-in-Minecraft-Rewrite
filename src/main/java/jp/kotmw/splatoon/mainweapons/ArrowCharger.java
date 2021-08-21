package jp.kotmw.splatoon.mainweapons;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.threads.SquidRunnable;
import jp.kotmw.splatoon.mainweapons.threads.ArrowChargerRunnable;
import jp.kotmw.splatoon.mainweapons.threads.ChargerBulletRunnable;
import jp.kotmw.splatoon.manager.Paint;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArrowCharger implements Listener {

	public static final String bulletname="bullet_charger";
	public static int fullcharge = 4;
	public static final String chargeMetaKey="charger_charge";

	public static final PotionEffect glowArrow=new PotionEffect(PotionEffectType.GLOWING,100,1);

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
		Player p = e.getPlayer();

		ItemStack item = p.getInventory().getItemInMainHand();
		PlayerData data = DataStore.getPlayerData(p.getName());
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Charger)
			return;
		if(data.isAllCancel()
				|| item == null
				|| item.getType() == Material.AIR
				|| !item.hasItemMeta()
				|| item.getItemMeta().getLore().size() < 5
				|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(data.getWeapon()))
			return;
		WeaponData weapon = DataStore.getWeapondata(data.getWeapon());

		//player.setExp((float) (player.getExp()-weapon.getCost()));
		if(data.getTask() == null|| data.getTask().isCancelled()) {
			BukkitRunnable task = new ArrowChargerRunnable(p.getName(),weapon.getFullcharge()/(fullcharge-1),item);
			task.runTaskTimer(Main.main, 0, 1);
			data.setTask(task);
		}
		data.setTick(1);
	}

	@EventHandler
	public void onLaunch(EntityShootBowEvent e) {
		if(e.getEntity() instanceof Player){
			Player p= (Player) e.getEntity();
			if(!DataStore.hasPlayerData(p.getName()))
				return;
			if(DataStore.getPlayerData(p.getName()).getArena() == null)
				return;
			PlayerData data = DataStore.getPlayerData(p.getName());
			if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Charger
			||!DataStore.getWeapondata(data.getWeapon()).isBowItem())
				return;
			ItemStack item = e.getBow();
			if(data.isAllCancel()
					|| item == null
					|| item.getType() == Material.AIR
					|| !item.hasItemMeta()
					|| !item.getItemMeta().getDisplayName().equalsIgnoreCase(data.getWeapon()))
				return;
			//e.getProjectile().remove();
			e.setCancelled(true);
			/*
			WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
			e.getProjectile().setGravity(false);
			e.getProjectile().setCustomName(bulletname);
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
			Vector direction = e.getProjectile().getVelocity().normalize();
			Vector vec = new Vector(x,0,z), vec2 = new Vector(direction.getX()*weapon.getSpeed(), direction.getY()*weapon.getSpeed(), direction.getZ()*weapon.getSpeed());
			vec2.add(vec);
			e.getProjectile().setVelocity(vec2);
			BukkitRunnable task = new ChargerBulletRunnable(p.getName(), (Projectile) e.getProjectile());
			task.runTaskTimer(Main.main, 0, 1);

			p.getWorld().playSound(p.getLocation(),weapon.getSoundId(), SoundCategory.PLAYERS,weapon.getSoundVolume(),weapon.getSoundPitch());

			 */
		}
	}

public static void shoot(PlayerData data) {
	Player player = Bukkit.getPlayer(data.getName());
	WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
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
		Arrow arrow = player.launchProjectile(Arrow.class);
		arrow.setCustomName(bulletname);
		Vector vec = new Vector(x,0,z), vec2 =
				new Vector(direction.getX()*weapon.getSpeed()*data.getCharge()/fullcharge,
						direction.getY()*weapon.getSpeed()*data.getCharge()/fullcharge,
						direction.getZ()*weapon.getSpeed()*data.getCharge()/fullcharge);
		vec2.add(vec);
		arrow.setVelocity(vec2);
		arrow.setGravity(false);
		arrow.setMetadata(chargeMetaKey, new FixedMetadataValue(Main.main,data.getCharge()));
		arrow.addCustomEffect(glowArrow,true);
		BukkitRunnable task = new ChargerBulletRunnable(player.getName(),arrow,data.getCharge());
		task.runTaskTimer(Main.main, 0, 1);
		data.setCharge(0);
	});
	//MessageUtil.playSoundAt(player,weapon.getSoundId(),weapon.getSoundVolume(),0.05f,weapon.getSoundPitch());
	player.getWorld().playSound(player.getLocation(),weapon.getSoundId(), SoundCategory.PLAYERS,weapon.getSoundVolume(),weapon.getSoundPitch());
	//player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,0.5f,1.2f);
}


	@EventHandler
	public void onHit(ProjectileHitEvent e) {
		if(!(e.getEntity().getShooter() instanceof Player)
				|| e.getEntity().getCustomName()==null
				|| !(e.getEntity().getCustomName().equalsIgnoreCase(bulletname)))
			return;
		Player player = (Player) e.getEntity().getShooter();
		if(!DataStore.hasPlayerData(player.getName()))
			return;
		if(DataStore.getPlayerData(player.getName()).getArena() == null)
			return;
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(DataStore.getWeapondata(data.getWeapon()).getType() != WeaponType.Charger)
			return;
		WeaponData weapon=DataStore.getWeapondata(data.getWeapon());
		// ループで全部チェックする
		int charge=1;
		for (MetadataValue v : e.getEntity().getMetadata(chargeMetaKey)) {
			// 名前を比較して同じプラグインか確認
			if (v.getOwningPlugin().getName().equals(Main.main.getName())) {
				// 同じなら値をセットしてループ抜ける
				charge = v.asInt();
				break;
			}
		}
		//System.out.println("charge in onHit: "+charge);
		double radius=weapon.getRadius() * getDecayRate(e.getEntity(),weapon)*charge/fullcharge;
		radius=radius<0.8?0.8:radius;
		if(e.getHitBlock()!=null && SquidRunnable.isSlipBlock(e.getHitBlock().getLocation())){
			radius*=1.5;
		}
		Paint.SpherePaint(e.getEntity().getLocation(),radius, data);
		e.getEntity().remove();
	}

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		//System.out.println("call");
		if(e.getDamager().getCustomName()!=null &&
				(e.getDamager().getCustomName().equalsIgnoreCase(bulletname))
				&&(e.getEntity() instanceof Player || e.getEntity() instanceof Creeper)) {
			Projectile ball = (Projectile) e.getDamager();
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
			if(data.getType() != WeaponType.Charger)
				return;
			//System.out.println("call5");
			int charge=1;
			for (MetadataValue v : ball.getMetadata(chargeMetaKey)) {
				// 名前を比較して同じプラグインか確認
				if (v.getOwningPlugin().getName().equals(Main.main.getName())) {
					// 同じなら値をセットしてループ抜ける
					charge = v.asInt();
					break;
				}
			}

			double damage=data.getDamage()*getDecayRate(ball,data)*(charge-1)/(fullcharge-1);
			//System.out.println("charge in onDamage: "+charge+" damage: "+damage);
			e.setDamage(damage);
			((LivingEntity) e.getEntity()).setMaximumNoDamageTicks(1);
			shooter.playSound(shooter.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS,1,1);


		}
	}

	public static double getDecayRate(Projectile ball,WeaponData weapon){
		if(weapon.getFlyDecayTick()==0)return 1;
		double decay=1-((ball.getTicksLived()-weapon.getFlyDecayTick())*weapon.getFlyDecayRatio()/100);
		return decay<0?0:decay;
	}

}
