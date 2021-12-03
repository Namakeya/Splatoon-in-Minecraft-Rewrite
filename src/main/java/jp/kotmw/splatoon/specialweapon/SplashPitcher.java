package jp.kotmw.splatoon.specialweapon;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.specialweapon.threads.BarrierRunnable;
import jp.kotmw.splatoon.specialweapon.threads.MultiMissileRunnable;
import jp.kotmw.splatoon.specialweapon.threads.PitcherRunnable;
import jp.kotmw.splatoon.subweapon.SplashBomb;
import jp.kotmw.splatoon.subweapon.threads.SplashBombRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SplashPitcher extends SpecialWeapon{

	public SplashPitcher() {
		super(DataStore.SpecialType.SplashBomb, "bullet_splashpitcher");
	}

	@Override
	public boolean doOnInteract(PlayerInteractEvent e, PlayerData pd, Player pe) {
		pe.getWorld().playSound(pe.getLocation(), Sound.BLOCK_PISTON_EXTEND, SoundCategory.PLAYERS,1f,0.8f);
		MainGame.sendMessage(pd, ChatColor.YELLOW+"ボムを投げまくれ！");
		new PitcherRunnable(pe,pd,160).runTaskTimer(Main.main, 1, 1);
		return true;
	}
}
