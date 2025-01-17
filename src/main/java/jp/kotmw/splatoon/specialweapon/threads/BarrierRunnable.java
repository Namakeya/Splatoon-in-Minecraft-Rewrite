package jp.kotmw.splatoon.specialweapon.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.util.DetailsColor.DetailsColorType;

import static jp.kotmw.splatoon.specialweapon.Barrier.barrierhp;

public class BarrierRunnable extends BukkitRunnable {
	
	Player player;
	PlayerData playerData;
	float pitch = 0;
	int duration;
	int timer=0;
	
	public BarrierRunnable(Player player,PlayerData playerData,int duration)
	{
		this.player = player;
		this.playerData=playerData;
		this.duration=duration;
	}

	@Override
	public void run() {
		for(PlayerData data : DataStore.getArenaPlayersList(playerData.getArena())) {
			if(data.getTeamid() == playerData.getTeamid() && data!=playerData
			&& Bukkit.getPlayer(data.getName()).getLocation().distanceSquared(player.getLocation())<9) {
				data.addArmor(barrierhp,duration-timer);
			}
		}
		if(this.duration<timer || playerData.isDead()){
			playerData.setSpecialProgress(0);
			this.cancel();
		}else{
			timer++;

		}
	}
	
	void showParticleCircles(Location center, double radius, float pitch) {
		for(double deg = 0.0; deg <= 360.0; deg+=5) {
			double x = radius*Math.cos(Math.toRadians(deg))*Math.sin(pitch);
			double y = radius*Math.cos(pitch)+1.5;
			double z = radius*Math.sin(Math.toRadians(deg))*Math.sin(pitch);
			center.add(x,y,z);
			player.getWorld().spawnParticle(Particle.REDSTONE,center,1,
					0.5,0.5,0.5, new Particle.DustOptions(Color.WHITE,1.0f));
			center.subtract(x, y, z);
		}
	}
}
