package com.example.kazuya.gachaapp;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Kazuya on 2017/08/12.
 */

//ゲーム情報クラス
public class GameInfo implements Serializable{

    //region 変数
    //プレイヤー人数
    private int player_num;
    public int getPlayerNum(){
        return player_num;
    }
    public void setPlayer_num(int num){
        player_num = num;
    }
    //合計金額
    private int sumMoney;
    public int getSumMoney(){
        return sumMoney;
    }
    public void setSumMoney(int num){
        sumMoney = num;
    }
    //プレイヤー情報リスト
    private List<PlaylerInfo> playerInfoList;
    public List<PlaylerInfo> getPlayerInfoList(){
        return  playerInfoList;
    }
    public void setPlayerInfoList(PlaylerInfo pInfo){
        playerInfoList.add(pInfo);
    }
    //ゲーム終了フラグ
    private boolean gameFinishFlag = false;
    public boolean getGameFinishFlag(){
        return gameFinishFlag;
    }
    public void setGameFinishFlag(boolean flag){
        gameFinishFlag = flag;
    }
    //LRフラグ
    private boolean LRFlag = false;
    public boolean getLRFlag(){
        return  LRFlag;
    }
    public void setLRFlag(boolean flag){
        LRFlag = flag;
    }
    //グループ名
    private String groupName;
    public String getGroupName(){
        return groupName;
    }
    public void setGroupName(String str){
        groupName = str;
    }
    //endregion

    //region コンストラクター
    public GameInfo(){
        playerInfoList = new ArrayList<PlaylerInfo>();
    }
    //endregion

    //region メソッド
    //プレイヤー情報のリセット
    public void PlayerListClear(){
        playerInfoList.clear();
    }
    //endregion
}
