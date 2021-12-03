package jp.kotmw.splatoon.specialweapon;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.SpecialType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.specialweapon.threads.MultiMissileRunnable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class SuperSensor extends SpecialWeapon{

    private static PotionEffect glow=new PotionEffect(PotionEffectType.GLOWING,120,1);

    public SuperSensor() {
        super(SpecialType.SuperSensor, "bullet_supersensor");
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

        pe.getWorld().playSound(pe.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, SoundCategory.PLAYERS,0.8f,1f);
        for(PlayerData data:targetList){

            Player te= Bukkit.getPlayer(data.getName());
            te.addPotionEffect(glow);
        }
        pd.setSpecialProgress(0);
        return true;
    }
}
