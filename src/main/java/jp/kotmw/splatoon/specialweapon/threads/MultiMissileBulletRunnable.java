package jp.kotmw.splatoon.specialweapon.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.MaterialUtil;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class MultiMissileBulletRunnable extends BukkitRunnable {

    private Creeper missile;
    private PlayerData launcher;
    private Location launchPoint;
    private Entity target;
    private ArmorStand marker;

    private int timer;
    private int maxAscentTime;
    private int delay;

    private Location idealLoc;

    private int phase;

    public MultiMissileBulletRunnable(Creeper missile, PlayerData launcher, Entity target,int delay){
        this.missile=missile;
        this.launcher=launcher;
        this.target=target;
        this.delay=delay;
        this.maxAscentTime=delay+60;
        this.launchPoint= Bukkit.getPlayer(launcher.getName()).getLocation().clone();
        this.idealLoc=missile.getLocation().clone();
        phase=0;
        this.timer =0;
    }


    @Override
    public void run() {
        if(this.missile.isDead()){
            this.cancel();
            this.marker.remove();
            return;
        }
        SplatColor color = DataStore.getArenaData(launcher.getArena()).getSplatColor(launcher.getTeamid());
        if(timer==this.delay){
            Random rand=new Random();
            double randx=(rand.nextDouble()-0.5)*3;
            double randz=(rand.nextDouble()-0.5)*3;
            marker=missile.getWorld().spawn(target.getLocation().add(randx,50,randz),ArmorStand.class);
            marker.setSmall(true);
            marker.setInvisible(true);
            marker.setInvulnerable(true);
            marker.setHelmet(new ItemStack(MaterialUtil.fromColorIdToCarpet(color.getColorID())));
        }
        if(phase==0){

            phase=1;
        }else if(phase==1){
            this.idealLoc=this.idealLoc.add(new Vector(0,1,0));

            if(this.missile.getLocation().distanceSquared(this.idealLoc)>4){
                this.missile.teleport(this.idealLoc);
            }
            this.missile.setVelocity(new Vector(0,1,0));
            this.missile.getWorld().spawnParticle(Particle.BLOCK_DUST,missile.getLocation(),1,
                    MaterialUtil.fromColorIdToWool(color.getColorID()).createBlockData());
            this.missile.getWorld().spawnParticle(Particle.REDSTONE, missile.getLocation(), 3,
                    0, -2, 0, new Particle.DustOptions(color.getColor(), 1.5f));
            if(this.timer >=this.maxAscentTime) {

                this.missile.setGravity(true);
                this.missile.teleport(marker.getLocation().add(0,50,0));
                phase = 2;
            }
        }else if(phase==2){
            if(this.missile.isOnGround()){
                Paint.SpherePaint(this.missile.getLocation(),2,launcher,false);
                MainGame.SphereDamager(launcher,this.missile.getLocation(),0,8,6,2,false);
                MainGame.fireworkExplosion(this.missile.getLocation(),color);
                this.missile.getWorld().playSound(this.missile.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,0.7f,1f);
                this.missile.remove();
                this.marker.remove();
                this.cancel();
                return;
            }

        }
        this.timer++;
    }
}
