package jp.kotmw.splatoon.maingame.threads;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.Turf_War;

public class ResultRunnable extends BukkitRunnable {

	private Turf_War battle;
	private int tick = 26+10+10;
	private int i = 0;
	private int ii = 98;
	private int[] scorePercentage;
	private static String base = "||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||";
	private static String space = "            ";
 
	public ResultRunnable(Turf_War battle) {
		this.battle = battle;
		this.scorePercentage=new int[battle.getArena().getMaximumTeamNum()];
		for(int i=0;i<battle.getArena().getMaximumTeamNum();i++){
			scorePercentage[i] = (int) ((battle.getTeamScore(i+1) / (battle.getTotalTeamScore()))*100);
		}

	}

	@Override
	public void run() {
		ArenaData data = battle.getArena();
		if(tick >= 20) {
			for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
				Player enpl=Bukkit.getPlayer(player.getName());
				if(tick == 45)enpl.playSound(enpl.getLocation(), Sound.ENTITY_CREEPER_PRIMED,1,0.8f);
				MainGame.sendTitle(player, 0, 5, 0, " ", MeterText(data, i, ii));
			}
			i++;
			ii--;
		} else if(tick < 20 && tick >= 15){
			for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
				Player enpl= Bukkit.getPlayer(player.getName());
				if(tick == 19)enpl.playSound(enpl.getLocation(), Sound.ENTITY_GENERIC_EXPLODE,1,0.8f);
				MainGame.sendTitle(player, 0, 5, 0, " ", MeterText(data, scorePercentage[0], 99-scorePercentage[1]));//ここで良くエラーでる
			}
		} else if(tick < 15 && tick >= 11) {

			battle.sendResult();
		} else if(tick < 0) {
			MainGame.end(data, false);
			cancel();
		}
		tick--;
	}

	private static String MeterText(ArenaData data, int i, int ii)
	{
		i=i<0?0:i;
		ii=ii>98?98:ii;
		i=i>=ii?ii-1:i;
		ii=ii<=i?i+1:ii;
		return data.getSplatColor(1).getChatColor()+base.substring(0, i)+ ChatColor.GRAY +base.substring(i + 1, ii)+ data.getSplatColor(2).getChatColor()+base.substring(ii + 1, 99);
	}
/*
	private static String MeterText2(ArenaData data, int i, int ii)
	{
		i=i<0?0:i;
		ii=ii<i?i+1:ii;
		ii=ii>98?98:ii;
		i=i>ii?ii-1:i;
		return data.getSplatColor(1).getChatColor()+base.substring(0, i)+ data.getSplatColor(2).getChatColor()+base.substring(ii, 99)+ space;
	}
*/
}
