package jp.kotmw.splatoon.mainweapons.threads;

import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.mainweapons.MainWeapon;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.util.Polar_coodinates;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**相打ち判定用*/
public class RollerRollRunnable extends BukkitRunnable {

    String name;
    MainWeapon mainWeapon;
    Location location;

    int timer=0;

    public RollerRollRunnable(String name,MainWeapon mainWeapon,Location location){
        this.name=name;
        this.mainWeapon=mainWeapon;
        this.location=location;
    }


    @Override
    public void run() {
        if (DataStore.hasPlayerData(name) && Bukkit.getPlayer(name) != null) {
            PlayerData pd=DataStore.getPlayerData(name);
            Player pe=Bukkit.getPlayer(name);
            WeaponData weapon = DataStore.getWeapondata(pd.getWeapon());
            Color color = DataStore.getArenaData(pd.getArena()).getSplatColor(pd.getTeamid()).getColor();

            int ways=(int)Math.ceil(weapon.getRadius()*1.8);
            for(int i=0;i<ways;i++) {
                float yaw = -location.getYaw()+(i-(ways-1f)/2f)*30;
                double yawradian = Math.toRadians(yaw);
                //Polar_coodinates pc, pc2 = new Polar_coodinates(pe.getWorld(), weapon.getRadius() / 2, Math.toRadians(yaw), 0);
                Vector offset = new Vector(Math.sin(yawradian), 0, Math.cos(yawradian)).multiply(weapon.getRadius());
                //System.out.println(offset);
                Location rollCenter = location.clone().add(offset);
                //todo onGroundつけるかどうか
                if(!pd.isDead() && pe.isOnGround()) {
                    Paint.UnderCylinderPaint(rollCenter, 0.8, 1.5, pd);
                }
                MainGame.sync(() -> {
                    MainGame.SphereDamager(pd, rollCenter, weapon.getDamage(), weapon.getDamage(), 0, 1, false);
                });
            }

        }
        if(timer>2){
            this.cancel();
        }
        timer++;
    }
}
