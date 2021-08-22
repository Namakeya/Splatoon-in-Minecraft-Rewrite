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

public class ArrowCharger extends MainWeapon{

	public static int fullcharge = 4;
	public static final String chargeMetaKey="charger_charge";

	public ArrowCharger() {
		super("bullet_charger", WeaponType.Charger);
	}

	@Override
	public void doOnInteract(PlayerInteractEvent e, PlayerData pd, Player pe) {
		WeaponData weapon = DataStore.getWeapondata(pd.getWeapon());

		//player.setExp((float) (player.getExp()-weapon.getCost()));
		ItemStack item = pe.getInventory().getItemInMainHand();
		if(pd.getTask() == null|| pd.getTask().isCancelled()) {
			BukkitRunnable task = new ArrowChargerRunnable(pe.getName(),(weapon.getFullcharge()-6)/(fullcharge-1),item,this);
			task.runTaskTimer(Main.main, 6, 1);
			pd.setTask(task);
		}
		pd.setTick(1);
	}


	@Override
	public void doOnHit(ProjectileHitEvent e, PlayerData pd, Player pe) {
		WeaponData weapon=DataStore.getWeapondata(pd.getWeapon());
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
		Paint.SpherePaint(e.getEntity().getLocation(),radius, pd);
		e.getEntity().remove();
	}

	@EventHandler
	public void onLaunch(EntityShootBowEvent e) {
		if (!DataStore.hasPlayerData(e.getEntity().getName()))
			return;
		PlayerData pd=DataStore.getPlayerData(e.getEntity().getName());
		if(isMyWeapon(pd,e.getBow())) {

			e.setCancelled(true);

		}
	}

	@Override
public void shoot(PlayerData data) {
	Player player = Bukkit.getPlayer(data.getName());
	WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
	Paint.SpherePaint(player.getLocation(), DataStore.getWeapondata(data.getWeapon()).getRadius(), data);

	//精度

	Vector direction = calculateDirection(player.getLocation(), weapon.getAngle());
	MainGame.sync(() -> {
		Arrow arrow = player.launchProjectile(Arrow.class);
		arrow.setCustomName(bulletname);
		Vector vec2 =
				new Vector(direction.getX()*weapon.getSpeed()*data.getCharge()/fullcharge,
						direction.getY()*weapon.getSpeed()*data.getCharge()/fullcharge,
						direction.getZ()*weapon.getSpeed()*data.getCharge()/fullcharge);

		arrow.setVelocity(vec2);
		arrow.setGravity(!weapon.NoGravity());
		arrow.setMetadata(chargeMetaKey, new FixedMetadataValue(Main.main,data.getCharge()));
		//arrow.addCustomEffect(glowArrow,true);
		BukkitRunnable task = new ChargerBulletRunnable(player.getName(),arrow,data.getCharge());
		task.runTaskTimer(Main.main, 0, 1);
		data.setCharge(0);
	});
	//MessageUtil.playSoundAt(player,weapon.getSoundId(),weapon.getSoundVolume(),0.05f,weapon.getSoundPitch());
	player.getWorld().playSound(player.getLocation(),weapon.getSoundId(), SoundCategory.PLAYERS,weapon.getSoundVolume(),weapon.getSoundPitch());
	//player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_WOODEN_DOOR,0.5f,1.2f);
}


	@Override
	public void doOnDamage(EntityDamageByEntityEvent e, PlayerData pd, Player pe) {
		WeaponData data = DataStore.getWeapondata(DataStore.getPlayerData(pe.getName()).getWeapon());
		Projectile arrow= (Projectile) e.getDamager();
		//System.out.println("call5");
		int charge=1;
		for (MetadataValue v : arrow.getMetadata(chargeMetaKey)) {
			// 名前を比較して同じプラグインか確認
			if (v.getOwningPlugin().getName().equals(Main.main.getName())) {
				// 同じなら値をセットしてループ抜ける
				charge = v.asInt();
				break;
			}
		}

		double damage=data.getDamage()*getDecayRate(arrow,data)*(charge)/(fullcharge);
		//System.out.println("charge in onDamage: "+charge+" damage: "+damage);
		e.setDamage(damage);
		((LivingEntity) e.getEntity()).setMaximumNoDamageTicks(1);
		pe.playSound(pe.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, SoundCategory.PLAYERS,1,1);

	}




}
