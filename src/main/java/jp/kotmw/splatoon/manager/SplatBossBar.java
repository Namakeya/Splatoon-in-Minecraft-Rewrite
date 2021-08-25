package jp.kotmw.splatoon.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import jp.kotmw.splatoon.mainweapons.MainWeapon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;

public class SplatBossBar {

	private Map<String, BossBar> playerbossbar = new HashMap<>();
	private ArenaData data;
	
	public SplatBossBar(ArenaData data) {// ❙
		this.data = data;
		for(PlayerData pd:DataStore.getArenaPlayersList(data.getName())){
			BossBar bar = Bukkit.createBossBar("SpecialGauge", BarColor.GREEN, BarStyle.SOLID);
			bar.setProgress(0.0);
			playerbossbar.put(pd.getName(), bar);
		}
	}
	
	/**
	 * 
	 */
	public void updateBar() {

		for(Map.Entry<String,BossBar> entry:playerbossbar.entrySet()){
			PlayerData pd=DataStore.getPlayerData(entry.getKey());
			double spp = pd.getSpecialPoint();
			double progress = spp/ MainWeapon.getWeaponData(pd).getSpecialPoint();
			BossBar bar = entry.getValue();
			bar.setProgress((progress > 1.0 ? 1.0 : progress));
			bar.setColor(progress>0.99?BarColor.YELLOW:BarColor.BLUE);
		}
	}

	public void updateLifeBar() {
		/*
		teambossbar.entrySet().forEach(team -> {
			BossBar bar = team.getValue();
			bar.setTitle(updateisLifeBar_Allteam(team.getKey()));
		});*/
	}

	public void show(PlayerData data) {
		/*
		if(data.isAllView()) {
			teambossbar.values().forEach(bar -> bar.addPlayer(Bukkit.getPlayer(data.getName())));
			return;
		}

		 */
		playerbossbar.get(data.getName()).addPlayer(Bukkit.getPlayer(data.getName()));
	}
	public void showAll() {
		/*
		if(data.isAllView()) {
			teambossbar.values().forEach(bar -> bar.addPlayer(Bukkit.getPlayer(data.getName())));
			return;
		}

		 */
		for(Map.Entry<String,BossBar> entry:playerbossbar.entrySet()){
			entry.getValue().addPlayer(Bukkit.getPlayer(entry.getKey()));
		}
	}
	
	public void hide(String playerName) {
		playerbossbar.get(playerName).removeAll();
	}
	
	public void removeAllPlayer() {
		for(Map.Entry<String,BossBar> entry:playerbossbar.entrySet()){
			entry.getValue().removeAll();
		}

	}
	
	public void resetBossBar() {
		for(Map.Entry<String,BossBar> entry:playerbossbar.entrySet()){
			entry.getValue().setColor(BarColor.GREEN);
			entry.getValue().setProgress(0.0);
		}
	}
	/*
	private String updateisLifeBar_Allteam(int myteam) {
		String text = "";
		for(int team = 1; team <= data.getMaximumTeamNum(); team++) {
			if(team == myteam)
				continue;
			text += (text.equalsIgnoreCase("") ? "" : " ")+updateisLifeBar(team);
		}
		return text;
	}

	 */
	
	private String updateisLifeBar(int team) {
		/*
		String players = "";
		List<PlayerData> playerDatas = DataStore.getArenaPlayersList(data.getName()).stream().filter(pd -> pd.getTeamid() == team).collect(Collectors.toList());
		for(int playerNum = 1; playerNum <= data.getMaximumPlayerNum(); playerNum++) {
			if(playerDatas.size() < playerNum) {
				players += ChatColor.DARK_GRAY + "❙";
				continue;
			}
			PlayerData playerData = playerDatas.get(playerNum-1);
			players += (playerData.isDead() ? ChatColor.DARK_GRAY : data.getSplatColor(team).getChatColor()) + "❙";
		}
		return players + ChatColor.RESET;

		 */
		return "";
	}
	
	
	/*
	 * メモ
	 * 
	 * 概要 : 呼び出されたらチームの全員のPlayerData取って、isDeadがtrueだったらアイコンを暗転
	 * MAX人数居ない時の為にintのforで回してるけど、ここからどうやってPlayerData取り出そうかっていう
	 * 
	 * 提案
	 * ・リストのサイズを固定長にする
	 * ・別でリスト回して、そこからPlayerDataのポジションIDを取り出して比較してやる
	 * 
	 * リストのサイズ固定長にすると、add removeが使えなくなる
	 *   ↑
	 * DataStore#getArenaPlayersListの戻り値を固定長にするだけだから多分可変はしない・・・はず
	 * それだったらそこの戻り値を固定長にするって方面で問題は無さげ
	 * 
	 * ArenaPlayersListだとステージ全体のプレイヤー一覧取られるの忘れてた
	 *   ↑
	 * フィルター掛ければいいや
	 */
}
