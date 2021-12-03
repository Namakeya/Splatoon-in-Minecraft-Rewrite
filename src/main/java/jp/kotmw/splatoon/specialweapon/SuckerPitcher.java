package jp.kotmw.splatoon.specialweapon;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.specialweapon.threads.PitcherRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class SuckerPitcher extends SpecialWeapon{

	public SuckerPitcher() {
		super(DataStore.SpecialType.SuckerBomb, "bullet_suckerpitcher");
	}

	@Override
	public boolean doOnInteract(PlayerInteractEvent e, PlayerData pd, Player pe) {
		pe.getWorld().playSound(pe.getLocation(), Sound.BLOCK_PISTON_EXTEND, SoundCategory.PLAYERS,1f,0.8f);
		MainGame.sendMessage(pd, ChatColor.YELLOW+"ボムを投げまくれ！");
		new PitcherRunnable(pe,pd,160).runTaskTimer(Main.main, 1, 1);
		return true;
	}
}
