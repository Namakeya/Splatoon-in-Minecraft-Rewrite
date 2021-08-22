package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.mainweapons.MainWeapon;
import jp.kotmw.splatoon.mainweapons.Roller;
import jp.kotmw.splatoon.mainweapons.Shooter;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.MaterialUtil;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RollerBulletRunnable extends BukkitRunnable {

    private String playerName;
    private Projectile bullet;
    private MainWeapon mainWeapon;

    private int paintInterval=5;
    private int paintTimer=0;


    public RollerBulletRunnable(String name, Projectile bullet,MainWeapon mainWeapon) {
        this.playerName = name;
        this.bullet=bullet;
        this.mainWeapon=mainWeapon;
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
        this.bullet.getWorld().spawnParticle(Particle.BLOCK_DUST,bullet.getLocation(),2,
                MaterialUtil.fromColorIdToWool(color.getColorID()).createBlockData());
        this.bullet.getWorld().spawnParticle(Particle.REDSTONE,bullet.getLocation(),1,
                0.5,0.5,0.5, new Particle.DustOptions(color.getColor(),1.5f));
        double speed=bullet.getVelocity().length();
        if(speed>weapon.getAirResistance()){
            Vector vel=bullet.getVelocity();
            bullet.setVelocity(vel.setX(vel.getX()*(1-weapon.getAirResistance())));
            bullet.setVelocity(vel.setZ(vel.getZ()*(1-weapon.getAirResistance())));
        }
        if(paintTimer>=paintInterval){

            Paint.UnderCylinderPaint(bullet.getLocation(), weapon.getFlyingPaintRadius()* mainWeapon.getDecayRate(bullet,weapon),1, data);
            paintInterval= (int) (3/speed);
            paintTimer=0;
        }else{
            paintTimer++;
        }
    }
}
