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
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class QuickBomb extends SubWeapon {

	public QuickBomb() {
		super(BombType.QuickBomb, "bullet_quickbomb");
	}

	@Override
	public boolean doOnUse(PlayerData player, Player p) {
		SubWeaponData subweapon = DataStore.getSubWeaponData(subWeaponType.name());
		if(!player.isUsingSpecial() && p.getExp() < subweapon.getCost()) {
			MainGame.sendActionBar(player, ChatColor.RED+"インクがありません!");
			return false;
		}
		launch(player,p, subweapon);
		return true;
	}




	private void launch(PlayerData pd,Player player, SubWeaponData data) {
		if(!pd.isUsingSpecial()) {
			float ink = player.getExp();
			player.setExp((float) (ink - data.getCost()));
		}
		ThrownExpBottle expBottle = player.launchProjectile(ThrownExpBottle.class, player.getLocation().getDirection());
		expBottle.setCustomName(bulletname);
		player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.PLAYERS,1,1);
	}
	@Override
	public void doOnExex(ExpBottleEvent e, PlayerData data, Player pe) {
		SubWeaponData subweapon = DataStore.getSubWeaponData(subWeaponType.name());

		Paint.SpherePaint(e.getEntity().getLocation(), subweapon.getExplRadius(), data,true);
		MainGame.SphereDamager(data, e.getEntity().getLocation(), subweapon, subweapon.getExplRadius(), true);
	}


}
