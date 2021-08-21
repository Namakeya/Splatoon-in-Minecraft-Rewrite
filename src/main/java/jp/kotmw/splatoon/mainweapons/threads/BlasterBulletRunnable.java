package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.Blaster;
import jp.kotmw.splatoon.mainweapons.MainWeapon;
import jp.kotmw.splatoon.mainweapons.Shooter;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.MaterialUtil;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class BlasterBulletRunnable extends BukkitRunnable {

    private String playerName;
    private Projectile bullet;
    private MainWeapon blaster;

    private int paintInterval=5;
    private int paintTimer=0;



    public BlasterBulletRunnable(String name, Projectile bullet, MainWeapon mainWeapon) {
        this.playerName = name;
        this.bullet=bullet;
        this.blaster=mainWeapon;
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

        if(bullet.getTicksLived()-weapon.getFlyDecayTick()>=0){
            MainGame.SphereDamager(data, bullet.getLocation(), weapon.getExplosionDamage(),weapon.getExplosionDamage(), weapon.getExplosionRadius(), false);
            Paint.SpherePaint(bullet.getLocation(),weapon.getExplosionRadius(), data);
            bullet.remove();
            this.cancel();
            FireworkEffect effect=FireworkEffect.builder().withColor(color.getColor()).with(FireworkEffect.Type.BALL).build();

            Firework fw = (Firework) bullet.getWorld().spawnEntity(bullet.getLocation(), EntityType.FIREWORK);
            FireworkMeta fwm = fw.getFireworkMeta();

            fwm.addEffect(effect);

            fw.setFireworkMeta(fwm);
            fw.detonate();
            return;
        }


        this.bullet.getWorld().spawnParticle(Particle.BLOCK_DUST,bullet.getLocation(),2, MaterialUtil.fromColorIdToWool(color.getColorID()).createBlockData());
        double speed=bullet.getVelocity().length();
        if(speed>weapon.getAirResistance()){
            bullet.setVelocity(bullet.getVelocity().multiply((speed-weapon.getAirResistance())/speed));
        }
        if(paintTimer>=paintInterval){
            Paint.UnderPaint(bullet.getLocation(), weapon.getFlyingPaintRadius()* blaster.getDecayRate(bullet,weapon), data);
            paintInterval= (int) (3/speed);
            paintTimer=0;
        }else{
            paintTimer++;
        }
    }
}
