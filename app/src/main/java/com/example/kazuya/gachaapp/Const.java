package com.example.kazuya.gachaapp;

/**
 * Created by Kazuya on 2017/08/11.
 */

//定数クラス
public class Const {

    //region レアリティ設定
    //ノーマル
    public static int NOMAL = (int)CardInfo.RATE_NOMAL;
    //レア
    public static int RARE = NOMAL + (int)CardInfo.RATE_RARE;
    //Sレア
    public static int SR = RARE + (int)CardInfo.RATE_SR;
    //SSR
    public static int SSR= SR + (int) CardInfo.RATE_SSR;
    //UR
    public static int UR = SSR + (int)CardInfo.RATE_UR;
    //LR
    public static int LR = UR + (int)CardInfo.RATE_LR;
    //endregion

    //region 割引額設定
    //レア
    public static int DISCOUNT_R = 10;
    //Sレア
    public static int DISCOUNT_SR = 20;
    //SSレア
    public static int DISCOUNT_SSR = 30;
    //URレア
    public static int DISCOUNT_UR = 50;
    //endregion

    //region グループ設定
    public static int GROUP_COUNT_MAX = 10;
    //endregion
}
