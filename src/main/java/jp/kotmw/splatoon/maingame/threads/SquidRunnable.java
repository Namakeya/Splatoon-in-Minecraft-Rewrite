package jp.kotmw.splatoon.maingame.threads;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.GameItems;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.SquidMode;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.manager.SplatColorManager;

public class SquidRunnable extends BukkitRunnable {

	private String name;

	public SquidRunnable(String name) {
		this.name = name;
	}

	@Override
	public void run() {

		if(!DataStore.hasPlayerData(name)) {
			this.cancel();
			return;
		}
		PlayerData data = DataStore.getPlayerData(name);

		Player player = Bukkit.getPlayer(name);
		//System.out.println("sprinting : "+player.isSprinting());

		int maxArmorHP=0;
		for(int i=0;i<data.getArmors().size();i++){
			PlayerData.Armor a=data.getArmors().get(i);
			if(a.getHp()>maxArmorHP)maxArmorHP=a.getHp();
			a.setDuration(a.getDuration()-1);
			if(a.getDuration()<=0){
				player.getWorld().playSound(player.getLocation(),Sound.BLOCK_GRASS_BREAK,SoundCategory.PLAYERS,0.7f,1.4f);
				data.getArmors().remove(i);
				i--;
			}else if(a.getHp()<=0){
				player.getWorld().playSound(player.getLocation(),Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR,SoundCategory.PLAYERS,1f,1f);
				data.getArmors().remove(i);
				i--;
			}
		}
		if(maxArmorHP>0){
			int particles=10+maxArmorHP;
			if(particles>30)particles=30;
			for (Player p:player.getWorld().getPlayers()
				 ) {
				if(p != player) {
					p.spawnParticle(Particle.REDSTONE, player.getLocation().add(0, 1, 0), particles,
							1, 1, 1, new Particle.DustOptions(Color.WHITE, 1.0f));
				}else{
					player.spawnParticle(Particle.REDSTONE,player.getLocation().add(0,1,0),particles/5,
							1,1,1, new Particle.DustOptions(Color.WHITE,1.0f));
				}
			}
		}


		if(data.getRecoilTick() > 0) {
			int cooltime = data.getRecoilTick();
			cooltime--;
			data.setRecoilTick(cooltime);
			return;
		}

		if(!data.isSquidMode())
			return;

		if(data.getInkCoolTime() > 0) {
			int cooltime = data.getInkCoolTime();
			cooltime--;
			data.setInkCoolTime(cooltime);

		}else {
			float ink = player.getExp();
			if ((SplatColorManager.isBelowBlockTeamColor(Bukkit.getPlayer(name), true)) || data.isClimb()) {

				if (ink <= 0.983) {
					player.setExp(ink + 0.016f);
				}
			} else {
				if (ink <= 0.997) {
					player.setExp(ink + 0.002f);
				}
			}
		}
		//player.setFoodLevel(2);
		//player.setSprinting(false);

		/*
		if(;){
			System.out.println("too fast");
			player.setVelocity(player.getVelocity().normalize().multiply(0.2));
		}*/

		if(canSlipBlock_under(player,player.getLocation())) {
			//System.out.println(player.getLocation()+" is slipblock");
			player.teleport(player.getLocation().add(0, -0.1, 0));
		}else if(data.isClimb() && !player.isSneaking() && canSlipBlock_above(player,player.getLocation())) {
			//System.out.println(player.getLocation()+" is slipblock");
			player.teleport(player.getLocation().add(0, +0.3, 0));
		}
		if(canSlipBlock_xn(player,player.getLocation())){
			player.teleport(player.getLocation().add(-0.1, 0, 0));
		}else if(canSlipBlock_xp(player,player.getLocation())){
			player.teleport(player.getLocation().add(0.1, 0, 0));
		}
		if(canSlipBlock_zn(player,player.getLocation())){
			player.teleport(player.getLocation().add(0, 0, -0.1));
		}else if(canSlipBlock_zp(player,player.getLocation())){
			player.teleport(player.getLocation().add(0, 0, 0.1));
		}


	}

	private boolean canSlipBlock_under(Entity player, Location location) {
		Location loc = location.clone().add(0, -0.5, 0);
		double maxy=loc.getBlock().getBoundingBox().getMaxY();
		if(maxy-0.001 <= location.getY() && location.getY() < maxy+0.001){
			//System.out.println("Suitable under pos");
			return isSlipBlock(loc);
		}
		return false;
		//System.out.println(loc);

	}

	private boolean canSlipBlock_above(Entity player,Location location) {

		return isSlipBlock(location)
				|| isSlipBlock(location.clone().add(0, player.getHeight()/2, 0))
				|| isSlipBlock(location.clone().add(0, player.getHeight()+0.02, 0));
		//System.out.println(loc);
	}

	private boolean canSlipBlock_xp(Entity player,Location location) {
		Location loc = location.clone().add(0.5, 0, 0);
		double minx=loc.getBlock().getBoundingBox().getMinX();
		double width=player.getWidth();
		//System.out.println("maxx : "+maxx+" playerx : "+location.getX()+width/2);
		if(minx-0.001 <= location.getX()+width/2 && location.getX()+width/2 < minx+0.001){
			//System.out.println("Suitable under pos");
			return isSlipBlock(loc);
		}
		return false;

	}

	private boolean canSlipBlock_zp(Entity player,Location location) {
		Location loc = location.clone().add(0, 0, 0.5);
		double minz=loc.getBlock().getBoundingBox().getMinZ();
		double width=player.getWidth();
		if(minz-0.001 <= location.getZ()+width/2 && location.getZ()+width/2 < minz+0.001){
			//System.out.println("Suitable under pos");
			return isSlipBlock(loc);
		}
		return false;
		//System.out.println(loc);

	}

	private boolean canSlipBlock_xn(Entity player,Location location) {
		Location loc = location.clone().add(-0.5, 0, 0);
		double maxx=loc.getBlock().getBoundingBox().getMaxX();
		double width=player.getWidth();
		//System.out.println("maxx : "+maxx+" playerx : "+location.getX()+width/2);
		if(maxx-0.001 <= location.getX()-width/2 && location.getX()-width/2 < maxx+0.001){
			//System.out.println("Suitable under pos");
			return isSlipBlock(loc);
		}
		return false;

	}

	private boolean canSlipBlock_zn(Entity player,Location location) {
		Location loc = location.clone().add(0, 0, -0.5);
		double maxz=loc.getBlock().getBoundingBox().getMaxZ();
		double width=player.getWidth();
		if(maxz-0.001 <= location.getZ()-width/2 && location.getZ()-width/2 < maxz+0.001){
			//System.out.println("Suitable under pos");
			return isSlipBlock(loc);
		}
		return false;
		//System.out.println(loc);

	}

	public static boolean isSlipBlock(Location location) {
		//System.out.println(location.getBlock().getType().toString());
		return DataStore.getConfig().getCanSlipBlocks().contains(location.getBlock().getType().toString());
	}


}
