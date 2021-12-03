package jp.kotmw.splatoon.maingame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.kotmw.splatoon.Main;
import jp.kotmw.splatoon.maingame.threads.ResultRunnable;
import jp.kotmw.splatoon.util.MaterialUtil;
import org.apache.http.annotation.Obsolete;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import jp.kotmw.splatoon.event.ZoneChangeEvent;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.manager.Paint;
import jp.kotmw.splatoon.manager.SplatColorManager;
import jp.kotmw.splatoon.manager.TeamCountManager;
import org.bukkit.scheduler.BukkitRunnable;

public class SplatZones extends BattleClass {

	public SplatZones(ArenaData data) {
		super(data);
	}

	/**
	 * BattleRunnable.java #L125
	 * で呼び出されます
	 */


	public double getTeamScore(int team) {
		return 100-data.getCount(team).getcount();

	}

	public double getTotalTeamScore() {
		return getTeamScore(1)+getTeamScore(2)+1;
	}


	public void showZone(int colorid) {
		//System.out.println("showZone");
		List<ArmorStand> areastands = new ArrayList<ArmorStand>();
		int x1 = (int)data.getAreaPosition1().getX();
		int y1 = (int)data.getAreaPosition1().getY();
		int z1 = (int)data.getAreaPosition1().getZ();
		int x2 = (int)data.getAreaPosition2().getX();
		int y2 = (int)data.getAreaPosition2().getY();
		int z2 = (int)data.getAreaPosition2().getZ();
		int i=0;
		World w=Bukkit.getWorld(data.getWorld());
		//World w=Bukkit.getPlayer(DataStore.getArenaPlayersList(data.getName()).get(0).getName()).getWorld();
		for(int x = x2; x <= x1; x++) {
			for(int y = y1+2;y <= y1+20;y+=5) {
				for(int z = z2; z <= z1; z++) {
					if((x == x1 || x == x2) || (z == z1 || z == z2)) {

						Location l = new Location(w, x+0.5, y, z+0.5);
						ArmorStand stand = (ArmorStand) w.spawnEntity(l, EntityType.ARMOR_STAND);
						ItemStack item = new ItemStack(MaterialUtil.fromColorIdToStainedGlass(colorid), 1);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName("SplatPluginItem ["+data.getName()+"]");
						item.setItemMeta(meta);
						stand.setHelmet(item);
						stand.setVisible(false);
						stand.setGravity(false);
						stand.setMarker(true);
						stand.setCustomName("SplatPluginItem ["+data.getName()+"]");
						areastands.add(stand);
						i++;
					}
				}
			}
		}
		//System.out.println("i : "+i);
		//System.out.println("areastands : "+areastands.size());
		data.setTotalareablock(getTotalArea(Bukkit.getWorld(data.getWorld()), x1, x2, y1, y2, z1, z2));
		data.setAreastands(areastands);
	}

	public boolean isInArea(Location location){
		int x1 = (int)data.getAreaPosition1().getX();
		int y1 = (int)data.getAreaPosition1().getY();
		int z1 = (int)data.getAreaPosition1().getZ();
		int x2 = (int)data.getAreaPosition2().getX();
		int y2 = (int)data.getAreaPosition2().getY();
		int z2 = (int)data.getAreaPosition2().getZ();
		return x2<=location.getX() && location.getX()<=x1
				&& y2<=location.getY() && location.getY()<=y1
				&& z2<=location.getZ() && location.getZ()<=z1;
	}

	public int getOpposite(int team){
		return team==1?2:1;
	}

