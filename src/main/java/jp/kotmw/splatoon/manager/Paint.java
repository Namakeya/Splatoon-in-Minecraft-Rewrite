package jp.kotmw.splatoon.manager;

import jp.kotmw.splatoon.maingame.BattleClass;
import jp.kotmw.splatoon.maingame.SplatZones;
import jp.kotmw.splatoon.specialweapon.SpecialWeapon;
import jp.kotmw.splatoon.util.MaterialUtil;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.Wool;

import jp.kotmw.splatoon.event.BlockPaintEvent;
import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.PlayerData;
import jp.kotmw.splatoon.maingame.MainGame;
import jp.kotmw.splatoon.util.SplatColor;

import java.util.Map;

public class Paint {

	public static boolean PaintWool(PlayerData data, Block block) {
		return PaintWool( data,  block, true);
	}
	@SuppressWarnings("deprecation")
	public static boolean PaintWool(PlayerData data, Block block,boolean countAsSPP) {
		ArenaData arena = DataStore.getArenaData(data.getArena());

		if(PaintWool(data,arena,data.getTeamid(),block)) {
			//カーペットのみカウントする。将来的に「上が開いているブロック」という基準でカウントする可能性もあり sesamugi
			if (MaterialUtil.isCarpet(block.getType())) {

				addScore(data, countAsSPP);//0じゃない場合は敵チームのを上書きしたという事だからボーナスに追加

			}


			return true;
		}else{
			return false;
		}

	}

	/**主にエリア塗りなどプレイヤーの介さない塗り用
	 * @param data はnullable*/
	public static boolean PaintWool(PlayerData data,ArenaData arena,int team,Block block){
		if(block == null || block.getType() == Material.AIR)
			return false;
		if(!isCanPaintBlock(block))
			return false;
		if(!isCanPaintColor(DyeColor.getByWoolData((byte) SplatColorManager.getColorID(block))))
			return false;
		if(SplatColorManager.getColorID(block) == arena.getSplatColor(team).getColorID())
			return false;
		//カーペットのみカウントする。将来的に「上が開いているブロック」という基準でカウントする可能性もあり sesamugi
		if(MaterialUtil.isCarpet(block.getType())) {
			int bonus = 0;//塗ったブロックが敵チームのカラーだったら、この変数に敵チームの番号が入る
			for (int team_ = 1; team_ <= arena.getMaximumTeamNum(); team_++)
				if ((team != team_) && SplatColorManager.getColorID(block) == arena.getSplatColor(team_).getColorID()) {
					bonus = team_;
					break;
				}

			arena.addTeamScore(team, bonus);//TODO ここ
			if(arena.getBattleClass() instanceof SplatZones
					&& ((SplatZones)arena.getBattleClass()).isInArea(block.getLocation())){
				arena.addTeamAreaOccupation(team, bonus);
			}
		}
		BlockPaintEvent event = new BlockPaintEvent(block, data, arena);
		Bukkit.getPluginManager().callEvent(event);
		addRollBack(arena, block);

		ColorChange(block, arena.getSplatColor(team));
		return true;
	}
	
	/**
	 * 指定したブロックの色を指定した色に変更する
	 *
	 * @param block 色を変更するブロック
	 * @param color 色
	 *
	 */
	@SuppressWarnings("deprecation")
	public static void ColorChange(Block block, SplatColor color) {
		//System.out.println("change to "+color.name()+" at "+block.getLocation());
		if(block == null)
			return;
		if(MaterialUtil.isWool(block.getType())){
			//BlockState state = block.getState();
			block.setBlockData(MaterialUtil.fromColorIdToWool(color.getDyeColor().getWoolData()).createBlockData());
			//block.getState().update();
			return;
		}else if(MaterialUtil.isCarpet(block.getType())){
			//BlockState state = block.getState();
			block.setBlockData(MaterialUtil.fromColorIdToCarpet(color.getDyeColor().getWoolData()).createBlockData());
			//block.getState().update();
			return;
		}else{
			return;
		}
		//ここでなぜsyncを使っているか不明だが、これが原因で、1tick中に同じブロックを同じ色で塗ろうとしたときに
		//PaintWoolメソッド内でのブロックの色の確認と色を塗るタイミングにずれが生じ
		//塗った場所が二重にカウントされることがあったので廃止
		//特にローラーで顕著に発生 sesamugi
		/*
		else MainGame.sync(() -> {

			if(MaterialUtil.isWool(block.getType())){
				//BlockState state = block.getState();
				block.setBlockData(MaterialUtil.fromColorIdToWool(color.getDyeColor().getWoolData()).createBlockData());
				//block.getState().update();
				return;
			}else if(MaterialUtil.isCarpet(block.getType())){
				//BlockState state = block.getState();
				block.setBlockData(MaterialUtil.fromColorIdToCarpet(color.getDyeColor().getWoolData()).createBlockData());
				//block.getState().update();
				return;
			}else{
				return;
			}

			//block.getState().setData(color.getDyeColor().getWoolData());
		}); */
	}

