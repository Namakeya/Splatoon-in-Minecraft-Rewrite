package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.ArrowCharger;
import jp.kotmw.splatoon.mainweapons.MainWeapon;
import jp.kotmw.splatoon.mainweapons.Shooter;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.MaterialUtil;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;

import static jp.kotmw.splatoon.mainweapons.ArrowCharger.fullcharge;

public class ArrowChargerRunnable extends BukkitRunnable {

	private String name;
	private ItemStack weaponItem;
	private MainWeapon mainWeapon;

	private int period;
	private int chargeTimer=10000;
	//private String[] blockmeter = {" ▝", " ▌", "▟","█"};
	private String[] blockmeter = {"╵", "└", "├","┼"};
	private PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 20, 1, false, false);
	private PotionEffect glow = new PotionEffect(PotionEffectType.GLOWING, 10, 1, false, false);

	public ArrowChargerRunnable(String name, int period, ItemStack weaponItem,MainWeapon mainWeapon) {
		this.name = name;
		this.period=period;
		this.weaponItem=weaponItem;
		this.mainWeapon=mainWeapon;
	}

	@Override
	public void run() {
		if(!DataStore.hasPlayerData(name)) {
			this.cancel();
			return;
		}
		PlayerData data = DataStore.getPlayerData(name);
		WeaponData weapon=DataStore.getWeapondata(data.getWeapon());
		int tick = data.getTick();
		int charge = data.getCharge();
		Player player=Bukkit.getPlayer(data.getName());
		//System.out.println("charge: "+charge+" timer: "+chargeTimer);
		//System.out.println(player.getItemInUse().equals(weaponItem));
		if(player.getItemInUse()!=null && player.getItemInUse().equals(weaponItem)) {
			//System.out.println("charge");
			if(chargeTimer>=period) {
				if(player.getExp() < weapon.getCost()*charge/fullcharge) {
					MainGame.sendActionBar(data, ChatColor.RED+"インクがありません!");
					chargeTimer=0;
					return;
				}
				if (charge < fullcharge) {
					charge++;
					data.setCharge(charge);
					if(charge==fullcharge){
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, SoundCategory.PLAYERS, 1f, 2);

					}else{
						player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, SoundCategory.PLAYERS, 0.5f, 1 + (charge-1) * 0.33f);

					}
				}
				sendCharge(data, charge);
				//Bukkit.getPlayer(data.getName()).addPotionEffect(slow);

				chargeTimer=0;
			}else{
				chargeTimer++;
			}
			if(charge==fullcharge){
				player.addPotionEffect(glow);
			}

		}else {

			if(charge>0) {
				float ink = player.getExp() - weapon.getCost() * charge / fullcharge;
				if (ink < 0) {
					ink = 0;
				}

				player.setExp(ink);
				//System.out.println(charge);
				mainWeapon.shoot(data);

				sendCharge(data, 0);

			}
			this.cancel();
			return;
		}
	}

	@Override
	public void cancel(){
		super.cancel();
		//System.out.println("canceled");
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
		if(charge>=1){
			MainGame.sendTitle(data, 0, 10, 0, " ", inkcolor+blockmeter[charge-1]);
		}else{
			MainGame.sendTitle(data, 0, 1, 0, " ", "");
		}
	}
}
