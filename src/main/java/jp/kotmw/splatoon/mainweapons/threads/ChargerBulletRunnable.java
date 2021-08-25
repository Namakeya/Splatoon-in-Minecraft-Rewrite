package jp.kotmw.splatoon.mainweapons.threads;

import com.sk89q.worldedit.math.MathUtils;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.MaterialUtil;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import static jp.kotmw.splatoon.mainweapons.ArrowCharger.fullcharge;

public class ChargerBulletRunnable extends BukkitRunnable {

    private String playerName;
    private Projectile bullet;
    private int charge;

    private int paintInterval=0;
    private int paintTimer=0;



    public ChargerBulletRunnable(String name, Projectile bullet,int charge) {
        this.playerName = name;
        this.bullet=bullet;
        this.charge=charge;
    }

    @Override
    public void run() {
        if(this.bullet.isDead()){
            this.cancel();
        }
        if(!DataStore.hasPlayerData(playerName)) {
            this.cancel();
            return;
        }
        //System.out.println("call");
        PlayerData data = DataStore.getPlayerData(playerName);
        WeaponData weapon=DataStore.getWeapondata(data.getWeapon());
        SplatColor color = DataStore.getArenaData(data.getArena()).getSplatColor(data.getTeamid());

        double speed=bullet.getVelocity().length();
        if(speed>weapon.getAirResistance()){
            bullet.setVelocity(bullet.getVelocity().multiply((speed-weapon.getAirResistance())/speed));
        }
        if(bullet.getTicksLived()-weapon.getFlyDecayTick()>=0){
            if(!bullet.hasGravity()){
                bullet.setGravity(true);
            }
        }
        if(paintTimer>=paintInterval){
            //System.out.println("charge in CBR: "+charge);
            double radius=weapon.getFlyingPaintRadius();


            if(speed>radius){
                int n= (int) Math.ceil(speed/radius);
                radius=radius*(1+((double)charge/fullcharge))*0.5;
                //System.out.println("n="+n);
                for(int i=0;i<n;i++){
                    Location loc=bullet.getLocation().subtract(bullet.getVelocity().multiply((double)i/n));
                    Paint.UnderCylinderPaint(loc , radius,4, data);
                    this.bullet.getWorld().spawnParticle(Particle.BLOCK_DUST,loc,3,
                            MaterialUtil.fromColorIdToWool(color.getColorID()).createBlockData());
                    this.bullet.getWorld().spawnParticle(Particle.REDSTONE,loc,3,
                            0.5,0.5,0.5, new Particle.DustOptions(color.getColor(),1.5f));
                }
            }else{
                Paint.UnderCylinderPaint(bullet.getLocation(), radius,4, data);
                this.bullet.getWorld().spawnParticle(Particle.BLOCK_DUST,bullet.getLocation(),2,
                        MaterialUtil.fromColorIdToWool(color.getColorID()).createBlockData());
                this.bullet.getWorld().spawnParticle(Particle.REDSTONE,bullet.getLocation(),1,
                        0.5,0.5,0.5, new Particle.DustOptions(color.getColor(),1.5f));
            }
            paintInterval= (int) (3/speed);
            paintTimer=0;
        }else{
            paintTimer++;
        }
    }
}
