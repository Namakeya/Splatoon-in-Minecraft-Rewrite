package jp.kotmw.splatoon.specialweapon;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.SpecialType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.specialweapon.threads.MultiMissileBulletRunnable;
import jp.kotmw.splatoon.specialweapon.threads.MultiMissileRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class MultiMissile extends SpecialWeapon{
    public MultiMissile() {
        super(SpecialType.MultiMissile, "bullet_multimissile");
    }

    @Override
    public boolean doOnInteract(PlayerInteractEvent e, PlayerData pd, Player pe) {
        List<PlayerData> targetList=new ArrayList<PlayerData>();
        for(PlayerData data : DataStore.getArenaPlayersList(pd.getArena())) {
            if(data.getTeamid() != pd.getTeamid()) {
                targetList.add(data);
            }

        }
        if(targetList.size()==0){
            return false;
        }
        int maxPhase=5;
        int delay=6;
        switch(targetList.size()){
            case 1:
                maxPhase=10;
                break;
            case 2:
                maxPhase=6;
                delay=10;
                break;
            default:
                maxPhase=4;
                delay=6;
        }
        int i=0;
        for(PlayerData data:targetList){

            Player te= Bukkit.getPlayer(data.getName());
            new MultiMissileRunnable(pd,maxPhase,te).runTaskTimer(Main.main, i*delay, 2);
            i++;
        }

        return true;
    }
}
