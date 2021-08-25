package jp.kotmw.splatoon.mainweapons;

import jp.kotmw.splatoon.gamedatas.*;
import jp.kotmw.splatoon.gamedatas.DataStore.WeaponType;
import jp.kotmw.splatoon.maingame.MainGame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nullable;
import java.util.Random;
/** One class = One instance = Few types of mainweapons (may also multiple users)*/
public abstract class MainWeapon implements Listener {

	public String bulletname;
	public WeaponType weaponType;

	public MainWeapon(String bulletname,WeaponType weaponType){
		this.bulletname=bulletname;
		this.weaponType=weaponType;
	}
	public static WeaponData getWeaponData(PlayerData data){
		return DataStore.getWeapondata(data.getWeapon());
	}
/*
	public MainWeapon setBulletName(String bulletName){
		this.bulletname=bulletName;
		return this;
	}

	public MainWeapon setWeaponType(WeaponType weaponType){
		this.type=weaponType;
		return this;
	}
*/
	@Nullable
	public ArenaData getArena(Player pe){
		if(DataStore.hasPlayerData(pe.getName())){
			PlayerData pd=DataStore.getPlayerData(pe.getPlayer().getName());
			if(pd.getArena()!=null){
				return DataStore.getArenaData(pd.getArena());
			}
		}
		return null;
	}

	public boolean isMyWeaponType(PlayerData data){
		return DataStore.getWeapondata(data.getWeapon()).getType() == this.weaponType;
	}

	public boolean canShoot(PlayerData data){

		Player player = Bukkit.getPlayer(data.getName());
		//System.out.println(player.getInventory().getItemInMainHand());
		return !data.isUsingSpecial() && !data.isSquidMode() && isMyWeapon(data,player.getInventory().getItemInMainHand());
	}

	public boolean isMyWeapon(PlayerData data,ItemStack item){
		if(isMyWeaponType(data)
				&& !data.isAllCancel()
				&& item != null
				&& isMyWeaponType(data)
				&& item.hasItemMeta()
				&& item.getItemMeta().getDisplayName().equalsIgnoreCase(data.getWeapon())){
			//System.out.println("is my weapon");
			return true;
		}
		//System.out.println("not my weapon");
		return false;
	}

	public boolean isMyBullet(Entity entity){

		if(entity instanceof Projectile) {
			Projectile projectile=(Projectile) entity;
			if(bulletname==null){
				bulletname="bullet_"+this.getClass().getName();
				//System.out.println("buletname: "+bulletname);
			}
			if (projectile.getShooter() instanceof Player
					&& projectile.getCustomName() !=null
					&&projectile.getCustomName().equalsIgnoreCase(bulletname)) {
				return true;
			}
		}
		return false;
	}

	public boolean checkOnInteract(PlayerInteractEvent e){
		if(getArena(e.getPlayer())!=null) {
			Action action = e.getAction();
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				//System.out.println("interact");
				Player player = e.getPlayer();
				PlayerData data = DataStore.getPlayerData(player.getName());
				//System.out.println("suitable action");
				if (canShoot(data)) {
					return true;
				}
			}
		}
		return false;
	}

	public abstract void doOnInteract(PlayerInteractEvent e,PlayerData pd,Player pe);

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		//System.out.println("interact");
		if(checkOnInteract(e)){
			Player player = e.getPlayer();
			PlayerData data = DataStore.getPlayerData(player.getName());
			doOnInteract(e,data,player);
		}
	}
	public boolean checkOnHit(ProjectileHitEvent e){
		if(isMyBullet(e.getEntity())) {
			Player player = (Player) e.getEntity().getShooter();
			if (getArena(player) != null) {
				PlayerData data = DataStore.getPlayerData(player.getName());
				if (this.isMyWeaponType(data)){
					return true;
				}
			}

		}
		return false;
	}

	public abstract void doOnHit(ProjectileHitEvent e,PlayerData pd,Player pe);
	@EventHandler
	public void onHit(ProjectileHitEvent e) {

		if(checkOnHit(e)){
			Player player = (Player) e.getEntity().getShooter();
			PlayerData data = DataStore.getPlayerData(player.getName());
			doOnHit(e,data,player);
		}
	}

	public boolean checkOnDamage(EntityDamageByEntityEvent e){
		if(isMyBullet(e.getDamager())) {
			Projectile ball = (Projectile) e.getDamager();
			if (ball.getShooter() instanceof Entity &&
					MainGame.canDamage((Entity) ball.getShooter(), e.getEntity())) {
				Player shooter = (Player) ball.getShooter();
				PlayerData pd = DataStore.getPlayerData(shooter.getName());
				if (isMyWeaponType(pd)) {
					return true;
				}
			}
		}
		return false;
	}
	public abstract void doOnDamage(EntityDamageByEntityEvent e,PlayerData pd,Player pe);

	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		//System.out.println("call");
		if(checkOnDamage(e)){
			Projectile ball = (Projectile) e.getDamager();
			Player shooter = (Player) ball.getShooter();
			PlayerData pd = DataStore.getPlayerData(shooter.getName());
			doOnDamage(e,pd,shooter);

		}
	}

	/**距離減衰*/
	public double getDecayRate(Projectile ball,WeaponData weapon){
		if(weapon.getFlyDecayTick()==0)return 1;
		double decay=1-((ball.getTicksLived()-weapon.getFlyDecayTick())*weapon.getFlyDecayRatio()/100);
		return decay<0?0:decay>=1?1:decay;
	}

	public Vector calculateDirection(Location direction, double angle){
		Random random = new Random();
		//double anglediff=random.nextGaussian();
		//if(anglediff<-1 || anglediff>1)anglediff=0;
		double anglediff= random.nextDouble()-0.5;
		anglediff*=angle;
		//System.out.println(anglediff);

		Location loc=direction.clone();
		loc.setYaw((float) (loc.getYaw()+anglediff));
		//System.out.println(loc.getDirection());
		return loc.getDirection();
	}
	public void shoot(PlayerData data) {}
	/*
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
	*/

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
