package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static com.example.kazuya.gachaapp.Config.SELECT_TYPE.CHECKBOX;

/**
 * Created by Kazuya on 2017/08/12.
 */

public class Config extends Activity implements TextWatcher{

    private EditText editText;
    private TextView exception_text;
    private TextView exception_playernum;
    private int sumMoney;
    private int basicMoney;
    private boolean InputSumMoneyFlag = false;
    private Button start_btn;
    private List<CheckBox> checkBoxList;

    private GameInfo gameInfo;
    private int count_player;

    private TextView textView_equality;
    private TextView textView_equalityMoney;
    private TextView textView_playerChoise;

    private List<String> playerlist = new ArrayList<String>();
    private SharedPreferences preferences;
    private AccountInfo accountInfo;
    LinearLayout linearlayout;

    private SELECT_TYPE selectType;


    //コンストラクター
    public Config(){
        checkBoxList = new ArrayList<CheckBox>();
        gameInfo = new GameInfo();
        count_player = 0;
        basicMoney = 0;
        sumMoney = 0;
        accountInfo = new AccountInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.config_activity);

        //グループ情報を取得
        Intent intent = getIntent();
        String key = intent.getStringExtra("Key");

        if(!key.trim().equals("")){
            //プレイヤーリストを取得
            String players = "";
            String makeKey =accountInfo.addPlayerKey(key);
            try{
                preferences = getSharedPreferences(AccountInfo.PREF_NAME_ACCOUNT,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
                players = preferences.getString(makeKey,"");
                playerlist = accountInfo.getListToString(players);

                //グループ名を設定
                gameInfo.setGroupName(key);

            }catch (Exception e){

            }
        }else{

            //グループ名をデフォルトに設定
            gameInfo.setGroupName(accountInfo.DEFAULT_GROUP);
        }

        linearlayout = (LinearLayout)findViewById(R.id.linearlayout_checkbox);

        //金額入力の監視
        editText = (EditText)findViewById(R.id.money_sum);
        //リスナーの登録
        editText.addTextChangedListener(this);

        //警告文を設定
        exception_text = (TextView)findViewById(R.id.exception_text);
        exception_playernum = (TextView)findViewById(R.id.exception_player_text);

        //均等割り勘テキストビューを設定
        textView_equality = (TextView)findViewById(R.id.text_equality);
        textView_equalityMoney = (TextView)findViewById(R.id.equality);

        textView_playerChoise = (TextView)findViewById(R.id.text_player_choise);

        if(playerlist.size() != 0){
            //チェックボックスリストの作成
            RegistCheckBox();
            //デフォルト画面に設定
            SetDefaltScreen();
            //各チェックボックスにリスナーを登録
            setCheckBoxListener();
            //選択タイプを設定
            selectType = CHECKBOX;
        }else {
            //人数入力エディットテキストを設定
            createEditText();
            //選択タイプを設定
            selectType = SELECT_TYPE.TEXT;
        }


        //スタートボタンを設定
        start_btn = (Button)findViewById(R.id.start_btn);
        //リスナーに登録
        start_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //スタートボタン押下処理

                //入力金額のチェック
                if(!InputSumMoneyFlag){
                    return;
                }
                else{
                    gameInfo.setSumMoney(sumMoney);
                }

                //プレイヤー情報のリセット
                count_player = 0;
                gameInfo.PlayerListClear();

                //プレイヤーの登録
                switch (selectType){
                    case CHECKBOX:
                        for(CheckBox s : checkBoxList){
                            RegistPlayerInfo(s);
                        }
                        break;
                    case TEXT:
                        if(!createPlayerListByInputText()){
                            exception_playernum.setText("半角数字で入力してください");
                            exception_playernum.setVisibility(View.VISIBLE);
                            return;
                        }
                        break;
                }

                //プレイヤー人数チェック
                if(count_player < 2){
                    exception_playernum.setVisibility(View.VISIBLE);
                    return;
                }else{
                    exception_playernum.setVisibility(View.INVISIBLE);
                }

                //基底金額の登録
                SetBacicMoney(sumMoney);
                gameInfo.setPlayer_num(count_player);

                //画面移行データの作成
                Intent intent = new Intent(getApplication(), MainActivity.class);
                intent.putExtra("GameInfo", gameInfo);
                startActivity(intent);
                //画面を初期状態に戻す
                //SetDefaltScreen();

            }
        });
    }

    //region 戻るボタンの制御
    //終了ダイアログの表示
    private void ShowDialog(){

        //final boolean result = false;

        new AlertDialog.Builder(this).setTitle("ゲーム終了")
                .setMessage("スタート画面に戻ります。よろしいですか？")
                .setPositiveButton("YES", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface daialog, int which){
                        Config.this.finish();
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

    //基底金額の設定
    private void SetBacicMoney(int num){
        basicMoney = num / count_player;

        for(PlaylerInfo p: gameInfo.getPlayerInfoList()){
            p.setBasicMoney(basicMoney);
        }
    }

    //プレイヤー情報の登録
    private void RegistPlayerInfo(CheckBox checkBox){
        PlaylerInfo pInfo = new PlaylerInfo();
        if(checkBox.isChecked()){
            pInfo.setPlayerName(checkBox.getText().toString());

            gameInfo.setPlayerInfoList(pInfo);

            count_player ++;
        }
    }

    //チェックボックスの登録
    private void RegistCheckBox(){

        //プレイヤーリスト分チェックボックスを作成
        for(String str : playerlist){
            CheckBox cb = new CheckBox(this);
            cb.setText(str);
            cb.setChecked(true);
            linearlayout.addView(cb);
            checkBoxList.add(cb);
        }

    }

    //各チェックボックスにリスナーを登録
    private void setCheckBoxListener(){
        for(CheckBox cb : checkBoxList){
            cb.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    try{
                        //プレイヤー数をカウント
                        int count = CheckBoxCount();
                        //割り勘金額を表示
                        getDutchTreat(count);
                    }catch (Exception e){
                        return;
                    }
                }
            });
        }
    }

    //region 人数入力エリアを作成
    private void createEditText(){

        textView_playerChoise.setText("プレイヤー人数を入力してください");

        EditText editText = new EditText(this);
        editText.setInputType(TYPE_CLASS_TEXT);
        editText.setHint("半角数字入力");
        editText.setTextSize(20);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                int num = 0;
                try {
                    num = Integer.parseInt(s.toString());
                    getDutchTreat(num);
                    exception_playernum.setText("プレイヤーは二人以上にしてください");
                    exception_playernum.setVisibility(View.INVISIBLE);
                }catch (Exception e){
                    num = 0;
                    getDutchTreat(num);
                    exception_playernum.setText("半角数字で入力してください");
                    exception_playernum.setVisibility(View.VISIBLE);
                }
            }
        });

        linearlayout.addView(editText);

    }
    //endregion

    //region 入力監視メソッド
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    //入力終了時
    @Override
    public void afterTextChanged(Editable s) {

        try{
            sumMoney = Integer.parseInt(s.toString());

            InputSumMoneyFlag = true;
            exception_text.setVisibility(View.INVISIBLE);


        }catch (Exception e){
            exception_text.setVisibility(View.VISIBLE);
            InputSumMoneyFlag = false;
            return;
        }

        try{
            //プレイヤー数をカウント
            int count = CheckBoxCount();
            //割り勘金額を表示
            getDutchTreat(count);
        }catch (Exception e){
            return;
        }

    }
    //endregion

    //割り勘金額を設定・表示
    private void getDutchTreat(int count){

        if(count == 0){
            //TextViewを非表示にしてリターン
            textView_equality.setVisibility(View.INVISIBLE);
            textView_equalityMoney.setVisibility(View.INVISIBLE);
            return;
        }
        int sumMoney = 0;
        try{
            //入力した金額を取得
            String str = editText.getText().toString();
            //intに型変換
            sumMoney = Integer.parseInt(str);

        }catch (Exception e){
            //TextViewを非表示にしてリターン
            textView_equality.setVisibility(View.INVISIBLE);
            textView_equalityMoney.setVisibility(View.INVISIBLE);
            return;
        }
        //TextViewを表示
        textView_equality.setVisibility(View.VISIBLE);
        textView_equalityMoney.setVisibility(View.VISIBLE);

        int num = sumMoney / count;

        textView_equalityMoney.setText(String.valueOf(num));

    }

    //プレイヤー数をカウント
    private int CheckBoxCount(){
        int count = 0;
        for(CheckBox cb: checkBoxList){
            if(cb.isChecked()){
                count++;
            }
        }
        return count;
    }

    //初期画面設定
    private void SetDefaltScreen(){
        //全てのチェックボックスを入れる
        for(CheckBox cb:checkBoxList){
            cb.setChecked(true);
        }
        //警告文を非表示にする
        exception_playernum.setVisibility(View.INVISIBLE);
        exception_text.setVisibility(View.INVISIBLE);

    }

    //region 入力情報からプレイヤーリストを作成
    private boolean createPlayerListByInputText(){
        EditText edittext = (EditText)linearlayout.getChildAt(0);
        int num = 0;
        try{
            num = Integer.parseInt(edittext.getText().toString());


        }catch (Exception e){

            return false;

        }
        for(int i = 1;i <= num; i++){
            PlaylerInfo pInfo = new PlaylerInfo();

            pInfo.setPlayerName(String.format("%s%d",AccountInfo.DEFAULT_PLAYER,i));
            gameInfo.setPlayerInfoList(pInfo);

            count_player ++ ;
        }
        return true;
    }
    //endregion

    //region プレイヤー選択タイプ
    enum SELECT_TYPE{
        CHECKBOX,
        TEXT,
    }
    //endregion
}
