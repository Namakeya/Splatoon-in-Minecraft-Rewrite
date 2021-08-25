package jp.kotmw.splatoon.commands;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SettingCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> playerlist= new ArrayList<String>();
        for(Player player : Bukkit.getOnlinePlayers()){
            playerlist.add(player.getName());
        }

        List<String> generalcommand=new ArrayList();
        generalcommand.add("setlobby");
        generalcommand.add("start");
        generalcommand.add("endbattle");
        generalcommand.add("allview");
        generalcommand.add("configreload");
        generalcommand.add("addweapon");

        List<String> roomcommand=new ArrayList();
        roomcommand.add("setroom");
        roomcommand.add("removeroom");
        roomcommand.add("addarena");

        List<String> arenacommand=new ArrayList();
        arenacommand.add("removearena");
        arenacommand.add("setarena");
        arenacommand.add("setarea");
        arenacommand.add("finish");
        arenacommand.add("setspawn");
        arenacommand.add("editmode");
        arenacommand.add("rollback");

        //System.out.println(args.length);

        if(args.length==1){
            List<String> list= new ArrayList<String>(generalcommand);
            list.addAll(roomcommand);
            list.addAll(arenacommand);
            return list;
        }else if(args.length==2){
            //System.out.println(args[0]);
            if(roomcommand.contains(args[0])){
                List<String> list= new ArrayList<String>();
                for(WaitRoomData room : DataStore.getRoomList()){
                    list.add(room.getName());
                }
                return list;
            }else if(arenacommand.contains(args[0])){
                List<String> list= new ArrayList<String>();
                for(ArenaData arena : DataStore.getArenaList()){
                    list.add(arena.getName());
                }
                return list;
            }else{
                return playerlist;
            }
        }

        return playerlist;
    }
}
