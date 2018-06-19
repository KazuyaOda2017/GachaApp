package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AsyncPlayer;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Kazuya on 2017/08/13.
 */

public class Aggregate extends Activity {

    private GameInfo gameinfo;
    private TextView textView_sum;
    private TextView textView_equality;
    private LinearLayout layout;
    private  ViewGroup vg;
    private TextView textView_remainder;
    private TextView textView_playerSum;
    private TextView textView_playerRemainder;
    private ViewGroup vgrd;


    private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
    private AccountInfo accountInfo;
    private SaveGames saveGames;

    //region コンストラクタ
    public Aggregate(){
        accountInfo = new AccountInfo();
        saveGames = new SaveGames();
    }
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aggregate_activity);

        try{
            //ゲーム情報の取得
            gameinfo = (GameInfo)getIntent().getSerializableExtra("GameInfo");

            //合計金額TextViewを設定
            textView_sum = (TextView)findViewById(R.id.text_sumMoney);
            String str_sum = String.valueOf(gameinfo.getSumMoney());
            textView_sum.setText(str_sum);

            //均等割り勘金額をTextViewに設定
            textView_equality = (TextView)findViewById(R.id.text_equalityMoney);
            int num = gameinfo.getSumMoney();
            int equal_num = (num/gameinfo.getPlayerNum());
            String str_equal = String.valueOf(equal_num);
            textView_equality.setText(str_equal);

            //余りをTextViewに設定
            textView_remainder = (TextView)findViewById(R.id.text_remainder);
            int remainder = num - (equal_num * gameinfo.getPlayerNum());
            String str_remainder = String.valueOf(remainder);
            textView_remainder.setText(str_remainder);

        }
        catch (Exception e){

        }

        if(!gameinfo.getGameFinishFlag()){
            //LRFlagで別の計算処理をする
            if(gameinfo.getLRFlag()){
                searchLR();
            }else{
                //プレイヤーの減算金額を登録する
                SetMinusMoney();

                //減算金額から加算金額を算出する
                SetPlusMoney();

                //支払金額を設定する
                SetPayMoney();
            }
            gameinfo.setGameFinishFlag(true);
        }else {

        }

        //LinearLayoutを設定
        layout = (LinearLayout)findViewById(R.id.linearlayout);

        //テーブルレイアウトを設定
        vg = (ViewGroup)findViewById(R.id.table_layout);

        //プレやー分結果画面を作成する
        SetPlayerInfoToScreen(vg, setMoneyMode.standard);

        //ゲーム終了ボタンの設定
        final Button endBtn = (Button)findViewById(R.id.end_btn);

        //ボタンをリスナーに登録
        endBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //ボタンクリック処理
                ShowDialog();
            }
        });

        //保存ボタンを設定
        final Button saveBtn = (Button)findViewById(R.id.saveend_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //データを保存
                saveGameInfo(gameinfo);
                //終了ボタンのテキスト変更
                endBtn.setText(getString(R.string.end_btn));
                //セーブボタンを無効化
                saveBtn.setEnabled(false);
                //トーストを表示
                Toast.makeText(Aggregate.this, getString(R.string.text_save_gameinfo), LENGTH_SHORT).show();


            }
        });
        //デフォルトグループなら無効にする
        if(gameinfo.getGroupName().equals(accountInfo.DEFAULT_GROUP)){
            saveBtn.setEnabled(false);
        }

        //プレイヤーの支払合計を表示
        List<Integer> payMoneyList = creatPlayerPayMoneyList();
        int playerPay_sum = Sum(payMoneyList);
        textView_playerSum = (TextView)findViewById(R.id.text_sumPayMoney);
        textView_playerSum.setText(String.valueOf(playerPay_sum));

        //支払いの余りを表示
        int player_remainder = gameinfo.getSumMoney() - playerPay_sum;
        textView_playerRemainder = (TextView)findViewById(R.id.text_sumPayMoney_remainder);
        textView_playerRemainder.setText(String.valueOf(player_remainder));

        //端数切捨て金額を設定
        setRoundDownMoney();
        //合計を算出
        List<Integer> roundDownList = creatRoundDownList();
        int roundDownSum = Sum(roundDownList);

        //端数切捨て表を設定
        vgrd = (ViewGroup)findViewById(R.id.table_layout_roundDown);
        SetPlayerInfoToScreen(vgrd, setMoneyMode.roundDown);

        //端数支払金合計をTextViewに設定
        TextView textView_sumRoundDown = (TextView)findViewById(R.id.text_sumRoundDownMoney);
        textView_sumRoundDown.setText(String.valueOf(roundDownSum));

        //不足金を設定
        TextView textView_roundDown_remainder = (TextView)findViewById(R.id.text_sumRoundDown_remainder);
        int num = gameinfo.getSumMoney() - roundDownSum;
        textView_roundDown_remainder.setText(String.valueOf(num));


    }

    //region 終了処理
    //終了ダイアログの表示
    private void ShowDialog(){

        //final boolean result = false;

        new AlertDialog.Builder(this).setTitle("ゲーム終了")
                .setMessage("スタート画面に戻ります。よろしいですか？")
                .setPositiveButton("YES", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface daialog, int which){
                        Intent intent = new Intent(getApplication(),StartActivity.class);
                        startActivity(intent);
                        Aggregate.this.finish();
                    }
                })
                .setNegativeButton("NO",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){

                    }
                }).show();
    }

    //戻るボタンの制御
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            ShowDialog();
            return true;
        }
        return false;
    }
    //endregion

    //region 減算金額設定
    //減算金額の設定
    private void SetMinusMoney(){

        //プレイヤー分ループ
        for(PlaylerInfo pInfo:gameinfo.getPlayerInfoList()){
            int minusNum = 0;
            int minusParsent = 0;
            //カード情報で選別
            switch (pInfo.getCardInfo()){
                case Rere:
                    minusParsent = Const.DISCOUNT_R;
                    break;
                case SR:
                    minusParsent = Const.DISCOUNT_SR;
                    break;
                case SSR:
                    minusParsent = Const.DISCOUNT_SSR;
                    break;
                case UR:
                    minusParsent = Const.DISCOUNT_UR;
                    break;
                case LR:
                case N:
                    break;
            }
            minusNum = pInfo.getBasicMoney() * minusParsent / 100;

            //プレイヤー情報に上書き
            pInfo.setMinusMoney(minusNum);
        }
    }
    //endregion

    //region 加算金額設定
    //加算金額の設定
    private void SetPlusMoney(){

        //カードレアリティのリストを取得
        List<PlaylerInfo.CardRealityInfo> cardRealityInfos = PlaylerInfo.CardRealityInfo.getList();
        //カードリスト分ループ
        for(PlaylerInfo.CardRealityInfo cardInfo:cardRealityInfos){

            //レアリティがNならコンテニュー
            if(cardInfo == PlaylerInfo.CardRealityInfo.N){
                continue;
            }

            //レアリティが一致するプレイヤーを検索
            for(PlaylerInfo pInfo:gameinfo.getPlayerInfoList()){
                int count_under = 0;
                int plus_money = 0;

                if(pInfo.getCardInfo() == cardInfo){
                    //レアリティが下位のプレイヤーを検索
                    count_under = SearchUnderRare(pInfo.getCardInfo());
                    //加算金額を計算
                    if(count_under == 0){
                        //自身に加算金額を設定する
                        pInfo.setPlusMoney(pInfo.getMinusMoney());
                        continue;
                    }
                    plus_money = pInfo.getMinusMoney() / count_under;
                    //加算金額を設定
                    SetPlayerInfoToPlusMoney(plus_money ,pInfo.getCardInfo());
                }
            }
        }

    }

    //下位レアリティの検索
    private int SearchUnderRare(PlaylerInfo.CardRealityInfo cardInfo){
        int count = 0;

        for(PlaylerInfo pInfo : gameinfo.getPlayerInfoList()){
            if(pInfo.getCardInfo().getInt() < cardInfo.getInt()){
                count++;
            }
        }

        return count;
    }

    //下位レアリティのプレイヤーに加算金額を設定する
    private void SetPlayerInfoToPlusMoney(int num, PlaylerInfo.CardRealityInfo cInfo){

        for(PlaylerInfo pInfo: gameinfo.getPlayerInfoList()){
            if(pInfo.getCardInfo().getInt() < cInfo.getInt()){
                pInfo.setPlusMoney(num);
            }
        }
    }
    //endregion

    //region 支払い金額の設定
    private void SetPayMoney(){
        for(PlaylerInfo pInfo:gameinfo.getPlayerInfoList()){
            int payMoney = 0;
            payMoney = pInfo.getBasicMoney() - pInfo.getMinusMoney() + pInfo.getPlusMoney();

            //支払金額の設定
            pInfo.setPaymentMoney(payMoney);
        }
    }
    //endregion

    //region 端数切捨て金額を設定
    private void setRoundDownMoney(){
        for(PlaylerInfo pInfo: gameinfo.getPlayerInfoList()){
            pInfo.setRoundDownPayMoney(pInfo.getPaymentMoney());
        }
    }
    //endregion

    //region LR用計算処理
    //LRのプレイヤーを探す
    private void searchLR(){
        //レジェンドレアプレイヤーの支払金額を設定
        int payMoney_LR = gameinfo.getSumMoney()/2;
        //他プレイヤーの支払金額を設定
        int payMoney_other = 0;
        int count = gameinfo.getPlayerNum() - 1;
        if(count == 0){
            //プレイヤーが二人の場合はさらに半分を負担
            payMoney_LR += (payMoney_LR/2);
            //残りをもう一人が負担
            payMoney_other = gameinfo.getSumMoney() - payMoney_LR;
        }else{
            payMoney_other = payMoney_LR / count;
        }
        //
        for(PlaylerInfo pInfo:gameinfo.getPlayerInfoList()){
            if(pInfo.getCardInfo() == PlaylerInfo.CardRealityInfo.LR){
                //合計金額の半分を支払金額に設定
                pInfo.setPaymentMoney(payMoney_LR);
            }else{
                pInfo.setPaymentMoney(payMoney_other);
            }
        }
    }
    //endregion

    //region プレイヤーガチャ結果表示処理
    private void SetPlayerInfoToScreen(ViewGroup viewGroup, setMoneyMode mode){
        int row = 0;

        //一番上は項目名
        getLayoutInflater().inflate(R.layout.table_row, viewGroup);
        TableRow trindex = (TableRow)viewGroup.getChildAt(row);
        String player_name = getString(R.string.text_playerName);
        ((TextView)(trindex.getChildAt(0))).setText(player_name);
        String pay = getString(R.string.text_pay);
        ((TextView)(trindex.getChildAt(1))).setText(pay);

        row ++;

        for(PlaylerInfo pInfo: gameinfo.getPlayerInfoList()){

            //テーブルに行を追加
            getLayoutInflater().inflate(R.layout.table_row, viewGroup);
            TableRow tr = (TableRow)viewGroup.getChildAt(row);
            String str = pInfo.getPlayerName();
            ((TextView)(tr.getChildAt(0))).setText(str);
            if(mode == setMoneyMode.standard){
                ((TextView)(tr.getChildAt(1))).setText(String.valueOf(pInfo.getPaymentMoney()));
            }else{
                ((TextView)(tr.getChildAt(1))).setText(String.valueOf(pInfo.getRoundDownPayMoney()));
            }

           row++;
        }
    }

    enum setMoneyMode{
        standard,
        roundDown,
    }
    //endregion

    //プレイヤーの支払金額のリストを作成
    private List<Integer> creatPlayerPayMoneyList(){
        List<Integer> list = new ArrayList<Integer>();
        for(PlaylerInfo pInfo:gameinfo.getPlayerInfoList()){
            list.add(pInfo.getPaymentMoney());
        }
        return list;
    }

    //プレイヤーの端数切捨て金額のリストを作成
    private List<Integer> creatRoundDownList(){
        List<Integer> list = new ArrayList<Integer>();
        for(PlaylerInfo pInfo:gameinfo.getPlayerInfoList()){
            list.add(pInfo.getRoundDownPayMoney());
        }
        return list;
    }

    //合計算出メソッド
    private int Sum(List<Integer> list){
        int sum = 0;
        for(Integer num : list){
            sum += num;
        }
        return sum;
    }

    //region ゲーム情報を保存する
    public void saveGameInfo(GameInfo gameInfo){

        SharedPreferences preferences = null;
        SharedPreferences groupPre = null;
        SharedPreferences.Editor editer = null;

        List<String> playerlist = new ArrayList<String>();

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
        editer.putString(key,sumInfo);
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