	public static void addRollBack(ArenaData data, Block block) {
		if(!data.getRollbackblocks().containsKey(block.getLocation())){
			data.getRollbackblocks().put(block.getLocation(),block.getBlockData());
		}
		/*
		for(BlockState state : data.getRollbackblocks()) {
			Location l = block.getLocation();
			if(state.getLocation().equals(l)) return;
		}
		data.addRollBackBlock(block.getState());*/
	}

	private static void addScore(PlayerData data, boolean countAsSPP) {
		int score = data.getScore();
		if(SpecialWeapon.SPECIALENABLED && SpecialWeapon.SPPENABLED && countAsSPP) {
			data.setSpecialPoint(data.getSpecialPoint()+1);
		}
		data.setScore(score + 1);
	}
	public static int SpherePaint(Location center, double radius, PlayerData data) {
		return SpherePaint( center,  radius,  data,true);
	}

	public static int SpherePaint(Location center, double radius, PlayerData data,boolean countAsSPP) {
		double center_X = center.getX();
		double center_Y = center.getY();
		double center_Z = center.getZ();

		//boolean hollow = false;
		int painted=0;
		if(radius>30)return 0;
		for(double x = center_X - radius; x <= center_X + radius +0.1 ;x++)
			for(double y = center_Y - radius; y <= center_Y + radius +0.1 ;y++)
				for(double z = center_Z - radius; z <= center_Z + radius +0.1 ;z++) {
					double distance = ((center_X - x)*(center_X - x)) + ((center_Y - y)*(center_Y - y)) + ((center_Z - z)*(center_Z - z));
					if(distance < (radius*radius)) {
						Location l = new Location(center.getWorld(), x, y, z);
						if(Paint.PaintWool(data, l.getBlock(),countAsSPP)){
							painted++;
						}
					}
				}
		return painted;
	}

	public static int UnderCylinderPaint(Location center, double radius,double height, PlayerData data) {
		return UnderCylinderPaint( center,  radius, height,data,true);
	}

	public static int UnderCylinderPaint(Location center, double radius,double height, PlayerData data,boolean countAsSPP) {
		double center_X = center.getX();
		double center_Y = center.getY();
		double center_Z = center.getZ();

		//boolean hollow = false;
		int painted=0;
		if(radius>30)return 0;
		for(double x = center_X - radius; x <= center_X + radius +0.1 ;x++)
			for(double y = center_Y - height; y <= center_Y +0.1 ;y++)
				for(double z = center_Z - radius; z <= center_Z + radius +0.1 ;z++) {
					double distance = ((center_X - x)*(center_X - x)) + ((center_Z - z)*(center_Z - z));
					if(distance < (radius*radius)) {
						Location l = new Location(center.getWorld(), x, y, z);
						if(Paint.PaintWool(data, l.getBlock(),countAsSPP)){
							painted++;
						}
					}
				}
		return painted;
	}

	public static int UnderPaint(Location center, double radius, PlayerData data,boolean countAsSPP) {
		double center_X = center.getX();
		double center_Y = center.getY();
		double center_Z = center.getZ();

		//boolean hollow = false;
		int painted=0;
		//System.out.println(radius);
		if(radius>30)return 0;
		for(double y_ = - radius; y_ <= 0 ;y_++)
			for(double x = center_X - radius - y_; x <= center_X + radius +0.1 + y_;x++)
				for(double z = center_Z - radius - y_; z <= center_Z + radius +0.1 + y_ ;z++) {
					double y=center_Y+y_;

					Location l = new Location(center.getWorld(), x, y, z);
					if(Paint.PaintWool(data, l.getBlock(),countAsSPP)){
						painted++;
					}

				}
		return painted;
	}


	
	/* block.getBlock().setType(Material)でやるか
	 * block.update(true)にするかを暫く検討
	 * 
	 * block.setType(Material)と
	 * update()じゃ内部データとその場所にあるブロックのデータが食い違い、ロールバックに失敗する。
	 */
	public static void RollBack(ArenaData data) {
		for(Map.Entry<Location, BlockData> entry: data.getRollbackblocks().entrySet()) {
			entry.getKey().getBlock().setBlockData(entry.getValue());
		}
	}

	public static boolean isCanPaintBlock(Block block) {
		return MaterialUtil.isPaintable(block.getType());
	}
	
	private static boolean isCanPaintColor(DyeColor color) {
		return DataStore.getConfig().getCanpaintcolors().contains(color.toString());
	}
}
