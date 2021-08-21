package jp.kotmw.splatoon.commands;

import jp.kotmw.splatoon.gamedatas.ArenaData;
import jp.kotmw.splatoon.gamedatas.DataStore;
import jp.kotmw.splatoon.gamedatas.WaitRoomData;
import jp.kotmw.splatoon.gamedatas.WeaponData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlayerCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        List<String> playerlist= new ArrayList<String>();
        for(Player player : Bukkit.getOnlinePlayers()){
            playerlist.add(player.getName());
        }

        List<String> generalcommand=new ArrayList();
        generalcommand.add("leave");
        generalcommand.add("roomlist");
        generalcommand.add("arenalist");
        generalcommand.add("rank");
        generalcommand.add("getweapon");
        generalcommand.add("setweapon");

        List<String> roomcommand=new ArrayList();
        roomcommand.add("join");

        List<String> arenacommand=new ArrayList();

        //System.out.println(args.length);

        if(args.length==1){
            List<String> list= new ArrayList<String>(generalcommand);
            list.addAll(roomcommand);
            list.addAll(arenacommand);
            return list;
        }else if(args.length==2){
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
        }else if(args.length==3){
            if(args[0].equalsIgnoreCase("setweapon")) {
                List<String> list = new ArrayList<String>();
                for (WeaponData weapon : DataStore.getWeaponList()) {
                    list.add(weapon.getName());
                }
            }
        }

        return playerlist;
    }
}
