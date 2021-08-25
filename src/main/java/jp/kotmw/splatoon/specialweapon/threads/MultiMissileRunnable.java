package jp.kotmw.splatoon.specialweapon.threads;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.GameItems;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class MultiMissileRunnable extends BukkitRunnable {


    private PlayerData launcher;


    private int phase;
    private int maxPhase;
    private Entity target;

    private PotionEffect slow=new PotionEffect(PotionEffectType.SLOW,20,4);

    public MultiMissileRunnable(PlayerData launcher,int maxPhase,Entity target){
        this.launcher=launcher;
        this.target=target;
        phase=0;
        this.maxPhase=maxPhase;
    }

    @Override
    public void run() {
        if(phase>maxPhase){
            launcher.setUsingSpecial(false);
            this.cancel();
        }else{
            Player pe=Bukkit.getPlayer(launcher.getName());
            double dist=(maxPhase-phase)*0.2;
            if(phase%2==1)dist=-dist;
            double x=pe.getLocation().getDirection().getZ()*dist;
            double z=-pe.getLocation().getDirection().getX()*dist;

            Creeper missile=target.getWorld().spawn(pe.getLocation().add(x,2,z),Creeper.class);
            missile.getAttribute(Attribute.GENERIC_FOLLOW_RANGE).setBaseValue(0);
            missile.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(0);
            missile.setExplosionRadius(0);
            missile.setVelocity(new Vector(0,1,0));
            missile.setGravity(false);
            missile.setInvulnerable(true);

            new MultiMissileBulletRunnable(missile,launcher,target,phase*4).runTaskTimer(Main.main, 0, 1);
            pe.playSound(pe.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1f,1f);
            pe.addPotionEffect(slow);
            phase++;
        }

    }
}