	public void checkArea() {
		/*
		int area[]=new int[data.getMaximumTeamNum()];
		int x1 = (int)data.getAreaPosition1().getX();

		int z1 = (int)data.getAreaPosition1().getZ();
		int x2 = (int)data.getAreaPosition2().getX();
		int y2 = (int)data.getAreaPosition2().getY();
		int z2 = (int)data.getAreaPosition2().getZ();
		for(int x = x2; x <= x1; x++) {
			for(int z = z2; z <= z1; z++) {
				Block block = Bukkit.getWorld(data.getWorld()).getBlockAt(x, y2, z);
				Block aboveBlock = Bukkit.getWorld(data.getWorld()).getBlockAt(x, y2+1, z);
				if(block.getType() != Material.AIR
						&& aboveBlock.getType() == Material.AIR) {
					int colorbyte = SplatColorManager.getColorID(block);
					for(int i=1;i<=data.getMaximumTeamNum();i++){
						if(data.getSplatColor(i).getColorID() == colorbyte) {
							area[i - 1]++;
						}
					}

				}
			}
		}

		 */
		boolean areaneutral = true;
		for(int i=1;i<=data.getMaximumTeamNum();i++) {
			if (data.getCount(i).ishavearea()) {
				areaneutral=false;
				for (PlayerData pd : DataStore.getArenaPlayersList(data.getName())
				) {
					if (pd.getTeamid() != i){
						pd.setSpecialPoint(pd.getSpecialPoint()+5);
					}
				}
			}
		}
		if(areaneutral){
			for (PlayerData pd : DataStore.getArenaPlayersList(data.getName())
			) {
				if(data.getCount(pd.getTeamid()).getcount() > data.getCount(getOpposite(pd.getTeamid())).getcount())
				pd.setSpecialPoint(pd.getSpecialPoint()+2);

			}
		}
		int totalareablock = data.getTotalareablock();
		if((totalareablock*0.5 > data.getTeamAreaOccupation(1)) && (totalareablock*0.5 > data.getTeamAreaOccupation(2)))
			return; //開始当初でteam1,team2が両方ともぜんぜん塗ってない時の処理

		for(int i=1;i<=data.getMaximumTeamNum();i++){
			//System.out.println("team "+i+" 's score:"+data.getTeamAreaOccupation(i));
			if(data.getCount(i).ishavearea()) {//片方が既にエリアを確保している場合
				if(totalareablock*0.5 < data.getTeamAreaOccupation(getOpposite(i))) {//相手チームが5割以上になった場合
					//カウントストップ
					for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
						if(player.getTeamid() == i){
							MainGame.sendTitle(player, 0, 5, 0, " "
									, data.getSplatColor(getOpposite(i)).getChatColor()+"カウントストップされた!");
							Player pe=Bukkit.getPlayer(player.getName());
							pe.playSound(pe.getLocation(),Sound.BLOCK_CHEST_LOCKED,SoundCategory.PLAYERS,1f,1.2f);
						}else{
							MainGame.sendTitle(player, 0, 5, 0, " "
									, data.getSplatColor(getOpposite(i)).getChatColor()+"カウントストップした！");
							Player pe=Bukkit.getPlayer(player.getName());
							pe.playSound(pe.getLocation(),Sound.BLOCK_CHEST_LOCKED,SoundCategory.PLAYERS,1f,1.2f);
						}
					}
					data.getCount(i).sethavearea(false);
					return;
				}
				data.getCount(i).updatecount();
				data.getScoreboard().updateCount(i);
				return;
			}else if(totalareablock*0.75 < data.getTeamAreaOccupation(i)) {//どちらも確保していない状況から片方が確保した場合
				System.out.println("Team1: "+data.getTeamAreaOccupation(1)+"      "+"Team2: "+data.getTeamAreaOccupation(2));
				data.getCount(i).sethavearea(true);
				int teambefore=data.getCount(getOpposite(i)).setpenalty();
				data.getScoreboard().updateCount(getOpposite(i));
				ZoneChangeEvent event = new ZoneChangeEvent();
				Bukkit.getPluginManager().callEvent(event);
				EnsureArea(i);
				for(PlayerData player : DataStore.getArenaPlayersList(data.getName())) {
					if(player.getTeamid() == i){
						MainGame.sendTitle(player, 0, 5, 0, " "
								, data.getSplatColor(i).getChatColor()+"ガチエリア確保した!");
						Player pe=Bukkit.getPlayer(player.getName());
						pe.playSound(pe.getLocation(),Sound.BLOCK_ANVIL_USE,SoundCategory.PLAYERS,1f,1.2f);
					}else{
						MainGame.sendTitle(player, 0, 5, 0, " "
								, data.getSplatColor(i).getChatColor()+"ガチエリア確保された!");
						Player pe=Bukkit.getPlayer(player.getName());
						pe.playSound(pe.getLocation(),Sound.ENTITY_ITEM_BREAK,SoundCategory.PLAYERS,0.7f,1f);
					}
				}
			}
		}


	}

	public void EnsureArea(int ensureteam) {
		int x1 = (int)data.getAreaPosition1().getX();
		int z1 = (int)data.getAreaPosition1().getZ();
		int x2 = (int)data.getAreaPosition2().getX();
		int z2 = (int)data.getAreaPosition2().getZ();
		int y1 = (int)data.getAreaPosition1().getY();
		int y2 = (int)data.getAreaPosition2().getY();
		for(int x = x2; x <= x1; x++) {
			for (int z = z2; z <= z1; z++) {
				for (int y = y2; y <= y1; y++) {

					Block block = Bukkit.getWorld(data.getWorld()).getBlockAt(x, y, z);
					if(MaterialUtil.isCarpet(block.getType())) {
						Paint.PaintWool(null, data, ensureteam, block);
					}
				}
			}
		}
		for(ArmorStand stand:data.getAreastands()){
			ItemStack item=new ItemStack(MaterialUtil.fromColorIdToStainedGlass(data.getSplatColor(ensureteam).getColorID()),1);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("SplatPluginItem ["+data.getName()+"]");
			item.setItemMeta(meta);
			//System.out.println(stand.getLocation());
			//System.out.println(item.getType());
			stand.setHelmet(item);
			//System.out.println(stand.getHelmet().getType());


		}
		//showZone(data.getSplatColor(ensureteam).getColorID());

	}

	@Override
	public boolean doExtra(int tick){
		if(tick>=20*(-30)){
			//30秒以内... 勝っている方がエリアを取っていない場合延長
		for(int i=1;i<=data.getMaximumTeamNum();i++) {
			if (data.getCount(i).getcount() >= data.getCount(getOpposite(i)).getcount()
					&& !data.getCount(getOpposite(i)).ishavearea()) {
				return true;
			}
		}
		}else{
			//30秒以降... 負けている方がエリアを取っている場合延長
			for(int i=1;i<=data.getMaximumTeamNum();i++) {
				if (data.getCount(i).getcount() >= data.getCount(getOpposite(i)).getcount()
						&& data.getCount(i).ishavearea()) {
					return true;
				}
			}
		}
		return false;


	}

	public static void clearAreaStand(ArenaData data) {
		//System.out.println("SplatPluginItem ["+data.getName()+"]");
		for(ArmorStand stand : data.getAreastands()) {
			//System.out.println("data "+stand.getHelmet().getType());
			stand.remove();
		}
		for(Entity entity : Bukkit.getWorld(data.getWorld()).getEntities()) {
			if(entity.getType() != EntityType.ARMOR_STAND)
				continue;
			ItemMeta meta=((ArmorStand)entity).getHelmet().getItemMeta();
			//System.out.println("name : "+entity.getName());
			if((meta!=null && meta.getDisplayName().equalsIgnoreCase("SplatPluginItem ["+data.getName()+"]"))
			||entity.getName().equalsIgnoreCase("SplatPluginItem ["+data.getName()+"]")){
				//System.out.println("name "+((ArmorStand) entity).getHelmet().getType());
				entity.remove();
			}

		}
	}
}
