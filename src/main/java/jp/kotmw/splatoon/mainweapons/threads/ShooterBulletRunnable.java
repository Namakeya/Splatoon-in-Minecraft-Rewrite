package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.mainweapons.MainWeapon;
import jp.kotmw.splatoon.mainweapons.Shooter;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.MaterialUtil;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ShooterBulletRunnable extends BukkitRunnable {

    private String playerName;
    private Projectile bullet;
    private MainWeapon shooter;

    private int paintInterval=5;
    private int paintTimer=0;



    public ShooterBulletRunnable(String name,Projectile bullet,MainWeapon shooter) {
        this.playerName = name;
        this.bullet=bullet;
        this.shooter=shooter;
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
        this.bullet.getWorld().spawnParticle(Particle.BLOCK_DUST,bullet.getLocation(),2, MaterialUtil.fromColorIdToWool(color.getColorID()).createBlockData());
        double speed=bullet.getVelocity().length();
        if(speed>weapon.getAirResistance()){
            Vector vel=bullet.getVelocity();
            bullet.setVelocity(vel.setX(vel.getX()*(1-weapon.getAirResistance())));
            bullet.setVelocity(vel.setZ(vel.getZ()*(1-weapon.getAirResistance())));
        }
        if(paintTimer>=paintInterval){
            Paint.UnderPaint(bullet.getLocation(), weapon.getFlyingPaintRadius()* shooter.getDecayRate(bullet,weapon), data);
            paintInterval= (int) (3/speed);
            paintTimer=0;
        }else{
            paintTimer++;
        }
    }
}
