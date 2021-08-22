package jp.kotmw.splatoon.superjump;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.SquidMode;
import jp.kotmw.splatoon.maingame.threads.SquidRunnable;
import jp.kotmw.splatoon.util.SplatColor;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static jp.kotmw.splatoon.superjump.Superjump.respawnItemName;
import static jp.kotmw.splatoon.util.MaterialUtil.fromColorIdToBanner;

public class SuperjumpRunnable extends BukkitRunnable {

    private ArenaData arenaData;
    private PlayerData playerData;
    private Location destination;
    private Location startpoint;
    private int tick;

    private double gravity=0.08;
    private double drag=0.02;
    private int traveltime;

    private Location idealPosition;
    private Vector idealVelocity;

    private ArmorStand marker;

    public SuperjumpRunnable(ArenaData arenaData,PlayerData playerData,Location destination,int traveltime){
        this.arenaData=arenaData;
        this.playerData=playerData;
        this.destination=destination;
        Player pe=Bukkit.getPlayer(playerData.getName());
        this.startpoint=pe.getLocation();
        this.traveltime=traveltime;
    }
    @Override
    public void run() {
        Player pe=Bukkit.getPlayer(playerData.getName());
        Vector diff=this.destination.clone().subtract(startpoint).toVector();
        if(DataStore.getArenaData(playerData.getArena()).getGameStatus() != DataStore.GameStatusEnum.INGAME){
            pe.setInvulnerable(false);
            playerData.setSuperjumpStatus(0);
            marker.remove();
            this.cancel();
            return;
        }
        if(playerData.getSuperjumpStatus() == 1){


            /*
            Vector horiz=diff.clone();
            horiz.setY(0);
            Vector verti=diff.clone();
            verti.setX(0);
            verti.setZ(0);
            */

            //double dh=horiz.length();
            //double vh=this.getInitialHorizontalSpeed(dh,traveltime,drag);
            //System.out.println("diff: "+diff);
            double vx=this.getInitialHorizontalSpeed(diff.getX()*1.2,traveltime,drag);
            double vz=this.getInitialHorizontalSpeed(diff.getZ()*1.2,traveltime,drag);
            double vv=this.getInitialVerticalSpeed(diff.getY(),traveltime,drag,gravity);

            this.idealVelocity=new Vector(vx,vv,vz);
            //System.out.println("ideal velocity: "+this.idealVelocity);
            pe.setVelocity(idealVelocity.clone());
            this.idealPosition=pe.getLocation();

            tick=0;

            marker=pe.getWorld().spawn(this.destination,ArmorStand.class);
            marker.setBasePlate(false);
            marker.setSmall(true);
            marker.setGravity(false);
            marker.setInvisible(true);
            marker.setInvulnerable(true);
            marker.setMarker(true);
            SplatColor teamcolor=arenaData.getSplatColor(playerData.getTeamid());
            ItemStack itembanner=new ItemStack(Material.WHITE_BANNER);
            BannerMeta bannerMeta= (BannerMeta) itembanner.getItemMeta();
            bannerMeta.addPattern(new Pattern(teamcolor.getDyeColor(), PatternType.STRIPE_CENTER));
            bannerMeta.addPattern(new Pattern(teamcolor.getDyeColor(), PatternType.STRIPE_BOTTOM));
            bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.CURLY_BORDER));
            bannerMeta.addPattern(new Pattern(DyeColor.WHITE, PatternType.GRADIENT));
            itembanner.setItemMeta(bannerMeta);
            marker.setHelmet(itembanner);
            playerData.setSuperjumpStatus(2);

            pe.setInvulnerable(true);

            pe.playSound(pe.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,1,1);
            pe.playSound(pe.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,0.5f,1);
        }else if(playerData.getSuperjumpStatus() == 2){

            this.calculatePos(pe);
            if(tick>30) {
                //this.idealPosition = destination.clone().add(0, 100, 0);
                //idealPosition.setPitch(90);
               // pe.teleport(idealPosition);
                SquidMode.toMan(pe, playerData);
                playerData.setSuperjumpStatus(3);
            }
        }else if(playerData.getSuperjumpStatus() == 3){
            Location markerloc=marker.getLocation();
            markerloc.setYaw(markerloc.getYaw()+12);
            marker.teleport(markerloc);

            Vector diffNow=this.destination.clone().subtract(pe.getLocation()).toVector();
            if(diff.getX()*diffNow.getX()<0){ //符号が逆転 = 通り過ぎているとき
                this.idealVelocity.setX(0);
            }
            if(diff.getZ()*diffNow.getZ()<0){ //符号が逆転 = 通り過ぎているとき
                this.idealVelocity.setZ(0);
            }
            this.calculatePos(pe);
            if(this.idealPosition.getY()<destination.getY()
            ||tick>200) {
                pe.setInvulnerable(false);
                destination.setDirection(pe.getLocation().getDirection());
                pe.teleport(destination.clone());
                playerData.setSuperjumpStatus(0);
                marker.remove();
                this.cancel();
            }
        }
        tick++;
    }

    /**Using Sigma of Vh * (1-drag)^t*/
    public double getInitialHorizontalSpeed(double distance,int tick,double drag){
        double r=1.0-drag;

        return distance*(-drag)/(Math.pow(r,tick)-1);
    }

    public double getInitialVerticalSpeed(double distance,int tick,double drag,double gravity){
        double r=1.0-drag;
        return (distance*(r-1)-tick*r*gravity)/(Math.pow(r,tick)-1) + r*gravity/(r-1);
    }

    public void calculatePos(Player pe){

        this.idealVelocity=this.idealVelocity.add(new Vector(0,-gravity,0));
        this.idealVelocity=this.idealVelocity.multiply(1-drag);
        if(this.idealPosition.distanceSquared(pe.getLocation())>1){
            //System.out.println("too different position!");
            //System.out.println("pos : "+pe.getLocation());
            this.idealPosition.setPitch(pe.getLocation().getPitch());
            this.idealPosition.setYaw(pe.getLocation().getYaw());
            pe.teleport(this.idealPosition.clone());
            pe.setVelocity(this.idealVelocity.clone());
        }else if(this.idealVelocity.distanceSquared(pe.getVelocity())>0.1){
            //System.out.println("too different velocity!");
            pe.setVelocity(this.idealVelocity.clone());
        }
        //System.out.println("distance: "+this.idealPosition.distance(pe.getLocation()));
        //System.out.println("ideal : "+this.idealPosition);
        //System.out.println("desti : "+this.destination);
        this.idealPosition=this.idealPosition.add(this.idealVelocity);
    }

}
