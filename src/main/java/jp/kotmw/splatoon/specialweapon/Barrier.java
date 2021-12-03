package jp.kotmw.splatoon.specialweapon;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.specialweapon.threads.MultiMissileRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.specialweapon.threads.BarrierRunnable;

import java.util.ArrayList;
import java.util.List;

public class Barrier extends SpecialWeapon{

	public static int barrierhp=10000;
	public Barrier() {
		super(DataStore.SpecialType.Barrier, "bullet_barrier");
	}

	@Override
	public boolean doOnInteract(PlayerInteractEvent e, PlayerData pd, Player pe) {

		pd.addArmor(barrierhp,100);
		pe.getWorld().playSound(pe.getLocation(), Sound.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS,1f,1.6f);
		pd.setSpecialProgress(1);
		new BarrierRunnable(pe,pd,100).runTaskTimer(Main.main, 1, 1);
		return true;
	}
}
