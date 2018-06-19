package com.example.kazuya.gachaapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Kazuya on 2017/08/25.
 */

public class AccountInfo {

    //region 定数
    public static String PREF_NAME_ACCOUNT = "account";
    public static  String USER_KEY = "users";
    public static String PLAYER_KEY = "players";

    public static String DEFAULT_GROUP = "default";
    public static String DEFAULT_PLAYER = "Player";

    public static String PREF_NAME_GAME = "gameinfo";

    public static String GAME_KEY = "game";
    public static String COUNT_KEY = "count";
    public static String SUM_KEY = "summoney";
    public static String PAY_KEY = "pay";
    public static String ROUND_KEY = "roundpay";
    //endregion

    //region カンマ区切りの文字列からStringのListを作成する
    public List<String> getListToString(String str){

        List<String> list ;

        list = new ArrayList<>(Arrays.asList(str.split(",")));

        return list;
    }
    //endregion

    //region カンマ区切りの文字列からInt型のリストを作成する
    public List<Integer> getListToInteger(String str){
        List<Integer> list = new ArrayList<Integer>();

        String[] strlist = str.split(",");

        for(String s: strlist){
            try{
                list.add(Integer.parseInt(s));
            }catch (Exception e){

            }
        }

        return list;
    }
    //endregion

    //region StringのListからカンマ区切りの文字列を作成
    public String createStringByStringList(List<String> list){

        if(list.size() == 0){
            return "";
        }

        String str = "";
        for(String s:list){
            if(str != ""){
                str += ",";
            }
            str += s;
        }
        return str;
    }
    //endregion

    //region Intの配列からカンマ区切りの文字列を作成する
    public String creatStringByIntegerList(List<Integer> list){

        if(list.size() == 0){
            return "";
        }

        String str = "";
        for(Integer num:list){
            if(str != ""){
                str += ",";
            }
            str += String.valueOf(num);
        }
        return str;
    }
    //endregion

    //region keyにプレイヤーキーを追加する
    public String addPlayerKey(String key){
        if(key.equals("")) return "";

        return key + "_" + PLAYER_KEY;
    }
    //endregion

    //region ゲーム情報保存のグループキーを作成
    public String creatGameInfoKeyByGroup(String group){
        if(group.equals("")) return "";

        return group + "_" + GAME_KEY;
    }
    //endregion

    //region グループキーから保存キーを作成
    public String createKeyByGroupKey(String group,String addkey){
        return group + "_" + addkey;
    }
    //endregion

    //region グループキーとプレイヤー名からキーを作成
    public String creatKeyByGroupKeyWithPlayer(String groupkey,String player, String addkey){
        return groupkey + "_" + player + "_" + addkey;
    }
    //endregion

    //region 未登録チェック
    public boolean checkRegistInfo(List<String> checkList,String registStr){

        boolean result = false;

        for(String readStr:checkList){
            if(readStr.equals(registStr)){
                result = true;
            }
        }
        return result;
    }
    //endregion
}
