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

            float yaw = -location.getYaw();
            Polar_coodinates pc, pc2 = new Polar_coodinates(pe.getWorld(), weapon.getRadius() / 2, Math.toRadians(yaw), 0);
            for (double i = -weapon.getRadius() / 2; i <= weapon.getRadius() / 2; i += 0.5) {
                for (int j = 0; j <= 1; j++) {
                    pc = new Polar_coodinates(pe.getWorld(), i, Math.toRadians(yaw) + (Math.PI / 2), 0);
                    Location judgeloc = location.clone().add(0, j - 0.5, 0).add(pc2.convertLocation()).add(pc.convertLocation());
                    Paint.PaintWool(pd, judgeloc.getBlock());
                    MainGame.sync(() -> {
                        MainGame.Damager(pd, judgeloc, DataStore.getWeapondata(pd.getWeapon()).getDamage());
                    });
                    if(timer==0) {
                        pe.getWorld().spawnParticle(Particle.REDSTONE, judgeloc, 1,
                                0.5, 0.5, 0.5, new Particle.DustOptions(color, 1.0f));
                    }

                }
            }
        }
        if(timer>2){
            this.cancel();
        }
        timer++;
    }
}
