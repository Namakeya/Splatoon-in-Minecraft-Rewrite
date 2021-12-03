package jp.kotmw.splatoon.specialweapon.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static jp.kotmw.splatoon.specialweapon.Barrier.barrierhp;

public class PitcherRunnable extends BukkitRunnable {

	Player player;
	PlayerData playerData;
	int duration;
	int timer=0;

	public PitcherRunnable(Player player, PlayerData playerData, int duration)
	{
		this.player = player;
		this.playerData=playerData;
		this.duration=duration;
	}

	@Override
	public void run() {
		playerData.setSpecialProgress(100*(duration-timer)/duration);
		if(this.duration<timer || playerData.isDead()){
			this.cancel();
			playerData.setSpecialProgress(0);
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
