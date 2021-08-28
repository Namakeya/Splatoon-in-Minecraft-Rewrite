package jp.kotmw.splatoon.maingame;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class Turf_War extends BattleClass{

    private boolean resutcount = false;//最終的に塗り面積を集計するかしないか、するならtrue
    public Turf_War(ArenaData data) {
        super(data);
    }

}
