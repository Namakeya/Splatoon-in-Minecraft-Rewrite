package jp.kotmw.splatoon.subweapon;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BombType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.subweapon.threads.SplashBombRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
/** One class = One instance = One kind of subweapon (may multiple objects)*/
public abstract class SubWeapon implements Listener {

	public BombType subWeaponType;
	public String bulletname;

	public SubWeapon(BombType subWeaponType,String bulletname){
		this.subWeaponType=subWeaponType;
		this.bulletname=bulletname;
	}

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
		return DataStore.getWeapondata(data.getWeapon()).getSubWeapon().equalsIgnoreCase(subWeaponType.name());
	}

	public boolean canShoot(PlayerData data){

		Player player = Bukkit.getPlayer(data.getName());
		//System.out.println(player.getInventory().getItemInMainHand());
		return data.getSubCooldown()<=0 && !data.isSquidMode() && isMyWeapon(data,player.getInventory().getItemInMainHand());
	}

	public boolean isMyWeapon(PlayerData data,ItemStack item){
		if(isMyWeaponType(data)
				&& !data.isAllCancel()
				&& item != null
				&& isMyWeaponType(data)
				&& item.hasItemMeta()
				&& item.getItemMeta().getDisplayName().equalsIgnoreCase(this.subWeaponType.name())){
			//System.out.println("is my weapon");
			return true;
		}
		//System.out.println("not my weapon");
		return false;
	}

	public boolean isMyBullet(Entity entity){

		if(bulletname==null){
			bulletname="bullet_"+this.subWeaponType.name();
			//System.out.println("buletname: "+bulletname);
		}
		if (entity.getCustomName() !=null
				&&entity.getCustomName().equalsIgnoreCase(bulletname)) {
			return true;
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

	public abstract boolean doOnInteract(PlayerInteractEvent e,PlayerData pd,Player pe);

	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		//System.out.println("interact");
		if(checkOnInteract(e)){
			Player player = e.getPlayer();
			PlayerData data = DataStore.getPlayerData(player.getName());
			if(doOnInteract(e,data,player)){
				data.setSubCooldown(12);
				SubWeaponData sw=DataStore.getSubWeaponData(DataStore.getWeapondata(data.getWeapon()).getSubWeapon());
				data.setInkCoolTime(sw.getCooltime());
			}
		}
	}


	@EventHandler
	public void onProjectileLaunch(ProjectileLaunchEvent e) {
		if(e.getEntity().getShooter() instanceof Player) {
			if(!DataStore.hasPlayerData(((Player)e.getEntity().getShooter()).getName()))
				return;
			Player player = (Player) e.getEntity().getShooter();
			if(player.getInventory().getItemInMainHand().getType().equals(getLaunchItem(e.getEntity())))
				e.setCancelled(true);
		}
	}
	public boolean checkOnExex(ExpBottleEvent e){
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

	public abstract void doOnExex(ExpBottleEvent e,PlayerData pd,Player pe);
	@EventHandler
	public void onExplodeExpBottle(ExpBottleEvent e) {
		if(checkOnExex(e)){

			e.setExperience(0);
			Player player = (Player) e.getEntity().getShooter();
			PlayerData data = DataStore.getPlayerData(player.getName());
			doOnExex(e,data,player);
		}

	}

	private static Material getLaunchItem(Projectile projectile) {
		switch(projectile.getType()) {
		case ARROW:
			return Material.ARROW;
		case THROWN_EXP_BOTTLE:
			return Material.EXPERIENCE_BOTTLE;
		case ENDER_PEARL:
			return Material.ENDER_PEARL;
		case ENDER_SIGNAL:
			return Material.ENDER_EYE;
		case SPLASH_POTION:
			return Material.SPLASH_POTION;
		case FISHING_HOOK:
			return Material.FISHING_ROD;
		case SNOWBALL:
			return Material.SNOWBALL;
		case EGG:
			return Material.EGG;
		default:
			break;
		}
		return null;
	}
}
