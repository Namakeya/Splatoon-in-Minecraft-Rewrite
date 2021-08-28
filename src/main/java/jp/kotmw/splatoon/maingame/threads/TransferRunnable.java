package jp.kotmw.splatoon.maingame.threads;

import java.util.Collections;
import java.util.List;

import jp.kotmw.splatoon.maingame.*;
import jp.kotmw.splatoon.manager.SplatBossBar;
import jp.kotmw.splatoon.manager.SplatScoreBoard;
import jp.kotmw.splatoon.specialweapon.SpecialWeapon;
import jp.kotmw.splatoon.superjump.Superjump;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.PlayerData;

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
			int team = 1, posisions = 1, players = 1;
			data.setScoreBoard(new SplatScoreBoard(data));
			data.getScoreboard().DefaultScoreBoard(type);
			for(PlayerData playerdata : datalist) {
				if(players > data.getTotalPlayerCount()) {
					MainGame.sendMessage(playerdata, ChatColor.RED+"転送先ステージの許容人数をオーバーしたため、転送ができませんでした");
					MainGame.sendMessage(playerdata, ChatColor.YELLOW+"このまま残ることも可能ですが、待機前の場所に戻る場合は"+ChatColor.WHITE+" /splat leave "+ChatColor.YELLOW+"コマンドを使用してください");
					continue;
					/*
					 * メモ
					 * 
					 * TODO 対象ステージの最大許容人数オーバー時どうするか
					 * 
					 * 取りあえずオーバーした人はロビーに戻るかこのまま残るか選択出来るようにする
					 * 
					 */
				}
				if(team > data.getMaximumTeamNum()) {
					team = 1;
					posisions++;
				}
				playerdata.setMove(false);
				playerdata.setArena(this.data.getName());
				playerdata.setTeamId(team);
				playerdata.setPosisionId(posisions);
				MainGame.setInv(playerdata);
				Player player = Bukkit.getPlayer(playerdata.getName());
				player.setGameMode(GameMode.SURVIVAL);
				player.setExp(0.99f);//1.12.2対応のため
				player.setHealth(20);

				data.getScoreboard().setTeam(playerdata);
				data.getScoreboard().showBoard(playerdata);

				MainGame.Teleport(playerdata, this.data.getTeamPlayerPosision(team, posisions).convertLocation());
				player.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).setBaseValue(1);
				team++;
				players++;
			}
			data.setBossBar(new SplatBossBar(data));
			if(SpecialWeapon.SPECIALENABLED) {
				data.getBossBar().showAll();
			}
			//System.out.println("transfer");
			switch(type) {
			case Turf_War:
				data.setBattleClass(new Turf_War(data));
				SplatZones.clearAreaStand(data);
				break;
			case Splat_Zones:
				data.setBattleClass(new SplatZones(data));
				//TransferRunnable内で動かすとなぜかアーマースタンドが二重に生成されたのでBattleRunnableに移動
				//((SplatZones)data.getBattleClass()).showZone(0);
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

		Superjump sj = new Superjump(data);
		data.setSuperjump(sj);
	}
}
