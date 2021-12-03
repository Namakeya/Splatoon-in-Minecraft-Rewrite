package jp.kotmw.splatoon.subweapon.threads;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.SubWeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.manager.SplatColorManager;
import jp.kotmw.splatoon.subweapon.SplashBomb;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class TrapRunnable extends BukkitRunnable {
	private PlayerData data;
	private ArmorStand bomb;
	private int time;
	private boolean activated;
	private int id;

	private static PotionEffect glow=new PotionEffect(PotionEffectType.GLOWING,150,1);

	public TrapRunnable(PlayerData data, ArmorStand tnt,int id) {
		this.data = data;
		this.bomb = tnt;
		this.id=id;
		time=15;
		activated=false;
	}

	@Override
	public void run() {
		SubWeaponData subweapon = DataStore.getSubWeaponData(DataStore.getWeapondata(data.getWeapon()).getSubWeapon());
		if(bomb.isDead()){
			this.cancel();
		}
		for (PlayerData pd : DataStore.getArenaPlayersList(data.getArena())) {
			if (data.getTeamid() == pd.getTeamid()) {
				Player pe = Bukkit.getPlayer(pd.getName());
				pe.spawnParticle(Particle.SMOKE_NORMAL,bomb.getLocation(),0,0,0.1,0);
			}
		}
		if(activated) {
			if(time<=0){

				Paint.SpherePaint(bomb.getLocation(), subweapon.getExplRadius(), data);
				MainGame.SphereDamager(data, bomb.getLocation(), subweapon, subweapon.getExplRadius(), false);
				//bomb.getWorld().playSound(bomb.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS,1f,1f);
				SplatColor color= DataStore.getArenaData(data.getArena()).getSplatColor(data.getTeamid());
				MainGame.fireworkExplosion(bomb.getLocation(),color);


				for (PlayerData pd : DataStore.getArenaPlayersList(data.getArena())) {
					if(data.getTeamid() != pd.getTeamid()) {
						Player pe = Bukkit.getPlayer(pd.getName());
						if (pe.getLocation().distanceSquared(this.bomb.getLocation()) < subweapon.getExplRadius() * subweapon.getExplRadius()*2) {
							pe.addPotionEffect(glow);

						}
					}
				}
				bomb.remove();
				this.cancel();
			}else{
				time--;
			}
		}else{
			if(DataStore.getArenaData(this.data.getArena()).getGameStatus() != DataStore.GameStatusEnum.INGAME){

				bomb.remove();
				this.cancel();
				return;
			}
			if(data.getSubCount()>id+2){
				this.activated = true;
			}

			for (PlayerData pd : DataStore.getArenaPlayersList(data.getArena())) {
				if(data.getTeamid() != pd.getTeamid()) {
					Player pe = Bukkit.getPlayer(pd.getName());
					if (pe.getLocation().distanceSquared(this.bomb.getLocation()) < subweapon.getExplRadius() * subweapon.getExplRadius()) {
						this.activated = true;

					}
				}
			}
			if(!SplatColorManager.isBelowBlockTeamColor(bomb.getLocation(),data, true)
					&&SplatColorManager.isBelowBlockTeamColor(bomb.getLocation(),data, false)){
				this.activated=true;
			}
			if(this.activated){
				bomb.getWorld().playSound(bomb.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, SoundCategory.PLAYERS,1f,1.5f);
				bomb.getWorld().playSound(bomb.getLocation(), Sound.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS,1.5f,1f);

			}
		}
	}
}
