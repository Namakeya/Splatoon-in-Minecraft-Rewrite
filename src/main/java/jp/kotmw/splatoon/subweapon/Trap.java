package jp.kotmw.splatoon.subweapon;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BombType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.subweapon.threads.SplashBombRunnable;
import jp.kotmw.splatoon.subweapon.threads.TrapRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Trap extends SubWeapon {

	public Trap() {
		super(BombType.Trap, "bullet_trap");
	}

	@Override
	public boolean doOnInteract(PlayerInteractEvent e, PlayerData player, Player p) {
		SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(player.getWeapon()).getSubWeapon());
		if(p.getExp() < subweapon.getCost()) {
			MainGame.sendActionBar(player, ChatColor.RED+"インクがありません!");
			return false;
		}
		PlayerData pd=DataStore.getPlayerData(player.getName());
		launch(p, subweapon);
		e.setCancelled(true);
		return true;
	}




	private void launch(Player player, SubWeaponData data) {
		float ink = player.getExp();
		PlayerData pd=DataStore.getPlayerData(player.getName());
		player.setExp((float) (ink-data.getCost()));
		ArmorStand stand = (ArmorStand) player.getLocation().getWorld().spawnEntity(player.getLocation().add(0, 1, 0), EntityType.ARMOR_STAND);
		stand.setInvulnerable(true);
		stand.setInvisible(true);
		stand.setMarker(true);
		stand.setCustomName(bulletname);
		new TrapRunnable(DataStore.getPlayerData(player.getName()), stand,pd.getSubCount()).runTaskTimer(Main.main, 0, 1);
		player.playSound(player.getLocation(), Sound.BLOCK_METAL_PLACE,1,1);

		Paint.PaintWool(pd,player.getLocation().getBlock());
		pd.setSubCount(pd.getSubCount()+1);

	}
	@Override
	public void doOnExex(ExpBottleEvent e, PlayerData data, Player pe) {
	}


}
