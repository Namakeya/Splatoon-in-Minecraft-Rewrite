package jp.kotmw.splatoon.specialweapon;
import jp.kotmw.splatoon.gamedatas.*;
import jp.kotmw.splatoon.gamedatas.DataStore.SpecialType;
import jp.kotmw.splatoon.mainweapons.MainWeapon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public abstract class SpecialWeapon implements Listener{
    public static final boolean SPECIALENABLED=true;
    public static final boolean SPPENABLED=true;

    public SpecialType specialType;
    public String bulletname;

    public SpecialWeapon(SpecialType specialType, String bulletname){
        this.specialType =specialType;
        this.bulletname=bulletname;
    }

    @Nullable
    public ArenaData getArena(Player pe){
        if(DataStore.hasPlayerData(pe.getName())){
            PlayerData pd=DataStore.getPlayerData(pe.getPlayer().getName());
            if(pd.getArena()!=null){
                return DataStore.getArenaData(pd.getArena());
            }
        }
        return null;
    }

    public boolean isMyWeaponType(PlayerData data){
        return getSpecialWeapon(data).equalsIgnoreCase(specialType.name());
    }

    public String getSpecialWeapon(PlayerData data){
        return DataStore.getWeapondata(data.getWeapon()).getSpecialWeapon();
    }

    public static SpecialWeaponData getSpecialWeaponData(PlayerData data){
        return DataStore.getSpecialWeaponData(DataStore.getWeapondata(data.getWeapon()).getSpecialWeapon());
    }

    public boolean canShoot(PlayerData data){

        Player player = Bukkit.getPlayer(data.getName());
        //System.out.println(player.getInventory().getItemInMainHand());
        return (!SPPENABLED || data.isCanUseSpecial())
        &&!data.isSquidMode() && isMyWeapon(data,player.getInventory().getItemInMainHand());
    }

    public boolean isMyWeapon(PlayerData data, ItemStack item){
        if(isMyWeaponType(data)
                && !data.isAllCancel()
                && item != null
                && isMyWeaponType(data)
                && item.hasItemMeta()
                && item.getItemMeta().getDisplayName().equalsIgnoreCase(this.specialType.name())){
            //System.out.println("is my weapon");
            return true;
        }
        //System.out.println("not my weapon");
        return false;
    }

    public boolean isMyBullet(Entity entity){

        if(bulletname==null){
            bulletname="bullet_"+this.specialType.name();
            //System.out.println("buletname: "+bulletname);
        }
        if (entity.getCustomName() !=null
                &&entity.getCustomName().equalsIgnoreCase(bulletname)) {
            return true;
        }

        return false;
    }
    public boolean checkOnInteract(PlayerInteractEvent e){
        if(getArena(e.getPlayer())!=null) {
            Action action = e.getAction();
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK
                    ||action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
                //System.out.println("interact");
                Player player = e.getPlayer();
                PlayerData data = DataStore.getPlayerData(player.getName());
                //System.out.println("suitable action");
                if (canShoot(data)) {
                    return true;
                }
            }
        }
        return false;
    }

    public abstract boolean doOnInteract(PlayerInteractEvent e,PlayerData pd,Player pe);

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        //System.out.println("interact");
        if(checkOnInteract(e)){
            e.setCancelled(true);
            Player player = e.getPlayer();
            PlayerData data = DataStore.getPlayerData(player.getName());
            if(doOnInteract(e,data,player)){
                player.setExp(0.999f);
                data.setCanUseSpecial(false);
                data.setSpecialPoint(0);
                player.getInventory().setItem(2,null);
                player.getInventory().setHeldItemSlot(0);
            }
        }
    }
}
