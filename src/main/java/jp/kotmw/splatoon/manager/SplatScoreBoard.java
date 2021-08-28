package jp.kotmw.splatoon.manager;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore.BattleType;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;

public class SplatScoreBoard {

	private Scoreboard scoreboard;
	private ArenaData data;

	private Map<String,Score> scoreMap=new HashMap<>();

	
	public SplatScoreBoard(ArenaData data) {
		this.data = data;
		scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective obj = scoreboard.registerNewObjective(data.getName(), "dummy");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		obj.setDisplayName(MainGame.Prefix);
		for(int teamnum = 1; teamnum <= data.getMaximumTeamNum(); teamnum++) {
			Team team = scoreboard.registerNewTeam("SplatTeam"+teamnum);
			team.setPrefix(data.getSplatColor(teamnum).getChatColor().toString());
			team.setSuffix(ChatColor.RESET.toString());
			team.setAllowFriendlyFire(false);
			team.setCanSeeFriendlyInvisibles(false);
			team.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.FOR_OTHER_TEAMS);
		}
	}
	
	public void resetScoreboard() {
		for(int teamnum = 1; teamnum <= data.getMaximumTeamNum(); teamnum++) {
			scoreboard.getTeam("SplatTeam"+teamnum).setPrefix(data.getSplatColor(teamnum).getChatColor().toString());
		}
		Objective objective = scoreboard.getObjective(data.getName());
		Score timescore= scoreMap.get("time");
		timescore.setScore(0);
		scoreboard.resetScores(timescore.getEntry());
	}

	public void DefaultScoreBoard(BattleType type) {
		Objective obj = scoreboard.getObjective(data.getName());
		Map<String,String> board = new LinkedHashMap<>();//順序を維持するためLinkedHashMapでなければならない
		board.put("TIMELEFT",ChatColor.GREEN+"-Time left-");
		board.put("time",conversionTime(MainGame.getTime(type)));
		board.put("NOLINE1",ChatColor.RESET.toString());
		board.put("stage","Stage : "+data.getName());
		if(type == BattleType.Splat_Zones) {
			board.put("NOLINE2",ChatColor.RESET.toString()+ChatColor.RESET.toString());
			board.put("COUNT",ChatColor.YELLOW+"-Count-");
			board.put("team1count",data.getSplatColor(1).getChatColor()+"Team1 : "+ChatColor.WHITE+100);
			board.put("team2count",data.getSplatColor(2).getChatColor()+"Team2 : "+ChatColor.WHITE+100);
		}
		int i = board.size() -1;
		for(Map.Entry<String,String> boardtxt : board.entrySet()) {
			Score score = obj.getScore(boardtxt.getValue());
			score.setScore(i);
			scoreMap.put(boardtxt.getKey(),score);
			i--;
		}
	}

	public void updateScore(String name,String value){
		Objective obj = scoreboard.getObjective(data.getName());
		Score score= scoreMap.get(name);
		//int scorevalue = obj.getScore(ChatColor.GREEN+"-Time left-").getScore()-1;
		assert(score!=null);
		int scorevalue=score.getScore();
		score.setScore(0);
		//System.out.println(score.getEntry());
		scoreboard.resetScores(score.getEntry());
		score= obj.getScore(value);
		score.setScore(scorevalue);
		scoreMap.put(name,score);

	}

	private String conversionTime(int tick) {
		if(tick<=0){
			return ChatColor.RED.toString()+" 延長中! ";
		}
		int second = tick%60;
		int minut = (tick/60)%60;
		if(String.valueOf(second).length() == 2)
			return ChatColor.GOLD.toString()+minut+" : "+second;
		return ChatColor.GOLD.toString()+minut+" : 0"+second;
	}

	public void changeTime(int tick) {
		if(tick%20 != 0)
			tick -= tick%20;
		updateScore("time",conversionTime(tick/20));

	}

	public void updateCount(int team) {
		Objective obj = scoreboard.getObjective(data.getName());
		//int team1value = obj.getScore(ChatColor.YELLOW+"-Count-").getScore()-1;
		TeamCountManager manager = data.getCount(team);
		if(team==1){
			updateScore("team1count",getCountText(team,manager));
		}else if(team==2){
			updateScore("team2count",getCountText(team,manager));
		}else{

		}

	}
/*
	public void updatePenalty(int team, int beforepenalty) {
		Objective obj = scoreboard.getObjective(data.getName());
		int team1value = obj.getScore(ChatColor.YELLOW+"-Count-").getScore()-1;
		TeamCountManager manager = data.getCount(team);

		if(manager.getpenalty() < 1)
			return;
		String beforetext = data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount())+" +"+beforepenalty;
		if(beforepenalty < 1)
			beforetext = getText(team, manager, false).get(0);
		String aftertext = getText(team, manager, false).get(1);
		Score team1 = obj.getScore(beforetext);
		team1.setScore(0);
		scoreboard.resetScores(beforetext);
		team1 = obj.getScore(aftertext);
		team1.setScore(team1value);
	}
	*/
	public Scoreboard getScoreboard() {
		return scoreboard;
	}

	private String getCountText(int team,TeamCountManager manager) {
		if(manager.getpenalty()>0){
			return data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount())+" +"+(manager.getpenalty());
		}else{
			return data.getSplatColor(team).getChatColor()+"Team"+team+" : "+ChatColor.WHITE+(manager.getcount());
		}
	}
	
	public void setTeam(PlayerData data) {
		Team team = scoreboard.getTeam("SplatTeam"+data.getTeamid());
		team.addEntry(data.getName());
	}

	public void showBoard(PlayerData data) {
		Bukkit.getPlayer(data.getName()).setScoreboard(scoreboard);
	}

	public void hideBoard(PlayerData data) {
		Bukkit.getPlayer(data.getName()).setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		scoreboard.getTeam("SplatTeam"+data.getTeamid()).removeEntry(data.getName());
	}
}
