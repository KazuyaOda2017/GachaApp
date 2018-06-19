package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Random;

/**
 * Created by Kazuya on 2017/08/11.
 */

public class Result extends Activity{

    //CardName cName = null;
    int num = 0;
    private GameInfo gameInfo;
    private PlaylerInfo.CardRealityInfo cardRealityInfo;
    ImageView cardImage;
    private String player_name;
    CardName cName;
    private boolean showCardFlag = false;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result_activity);

        //ゲーム情報の取得
        gameInfo = (GameInfo)getIntent().getSerializableExtra("GameInfo");

        //ImageViewの設定
        cardImage = (ImageView)findViewById(R.id.result_card);

        //レアリティ抽選
        cardRealityInfo = RarerityLottery();
        SetCardInfo();
        //カード抽選
        cName = CardLottery(cardRealityInfo);


        //カードを裏向き表示のImageButton設定
        cardImage.setImageResource(R.drawable.card_ura);
        //リスナーを登録
        cardImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(showCardFlag){
                    return;
                }
                //カードを表示
                ShowCard(cName);
            }

        });

        //戻るボタンの設定
        Button againBtn = (Button)findViewById(R.id.again_btn);
        //リスナーに登録
        againBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent();

                intent.putExtra("ResultInfo",gameInfo);

                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    //ガチャを引くプレイヤーを探す
    private PlaylerInfo SearchPlayer(){
        for(PlaylerInfo pInfo: gameInfo.getPlayerInfoList()){
            if(pInfo.getGachaFlag()){
                return pInfo;
            }
        }
        return null;
    }

    //ガチャ結果をゲーム情報に保存する
    private void SetCardInfo(){

        //ガチャフラグがtrueのプレイヤーを探す
        for(PlaylerInfo pInfo:gameInfo.getPlayerInfoList()){
            if(pInfo.getGachaFlag()){
                pInfo.setCardInfo(cardRealityInfo);
                pInfo.setGachaFinishFlag(true);
            }
        }

    }

    //スマホの戻るボタンを押下したとき
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent();

            intent.putExtra("ResultInfo",gameInfo);

            setResult(RESULT_OK, intent);
            finish();

            return true;
        }
        return false;
    }

    //レアリティ抽選
    private PlaylerInfo.CardRealityInfo RarerityLottery(){

        PlaylerInfo.CardRealityInfo cr;

        //乱数の取得
        int num = GetRandomInt(10000);

        //レアリティ抽選
        if(num<Const.NOMAL){
            cr = PlaylerInfo.CardRealityInfo.N;
        }else if(num<Const.RARE){
            cr = PlaylerInfo.CardRealityInfo.Rere;
        }else if(num<Const.SR){
            cr = PlaylerInfo.CardRealityInfo.SR;
        }else if(num<Const.SSR){
            cr = PlaylerInfo.CardRealityInfo.SSR;
        }else if(num<Const.UR){
            cr = PlaylerInfo.CardRealityInfo.UR;
        }else{
            cr = PlaylerInfo.CardRealityInfo.LR;
        }

        return cr;
    }

    //カード抽選
    private CardName CardLottery(PlaylerInfo.CardRealityInfo cardRealityInfo){

        CardName cName;

        switch (cardRealityInfo){
            case N:
                cName = GetNormalCard();
                break;
            case Rere:
                cName = GetRareCard();
                break;
            case SR:
                cName = GetSRCard();
                break;
            case SSR:
                cName = GetSSRCard();
                break;
            case UR:
                cName = GetURCard();
                break;
            case LR:
                cName = GetLRCard();
                break;
            default:
                cName = null;
                break;
        }

        return cName;
    }

    //region レアリティ別カード抽選
    //ノーマルカード抽選
    private CardName GetNormalCard(){

        int num = GetRandomInt(30);

        if(num < 10){
            return CardName.oda_n;
        }else if(num < 20){
            return CardName.nakano_n;
        }else{
            return CardName.shinkawa_n;
        }
    }
    //レアカード抽選
    private CardName GetRareCard(){
        int num = GetRandomInt(40);

        if(num < 10){
            return CardName.nakano_r;
        }else if(num < 20){
            return CardName.shinkawa_r;
        }else if(num < 30){
            return CardName.nakahashi_r;
        }else{
            return CardName.tanaka_r;
        }
    }
    //SRカード抽選
    private CardName GetSRCard(){
        int num = GetRandomInt(20);

        if(num < 10){
            return CardName.nakahashi_sr;
        }else{
            return CardName.shinkawa_sr;
        }
    }
    //SSRカード抽選
    private CardName GetSSRCard(){
        int num = GetRandomInt(20);

        if(num < 10){
            return CardName.nakahashi_ssr;
        }else{
            return CardName.tanaka_ssr;
        }
    }
    //URカード抽選
    private CardName GetURCard(){
        int num = GetRandomInt(20);

        if(num < 10){
            return CardName.nakano_ur;
        }else{
            return CardName.tanaka_ur;
        }
    }
    //LRカード
    private CardName GetLRCard(){
        return CardName.mitsuhashi;
    }
    //endregion

    //表示カード取得
    private void ShowCard(CardName cName){

        switch (cName){
            case oda_n:
                cardImage.setImageResource(R.drawable.oda_n);
                break;
            case nakano_n:
                cardImage.setImageResource(R.drawable.nakano_n);
                break;
            case shinkawa_n:
                cardImage.setImageResource(R.drawable.shinkawa_n);
                break;
            case nakano_r:
                cardImage.setImageResource(R.drawable.nakano_r);
                break;
            case shinkawa_r:
                cardImage.setImageResource(R.drawable.shinkawa_r);
                break;
            case nakahashi_r:
                cardImage.setImageResource(R.drawable.nakahashi_r);
                break;
            case tanaka_r:
                cardImage.setImageResource(R.drawable.tanaka_r);
                break;
            case shinkawa_sr:
                cardImage.setImageResource(R.drawable.shinkawa_sr);
                break;
            case nakahashi_sr:
                cardImage.setImageResource(R.drawable.nakahashi_sr);
                break;
            case tanaka_ssr:
                cardImage.setImageResource(R.drawable.tanaka_ssr);
                break;
            case nakahashi_ssr:
                cardImage.setImageResource(R.drawable.nakahashi_ssr);
                break;
            case nakano_ur:
                cardImage.setImageResource(R.drawable.nakano_ur);
                break;
            case tanaka_ur:
                cardImage.setImageResource(R.drawable.tanaka_ur);
                break;
            case mitsuhashi:
                cardImage.setImageResource(R.drawable.mitsuhashi);
                break;
        }
        //カード表示フラグをTrueにする
        showCardFlag = true;
    }

    //乱数取得メソッド
    private int GetRandomInt(int max){
        Random r = new Random();
        return r.nextInt(max);
    }

    //カード列挙
    enum CardName{
        oda_n,
        nakano_n,
        nakano_r,
        nakano_ur,
        shinkawa_n,
        shinkawa_r,
        shinkawa_sr,
        nakahashi_r,
        nakahashi_sr,
        nakahashi_ssr,
        tanaka_r,
        tanaka_ssr,
        tanaka_ur,
        mitsuhashi,
    }

}
