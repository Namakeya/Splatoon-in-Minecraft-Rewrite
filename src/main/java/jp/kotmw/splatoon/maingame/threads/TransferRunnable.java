package jp.kotmw.splatoon.maingame.threads;

import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.GameSigns;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.maingame.SplatZones;
import jp.kotmw.splatoon.maingame.Turf_War;
import jp.kotmw.splatoon.manager.SplatScoreBoard;

public class TransferRunnable extends BukkitRunnable {

	private ArenaData data;
	private String beforeroom;
	private int second;
	private BattleType type;

	public TransferRunnable(ArenaData data, String beforeroom, int second, BattleType type) {
		this.data = data;
		this.beforeroom = beforeroom;
		this.second = second;
		this.type = type;
	}

	@Override
	public void run() {
		if(second > 0) {
			for(PlayerData data : DataStore.getRoomPlayersList(beforeroom))
				MainGame.sendTitle(data,
						0,
						5,
						0,
						ChatColor.GREEN+"ステージに転送します",
						ChatColor.BLUE.toString()+"---[  "+ChatColor.DARK_AQUA.toString()+ChatColor.BOLD+second+ChatColor.BLUE.toString()+"  ]---");
		} else {
			List<PlayerData> datalist = DataStore.getRoomPlayersList(beforeroom);
			Collections.shuffle(datalist);
			int team = 1, team1 = 1, team2 = 1;
			for(PlayerData data : datalist) {
				if(team == this.data.getTeamsCount()) team = 1;
				data.setMove(false);
				data.setArena(this.data.getName());
				data.setTeamid(team);
				MainGame.setInv(data);
				Player player = Bukkit.getPlayer(data.getName());
				player.setGameMode(GameMode.ADVENTURE);
				player.setExp(1.0f);
				SplatScoreBoard.DefaultScoreBoard(this.data, type);
				SplatScoreBoard.setTeam(data);
				SplatScoreBoard.showBoard(data);
				if(team == 1) {
					MainGame.Teleport(data, this.data.getTeam1(team1).convertLocation());
					team1++;
				} else if(team == 2) {
					MainGame.Teleport(data, this.data.getTeam2(team2).convertLocation());
					team2++;
				}
				team++;
			}
			switch(type) {
			case Turf_War:
				data.setBattleClass(new Turf_War(data.getName()));
				SplatZones.clearAreaStand(data);
				break;
			case Splat_Zones:
				data.setBattleClass(new SplatZones(data.getName()));
				((SplatZones)data.getBattleClass()).showZone();
				break;
			case Rain_Maker:
				break;
			}
			GameSigns.UpdateJoinSign(beforeroom);
			StageTransfar(data);
			this.cancel();
		}
		second--;
	}

	private void StageTransfar(ArenaData data) {
		BattleRunnable task = new BattleRunnable(data, MainGame.getTime(type), type);
		task.runTaskTimer(Main.main, 0, 1);
		data.setTask(task);
	}
}
