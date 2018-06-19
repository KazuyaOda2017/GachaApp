package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Kazuya on 2017/08/27.
 */

public class HistoryActivity extends Activity {

    private String groupKey;
    private SharedPreferences preferences;
    private SharedPreferences groupPre;

    private AccountInfo accountInfo;
    private int countGame;
    private String payMoneySum;
    private String players;
    private String key;

    private List<String> playerList;

    public HistoryActivity() {
    accountInfo = new AccountInfo();
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.history_layout);

        //グループ情報を取得
        Intent intent = getIntent();
        groupKey = intent.getStringExtra("Key");

        //プリファレンスを取得
        try{
            preferences = getSharedPreferences(AccountInfo.PREF_NAME_GAME,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
            groupPre = getSharedPreferences(AccountInfo.PREF_NAME_ACCOUNT,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
        }catch (Exception e){

        }

        //グループ名を表示
        TextView textView_GroupName = (TextView)findViewById(R.id.textView_GroupName);
        textView_GroupName.setText(groupKey);

        try{
            key = accountInfo.creatGameInfoKeyByGroup(groupKey);

            //ゲーム回数を取得
            String key_gamecount = accountInfo.createKeyByGroupKey(key,AccountInfo.COUNT_KEY);
            countGame = preferences.getInt(key_gamecount,0);

            TextView textView_GameCount = (TextView)findViewById(R.id.textView_CountGame);
            textView_GameCount.setText(String.valueOf(countGame));

            //支払金額を取得
            String key_moneySum = accountInfo.createKeyByGroupKey(key,AccountInfo.SUM_KEY);
            payMoneySum = preferences.getString(key_moneySum,"");

            List<Integer> payList = accountInfo.getListToInteger(payMoneySum);
            int sumMoney = 0;
            for(int i:payList){
                sumMoney += i;
            }

            TextView textView_PaySum = (TextView)findViewById(R.id.textView_PayMoneySum);
            textView_PaySum.setText(String.valueOf(sumMoney));

            //ゲームプレイヤーを取得
            String key_players = accountInfo.addPlayerKey(groupKey);
            players = groupPre.getString(key_players,"");

            //プレイヤーのリストを作成
            playerList = accountInfo.getListToString(players);

            //支払テーブルを作成
            ViewGroup vg = (ViewGroup)findViewById(R.id.table_playerPay);
            creatTable(vg,playerList,ROW_ADD_MODE.STANDERD);

            //支払テーブル(端数切捨て)を作成
            ViewGroup vg2 = (ViewGroup)findViewById(R.id.table_playerRoundPay);
            creatTable(vg2,playerList,ROW_ADD_MODE.ROUND);

        }catch (Exception e){

        }
    }

    //ViewGropを渡してTableを作成
    private void creatTable(ViewGroup vg,List<String> playerList,ROW_ADD_MODE mode){
        int row = 0;

        //項目名を追加
        getLayoutInflater().inflate(R.layout.table_row, vg);
        TableRow tr = (TableRow)vg.getChildAt(row);
        String player_name = getString(R.string.text_playerName);
        ((TextView)(tr.getChildAt(0))).setText(player_name);
        String pay = getString(R.string.text_pay);
        ((TextView)(tr.getChildAt(1))).setText(pay);

        row++;

        for(String player: playerList){

            //プレイヤーの支払情報を取得
           int num = getSumPayInfo(player, mode);

            //行を追加
            getLayoutInflater().inflate(R.layout.table_row, vg);
            TableRow _tr = (TableRow) vg.getChildAt(row);
            ((TextView)(_tr.getChildAt(0))).setText(player);
            ((TextView)(_tr.getChildAt(1))).setText(String.valueOf(num));

            row++;
        }
    }

    //プレイヤーの支払合計を取得する
    private int getSumPayInfo(String player,ROW_ADD_MODE mode){
        int sum = 0;
        String payInfo = "";
        String key = "";
        //モードで取得する情報をスイッチ
        switch (mode){
            case STANDERD:
                key = accountInfo.creatKeyByGroupKeyWithPlayer(this.key,player,AccountInfo.PAY_KEY);
                break;
            case ROUND:
                key = accountInfo.creatKeyByGroupKeyWithPlayer(this.key,player,AccountInfo.ROUND_KEY);
                break;
        }
        payInfo = preferences.getString(key,"");
        //リストに変換
        List<Integer> payList = accountInfo.getListToInteger(payInfo);

        //リストを回して合計金額を計算
        for(int i : payList){
            sum += i;
        }

        return sum;
    }

    enum ROW_ADD_MODE{
        STANDERD,
        ROUND,
    }
}
