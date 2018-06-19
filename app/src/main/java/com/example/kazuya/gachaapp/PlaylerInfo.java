package com.example.kazuya.gachaapp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kazuya on 2017/08/12.
 */

//プレイヤー情報クラス
public class PlaylerInfo implements Serializable{

    //region 変数

    //プレイヤー名
    private String playler_name ;
    public String getPlayerName(){
        return  playler_name;
    }
    public void setPlayerName(String name){
        playler_name = name;
    }
    //基底金額
    private int basic_money;
    public int getBasicMoney(){
        return basic_money;
    }
    public void setBasicMoney(int num){
        basic_money = num;
    }
    //減算金額
    private int minus_money ;
    public int getMinusMoney(){
        return minus_money;
    }
    public void setMinusMoney(int num){
        minus_money = num;
    }
    //加算金額
    private int plus_money = 0;
    public int getPlusMoney(){
        return plus_money;
    }
    public void setPlusMoney(int num){
        plus_money += num;
    }
    //カード情報
    private CardRealityInfo cardR_Info;
    public CardRealityInfo getCardInfo(){
        return cardR_Info;
    }
    public void setCardInfo(CardRealityInfo cInfo){
        cardR_Info = cInfo;
    }
    //ガチャフラグ
    private boolean gachaFlag;
    public boolean getGachaFlag(){
        return  gachaFlag;
    }
    public void setGachaFlag(boolean flag){
        gachaFlag = flag;
    }
    //ガチャ済みフラグ
    private boolean gachaFinishFlag;
    public boolean getGachaFinishFlag(){
        return  gachaFinishFlag;
    }
    public void setGachaFinishFlag(boolean flag){
        gachaFinishFlag = flag;
    }
    //支払い金額
    private int payment_money;
    public int getPaymentMoney(){
        return  payment_money;
    }
    public void setPaymentMoney(int num){
        payment_money = num;
    }
    //端数切捨て金額
    private int roundDownPayMoney;
    public int getRoundDownPayMoney(){
        return  roundDownPayMoney;
    }
    public void setRoundDownPayMoney(int num){
        int setNum = num / 100;
        roundDownPayMoney = setNum * 100;
    }
    //endregion

    //region カードレアリティ
    enum CardRealityInfo{
        N(0),
        Rere(1),
        SR(2),
        SSR(3),
        UR(4),
        LR(999);

        private CardRealityInfo(final int id) {
            this.id = id;
        }
        private final int id;
        public int getInt(){
            return this.id;
        }

        //要素をリストにして返す
        public static List<CardRealityInfo> getList(){
            List<CardRealityInfo> list = new ArrayList<CardRealityInfo>();
            for(int i = 0 ; i < CardRealityInfo.values().length; i ++){
                list.add(CardRealityInfo.values()[i]);
            }

            return list;
        }

    }
    //endregion

}

