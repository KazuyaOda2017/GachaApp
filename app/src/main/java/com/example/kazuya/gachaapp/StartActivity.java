package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Kazuya on 2017/08/14.
 */

public class StartActivity extends Activity{

    private Button startBtn;
    private Button cardListBtn;
    private Button accountBtn;
    private Button historyBtn;
    private SharedPreferences preferences;
    private List<String> userlist = new ArrayList<String>();
    private AccountInfo accountInfo;
    private String selectGroup = "";

    //メニューを取得
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_setting://グループ設定
                Intent intent = new Intent(getApplication(), AccountActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_history://ゲーム履歴
                readGroupInfo();
                //ラジオボタンダイアログを表示
                showRadioBtnDialog(RAGIOBOTTUN_MODE.HISTORY);
                break;
            case R.id.menu_cardlist://カード一覧
                Intent intentcard = new Intent(getApplication(), CardListActivity.class);
                startActivity(intentcard);
                break;
        }
        return  true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        accountInfo = new AccountInfo();



        //スタートボタンを設定
        startBtn = (Button)findViewById(R.id.gamaStart_btn);
        //リスナーを登録
        startBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                readGroupInfo();

                if(userlist.size() == 0){
                    showNextActivity(selectGroup,Config.class);

                }else{
                    //確認ダイアログを表示
                    showDialog();
                }
            }
        });

        //カードリストボタンを設定
        cardListBtn = (Button)findViewById(R.id.cardList_btn);
        //リスナーを登録
        cardListBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //カード一覧表示画面
                Intent intent = new Intent(getApplication(), CardListActivity.class);
                startActivity(intent);
            }
        });

        //デバッグ用データリセットボタンを設定
        Button resetBtn = (Button)findViewById(R.id.reset_btn);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfimationDialog();
            }
        });

    }

    //region 確認ダイアログを表示
    private void showConfimationDialog(){
        //ダイアログを設定
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setMessage(getString(R.string.dialog_message_alldelete))
                .setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //OKボタン押下処理
                        //データを全て削除する
                        SharedPreferences sharedPreferences = getSharedPreferences(AccountInfo.PREF_NAME_ACCOUNT,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
                        SharedPreferences.Editor e = sharedPreferences.edit();
                        e.clear().commit();
                        userlist.clear();
                        sharedPreferences = getSharedPreferences(AccountInfo.PREF_NAME_GAME,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
                        e = sharedPreferences.edit();
                        e.clear().commit();

                        //トーストを表示
                        Toast.makeText(StartActivity.this, getString(R.string.text_allDelete), LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(getString(R.string.dialog_Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //キャンセル処理
                    }
                })
                .show();
    }
    //endregion

    //region グループ使用確認ダイアログを表示
    private void showDialog(){
        //ダイアログを設定
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setMessage(getString(R.string.dialog_message_UseGroup))
                .setPositiveButton(getString(R.string.dialog_Yes), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //OKボタン押下処理
                        showRadioBtnDialog(RAGIOBOTTUN_MODE.START);

                    }
                })
                .setNegativeButton(getString(R.string.dialog_No), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //キャンセル処理
                        showNextActivity(selectGroup,Config.class);
                    }
                })
                .show();
    }
    //endregion

    //region グループ情報を渡して次の画面へ遷移
    private void showNextActivity(String select,Class<?> cl){
        Intent intent = new Intent(getApplication(), cl);
        intent.putExtra("Key",select);
        startActivity(intent);
        //選択グループをリセット
        selectGroup = "";
    }
    //endregion

    //region アカウント情報読み込み
    private void readGroupInfo(){
        String users = "";

        //アカウント情報の読み込み
        try{
            preferences = getSharedPreferences(AccountInfo.PREF_NAME_ACCOUNT,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
            users = preferences.getString(AccountInfo.USER_KEY,"");
        }catch (Exception e){

        }

        //ユーザーリストを作成
        if(!users.trim().equals("")){
            userlist = accountInfo.getListToString(users);
        }
    }
    //endregion

    //region ラジオボタンダイアログを表示
    private void showRadioBtnDialog(RAGIOBOTTUN_MODE mode){

        final RAGIOBOTTUN_MODE ragiobottun_mode = mode;

        ScrollView sv = new ScrollView(this);

        //ラジオボタンを作成
        RadioGroup rg = new RadioGroup(this);

        int count = 0;//ラジオボタンをカウント
        //リスト分ボタンを作成
        for(String user:userlist){
            //登録プレイヤーが条件をみたしていなければコンテニュー
            if(!checkPlayerCount(user)){
                continue;
            }
            RadioButton rb = new RadioButton(this);
            rb.setText(user);
            rg.addView(rb);
            count++;
        }

        //チェックリスナーを登録
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                selectGroup = "";

                if(checkedId == -1){

                }else{
                    RadioButton radioButton = (RadioButton)group.findViewById(checkedId);

                    //グループ名を取得
                    selectGroup = radioButton.getText().toString();
                }
            }
        });

        //表示するグループが無ければメッセージを表示
        if(count == 0){
            String title = getString(R.string.dialog_title_readerror);
            String message = getString(R.string.dialog_message_readerror);
            showErrorDialog(title,message);
            return;
        }

        sv.addView(rg);

        //ダイアログを表示
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle(getString(R.string.dialog_title_group))
                .setView(sv)
                .setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //OKボタン押下処理

                        Class<?> cl = null;
                        switch (ragiobottun_mode){
                            case START:
                                cl = Config.class;
                                break;
                            case HISTORY:
                                cl = HistoryActivity.class;
                                break;
                        }
                        if(cl == null) return;

                        if(selectGroup.equals("")){
                            //グループが選択されていない場合
                            String title = "";
                            String message = getString(R.string.dialog_message_NoSelectGroup);
                            showErrorDialog(title,message);
                            return;
                        }

                        showNextActivity(selectGroup,cl);


                    }
                })
                .setNegativeButton(getString(R.string.dialog_Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //キャンセル処理
                        selectGroup = "";
                    }
                })
                .show();
    }

    //ラジオボタンモード
    enum RAGIOBOTTUN_MODE {
        START,
        HISTORY,
    }
    //endregion

    //region 登録プレイヤー人数をチェック
    private boolean checkPlayerCount(String key){
        //プレイヤーを取得
        String getKey = key + "_"  + accountInfo.PLAYER_KEY;
        String str = preferences.getString(getKey,"");

        if(str.trim().equals("")) return false;

        List<String> playerlist = accountInfo.getListToString(str);
        if(playerlist.size()<2){
            return false;
        }else{
            return true;
        }
    }
    //endregion

    //region エラーダイアログを表示
    private void showErrorDialog(String title,String message){
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){

                    }
                })
                .show();
    }
    //endregion

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            selectGroup = "";
            return true;
        }
        return false;
    }



}
