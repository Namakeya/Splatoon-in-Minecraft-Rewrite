package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.util.MaterialUtil;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;

public class ChargerRunnable extends BukkitRunnable {

	private String name;
	private int full = 4;
	private int period;
	private String[] blockmeter = {" ▝", " ▌", "▟","█"};
	private PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 20, 1, false, false);
	private PotionEffect glow = new PotionEffect(PotionEffectType.GLOWING, 20, 1, false, false);

	public ChargerRunnable(String name,int period) {
		this.name = name;
		this.period=period;
	}

	@Override
	public void run() {
		if(!DataStore.hasPlayerData(name)) {
			this.cancel();
			return;
		}
		PlayerData data = DataStore.getPlayerData(name);
		int tick = data.getTick();
		int charge = data.getCharge();
		if(tick > 0) {
			tick--;
			data.setTick(tick);
			if(charge < full) {
				charge++;
				data.setCharge(charge);
			}
			sendCharge(data, charge);
			Bukkit.getPlayer(data.getName()).addPotionEffect(slow);
			Bukkit.getPlayer(data.getName()).addPotionEffect(glow);
		} else if (tick == 0){
			data.setCharge(0);
			data.setTick(-1);
			MainGame.sendTitle(data, 0, 1, 0, " ", " ");
			launch(data, charge);
			Bukkit.getPlayer(data.getName()).removePotionEffect(PotionEffectType.SLOW);
		}
	}
	
	private void launch(PlayerData data, int charge) {
		WeaponData weapon = DataStore.getWeapondata(data.getWeapon());
		SplatColor color = DataStore.getArenaData(data.getArena()).getSplatColor(data.getTeamid());
		Paint.SpherePaint(Bukkit.getPlayer(data.getName()).getLocation(), 1.2, data);
		//int full = weapon.getFullcharge();
		int shootlength = (int) weapon.getRange()*charge/4;
		ArenaData arena = DataStore.getArenaData(data.getArena());
		BlockIterator seeblock = new BlockIterator(Bukkit.getPlayer(data.getName()), shootlength);
		Player player=Bukkit.getPlayer(data.getName());
		player.getWorld().playSound(player.getLocation(),weapon.getSoundId(), SoundCategory.PLAYERS,weapon.getSoundVolume(),weapon.getSoundPitch());
		while(seeblock.hasNext()) {
			Block block = seeblock.next();
			Location loc = block.getLocation().clone();
			loc.getWorld().spawnParticle(Particle.BLOCK_DUST,loc,4, MaterialUtil.fromColorIdToWool(color.getColorID()).createBlockData());

			while(loc.getBlock().getType() == Material.AIR) {
				if(loc.getBlockY() <=arena.getStagePosition2().getY())
					break;
				loc.add(0,-1,0);
			}
			Paint.SpherePaint(loc, 1.5, data);
			MainGame.Damager(data, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), weapon.getDamage()*charge/4);
		}
	}

	private void sendCharge(PlayerData data, int charge) {
		ChatColor inkcolor = DataStore.getArenaData(data.getArena()).getSplatColor(data.getTeamid()).getChatColor();
		MainGame.sendTitle(data, 0, 10, 0, " ", inkcolor+blockmeter[charge-1]);
	}
}
