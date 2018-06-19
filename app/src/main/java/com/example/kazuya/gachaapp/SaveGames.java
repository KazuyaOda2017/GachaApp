package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.content.SharedPreferences;

import java.util.List;

/**
 * Created by Kazuya on 2017/08/27.
 */

public class SaveGames extends Activity{

    //region フィールド
    AccountInfo accountInfo;
    SharedPreferences preferences;
    SharedPreferences.Editor editer;

    SharedPreferences groupPre;
    private List<String> playerlist;
    //endregion

    //region コンストラクタ
    public SaveGames(){
        accountInfo = new AccountInfo();

    }
    //endregion


    //region ゲーム情報を保存する
    public void saveGameInfo(GameInfo gameInfo){


        //プリファレンスの取得
        try {
            preferences = getSharedPreferences(AccountInfo.PREF_NAME_ACCOUNT,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
            String users = preferences.getString(AccountInfo.USER_KEY,"");

            groupPre = getSharedPreferences(AccountInfo.PREF_NAME_GAME,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
            editer = groupPre.edit();

        }catch (Exception e){

        }


        String players = "";
        //グループのプレイヤー情報を取得
        try{
            String playerkey = accountInfo.addPlayerKey(gameInfo.getGroupName());
           players  = preferences.getString(playerkey,"");

        }catch (Exception e){

        }
        playerlist = accountInfo.getListToString(players);

        //グループキーを作成
        String groupKey = accountInfo.creatGameInfoKeyByGroup(gameInfo.getGroupName());

        //プレイ回数を取得
        String key_count = accountInfo.createKeyByGroupKey(groupKey,AccountInfo.COUNT_KEY);
        int playCount = groupPre.getInt(key_count,0);

        //会計金額を保存
        String key = accountInfo.createKeyByGroupKey(groupKey,AccountInfo.SUM_KEY);
        String sumInfo = groupPre.getString(key,"");
        if(!sumInfo.equals("")){
            sumInfo += ",";
        }
        sumInfo += String.valueOf(gameInfo.getSumMoney());
        editer.putInt(key,gameInfo.getSumMoney());
        editer.commit();

        //各プレイヤーの支払金額を保存


        //保存されているプレイヤー分ループ処理
        for(String playerName:playerlist){
            //初期化
            String saveInfo_pay = "";
            String saveInfo_round = "";
            String key_pay = accountInfo.creatKeyByGroupKeyWithPlayer(groupKey,playerName,AccountInfo.PAY_KEY);
            String key_round = accountInfo.creatKeyByGroupKeyWithPlayer(groupKey,playerName,AccountInfo.ROUND_KEY);

            //初回じゃない場合
            if(playCount != 0){
                //プレイヤーの保存情報を取得
                saveInfo_pay = groupPre.getString(key_pay,"");
                saveInfo_round = groupPre.getString(key_round,"");
                //情報が無ければプレイ数分情報を足す
                if(saveInfo_pay.equals("")){
                    for(int i = 0; i < playCount; i++){
                        if(!saveInfo_pay.equals("")){
                            saveInfo_pay += ",";
                            saveInfo_round += ",";
                        }
                        saveInfo_pay += "0";
                        saveInfo_round += "0";
                    }
                }
            }

            //ゲームに参加してた場合は結果を、参加していなければ０を追加保存する
            boolean IsExist = false;
            for(PlaylerInfo pInfo:gameInfo.getPlayerInfoList()){
               if(pInfo.getPlayerName().equals(playerName)){
                   //ゲーム情報を追加
                   if(!saveInfo_pay.equals("")){
                       saveInfo_pay += ",";
                       saveInfo_round += ",";
                   }
                   saveInfo_pay += String.valueOf(pInfo.getPaymentMoney());
                   saveInfo_round += String.valueOf(pInfo.getRoundDownPayMoney());
                   IsExist = true;
               }
            }
            if(!IsExist){
                if(!saveInfo_pay.equals("")){
                    saveInfo_pay += ",";
                    saveInfo_round += ",";
                }
                saveInfo_pay += "0";
                saveInfo_round += "0";
            }
            //ゲーム情報を保存
            editer.putString(key_pay,saveInfo_pay);
            editer.putString(key_round,saveInfo_round);
            editer.commit();
        }

        //プレイ回数をプラスして保存
        playCount ++;
        editer.putInt(key_count,playCount);
        editer.commit();

    }
    //endregion
}
