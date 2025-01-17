package jp.kotmw.splatoon.maingame;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.threads.SquidRunnable;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.manager.SplatColorManager;

public class SquidMode implements Listener {

	//public static PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 3600*20, 1, false, false);
	public static PotionEffect slow = new PotionEffect(PotionEffectType.SLOW, 3600*20, 0, false, false);
	public static PotionEffect invisible = new PotionEffect(PotionEffectType.INVISIBILITY, 3600*20, 1, false, false);


	@EventHandler(priority = EventPriority.HIGH)
	public void changeSquid(PlayerDropItemEvent e) {
		if(DataStore.hasPlayerData(e.getPlayer().getName())) {
			e.setCancelled(true);
			PlayerData data=DataStore.getPlayerData(e.getPlayer().getName());
			data.setDropped(true);
			//System.out.println("drop");
			if (DataStore.getStatusData(e.getPlayer().getName()).isDropToSquid())
				MainGame.sync(() -> {
					changeSquid(e.getPlayer());
				});
		}
	}

	public static boolean changeSquid(Player pe){

		if(!DataStore.hasPlayerData(pe.getName()))
			return false;
		if(DataStore.getPlayerData(pe.getName()).getArena() == null)
			return false;

		PlayerData data = DataStore.getPlayerData(pe.getName());
		if(data.isDead() || data.isAllCancel())
			return false;
		if(data.getRecoilTick()>0 || data.getSuperjumpStatus()>0)return false ;
		pe.playSound(pe.getLocation(), Sound.ENTITY_PLAYER_SWIM, 1, 1);
		if(data.isSquidMode()) {
			SquidMode.toMan(pe,data);
		} else {
			SquidMode.toSquid(pe,data,true);
			if(SplatColorManager.isBelowBlockTeamColor(pe, true)) {
				//pe.addPotionEffect(SquidMode.speed);
			} else {
				SquidMode.spawnSquid(pe);
				pe.addPotionEffect(SquidMode.slow);
			}
		}
		return true;
	}



	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		if(!DataStore.getPlayerData(e.getPlayer().getName()).isSquidMode())
			return;
		//System.out.println("move");
		Player player = e.getPlayer();
		PlayerData data = DataStore.getPlayerData(player.getName());




		LivingEntity squid = data.getPlayerSquid();
		if(squid != null) squid.teleport(player.getLocation());
		if(SplatColorManager.isTargetBlockTeamColor(player)) {
			data.setClimb(true);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setFlySpeed(0.08f);
		} else if(!SplatColorManager.isTargetBlockTeamColor(player)) {
			data.setClimb(false);
			player.setAllowFlight(false);
			player.setFlying(false);
		}
		if(!SplatColorManager.isBelowBlockTeamColor(player, true) && !data.isClimb()) {
			spawnSquid(player);
			//player.removePotionEffect(PotionEffectType.SPEED);
			player.addPotionEffect(slow);
		} else if(SplatColorManager.isBelowBlockTeamColor(player, true) || data.isClimb()) {
			player.removePotionEffect(PotionEffectType.SLOW);
			//player.addPotionEffect(speed);
			if(squid != null) {
				squid.remove();
				data.setPlayerSquid(null);
			}
		}
		if (player.isFlying() && player.isSprinting()) {
			player.setFlying(false);
		}

		/*Block block = canSlipBlock_front(e);
		if(block == null)
			return;
		new ParticleAPI.Particle(EnumParticle.REDSTONE, 
				block.getLocation().clone().add(0.5, 0, 0.5),
				0.1f, 
				0.1f, 
				0.1f, 
				1,
				0).sendParticle(player);*/
	}
/*
	@EventHandler
	public void onItemHeld(PlayerItemHeldEvent e) {
		if(!DataStore.hasPlayerData(e.getPlayer().getName()))
			return;
		if(!DataStore.getPlayerData(e.getPlayer().getName()).isSquidMode())
			return;
		if(e.getNewSlot() < 3)
			e.setCancelled(true);
	}*/

	public static void spawnSquid(Player player) {
		PlayerData data = DataStore.getPlayerData(player.getName());
		if(data.getPlayerSquid() != null)
			return;
		LivingEntity squid = (LivingEntity)player.getWorld().spawnEntity(player.getLocation(), EntityType.SQUID);
		squid.setCustomName(player.getName());
		squid.setAI(false);
		data.setPlayerSquid(squid);

	}
	


	private Block canSlipBlock_front(PlayerMoveEvent e) {
		Location before = e.getFrom(), after = e.getTo();
		double x = before.getX() - after.getX(), 
				z = before.getZ() - after.getZ();
		if(Math.abs(x) >= Math.abs(z)) {
			for(int i = 0; i<= 1; i++) {
				Location loc = after.clone().add((x >= 0 ? i : -i), 0, 0);
				if(isSlipBlock(loc))
					return loc.getBlock();
			}
		} else if(Math.abs(z) >= Math.abs(x)) {
			for(int i = 0; i<= 1; i++) {
				Location loc = after.clone().add(0, 0, (z >= 0 ? i : -i));
				if(isSlipBlock(loc))
					return loc.getBlock();
			}
		}
		return null;
	}

	public boolean isSlipBlock(Location location) {
		//System.out.println(location.getBlock().getType().toString());
		return DataStore.getConfig().getCanSlipBlocks().contains(location.getBlock().getType().toString());
	}


	public static void toSquid(Player player,PlayerData data,boolean doFlight){
		player.addPotionEffect(invisible);
		//player.getInventory().setHeldItemSlot(3);

		data.setSquidMode(true);
		if(doFlight && SplatColorManager.isTargetBlockTeamColor(player)) {
			data.setClimb(true);
			player.setAllowFlight(true);
			player.setFlying(true);
			player.setFlySpeed(0.08f);
		}
		MainGame.setSquidInv(data);
	}

	public static void toMan(Player player,PlayerData data){
		LivingEntity squid = data.getPlayerSquid();
		if(squid != null)
			squid.remove();
		data.setPlayerSquid(null);
		data.setSquidMode(false);
		data.setClimb(false);
		player.setAllowFlight(false);
		player.setFlying(false);
		//player.getInventory().setHeldItemSlot(0);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.removePotionEffect(PotionEffectType.SPEED);
		player.removePotionEffect(PotionEffectType.SLOW);
		WeaponData weapon=DataStore.getWeapondata(data.getWeapon());
		ArenaData arena = DataStore.getArenaData(data.getArena());
		SplatColor color=arena.getSplatColor(data.getTeamid());
		MainGame.setInv(data);
	}
}
