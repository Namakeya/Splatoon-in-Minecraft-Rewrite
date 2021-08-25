package jp.kotmw.splatoon.gamedatas;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import jp.kotmw.splatoon.filedatas.PlayerFiles;
import jp.kotmw.splatoon.gamedatas.DataStore.RankingPattern;

public class PlayerStatusData extends PlayerFiles {

	private String uuid;
	private String name;
	private int win;
	private int lose;
	private boolean finalwin;
	private int winstreak;
	private int maxwinstreak;
	private int rank;
	private double exp;
	private double totalexp;
	private int totalpaint;
	private List<String> weapons = new ArrayList<String>();

	private String currentWeapon;

	private boolean runToSquid,dropToSquid,sneakToSquid,swapToSub,clickToSub,dropToSub;

	public PlayerStatusData(String uuid, FileConfiguration file) {
		this.uuid = uuid;
		this.name = file.getString("Name");
		this.win = file.getInt("Rate.Win");
		this.lose = file.getInt("Rate.Lose");
		this.finalwin = file.getBoolean("Rate.FinalWin");
		this.winstreak = file.getInt("Rate.WinStreak");
		this.maxwinstreak = file.getInt("Rate.MaxWinStreak");
		this.rank = file.getInt("Status.Rank");
		this.exp = file.getDouble("Status.Exp");
		this.totalexp = file.getDouble("Status.TotalExp");
		this.totalpaint = file.getInt("Status.TotalPaint");
		this.weapons = file.getStringList("Status.Weapons");
		this.currentWeapon=file.getString("Status.CurrentWeapon");
		runToSquid=true;
		dropToSquid=true;
		sneakToSquid=false;
		swapToSub=false;
		clickToSub=true;
		dropToSub=false;
	}

	public boolean isRunToSquid() {
		return runToSquid;
	}

	public void setRunToSquid(boolean runToSquid) {
		this.runToSquid = runToSquid;
	}

	public boolean isDropToSquid() {
		return dropToSquid;
	}

	public void setDropToSquid(boolean dropToSquid) {
		this.dropToSquid = dropToSquid;
	}

	public boolean isSneakToSquid() {
		return sneakToSquid;
	}

	public void setSneakToSquid(boolean sneakToSquid) {
		this.sneakToSquid = sneakToSquid;
	}

	public boolean isSwapToSub() {
		return swapToSub;
	}

	public void setSwapToSub(boolean swapToSub) {
		this.swapToSub = swapToSub;
	}

	public boolean isClickToSub() {
		return clickToSub;
	}

	public void setClickToSub(boolean clickToSub) {
		this.clickToSub = clickToSub;
	}

	public boolean isDropToSub() {
		return dropToSub;
	}

	public void setDropToSub(boolean dropToSub) {
		this.dropToSub = dropToSub;
	}

	public String getCurrentWeapon() {
		return currentWeapon;
	}

	public void setCurrentWeapon(String currentWeapon) {
		this.currentWeapon = currentWeapon;
	}

	public String getUuid() {
		return uuid;
	}
	
	public String getName() {
		return name;
	}

	public int getWin() {
		return win;
	}

	public int getLose() {
		return lose;
	}

	public boolean isFinalwin() {
		return finalwin;
	}

	public int getWinstreak() {
		return winstreak;
	}

	public int getMaxwinstreak() {
		return maxwinstreak;
	}

	public int getRank() {
		return rank;
	}

	public double getExp() {
		return exp;
	}

	public double getTotalexp() {
		return totalexp;
	}
	
	public int getTotalPaint() {
		return totalpaint;
	}

	public List<String> getWeapons() {
		return weapons;
	}
	
	public double getParam(RankingPattern pattern) {
		switch (pattern) {
		case LOSE:
			return lose;
		case MAXWINSTREAK:
			return maxwinstreak;
		case RANK:
			return rank;
		case TOTALPAINT:
			return totalpaint;
		case WIN:
			return win;
		case RATE:
			if((win+lose) == 0)
				return 0.0;
			return ((double)win / (double)(win+lose));
		default:
			break;
		}
		return 0;
	}
	
	public void updateWinnerScore() {
		win++;
		if(finalwin) {
			winstreak++;
			if(winstreak > maxwinstreak)
				maxwinstreak = winstreak;
		}
		finalwin = true;
	}
	
	public void updateLoserScore() {
		lose++;
		finalwin = false;
		winstreak = 0;
	}
	
	public boolean updateScoreExp() {
		PlayerData data = DataStore.getPlayerData(name);
		double score = (data.getScore()/10)+exp;
		if(rank == 20)//一時的にランク20以上は設定しない
			return false;
		totalexp += (data.getScore()/10);
		totalpaint += data.getScore();
		boolean rankup = false;
		while(score >= DataStore.getRankData().getNextRankExp(rank)) {
			rankup = true;
			score -= DataStore.getRankData().getNextRankExp(rank);
			rank++;
		}
		exp = score;
		updateStatusFile(this);
		return rankup;
	}
	
	/**
	 *
	 * @param uuid 武器を追加するプレイヤーのUUID
	 * @param weapon ブキ名
	 *
	 * @return その武器を持っていない場合はtrue<br>
	 * 持っている場合はfalseを返す
	 */
	public boolean addWeapon(String weapon) {
		FileConfiguration file = YamlConfiguration.loadConfiguration(DirFile(filedir, uuid));
		weapons = file.getStringList("Status.Weapons");
		if(weapons.contains(weapon))
			return false;
		weapons.add(weapon);
		setData(DirFile(filedir, uuid), "Status.Weapons", weapons);
		return true;
	}

	/**
	 *
	 * @param uuid 対象プレイヤーのハイフン抜きのUUID
	 * @param weaponname 調べる武器名
	 * @return 既に持っていればtrueが返される
	 */
	public boolean hasHaveWeapon(String weaponname) {
		return weapons.contains(weaponname);
	}
}
