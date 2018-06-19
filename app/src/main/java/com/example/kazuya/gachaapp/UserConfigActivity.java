package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by Kazuya on 2017/08/25.
 */


public class UserConfigActivity extends Activity {

    private String key;
    private String player_key;
    private AccountInfo accountInfo;
    SharedPreferences preferences;
    private List<String> playerslist = new ArrayList<String>();
    ViewGroup linearLayout_playerlist;
    Context context;
    Handler handler = new Handler();
    private List<String> removeList = new ArrayList<String>();

    private SharedPreferences gameInfoPre;


    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.userconfig_activity);

        context = this;

        //データの受け取り
        Intent intent = getIntent();
        key = intent.getStringExtra("Key");

        //preference読み込み
        accountInfo = new AccountInfo();

        String players;
        try{
            preferences = getSharedPreferences(AccountInfo.PREF_NAME_ACCOUNT,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
            //キーの作成
            player_key = key + "_" + AccountInfo.PLAYER_KEY;
            players = preferences.getString(player_key,"");

            //ゲーム情報プレファレンスを取得
            gameInfoPre = getSharedPreferences(AccountInfo.PREF_NAME_GAME,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);

            //users = "";
        }catch (Exception e) {
            players = "";
        }
        //プレイヤーリストの作成
        if(!players.trim().equals("")){
            playerslist = accountInfo.getListToString(players);
        }

        //LayoutをViewGropで取得
        linearLayout_playerlist = (ViewGroup) findViewById(R.id.linearlayout_user);

        //プレイヤーリストの表示
        setPlayerslist();

        //登録ボタンを設定
        Button registBtn = (Button)findViewById(R.id.save_button);
        registBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                savePlayers();

                //トーストを表示
                Toast.makeText(UserConfigActivity.this, getString(R.string.text_save_config), LENGTH_SHORT).show();

                //画面を終了
                finish();
            }
        });
    }

    //region プレイヤーリストを表示する
    private void setPlayerslist(){

        //初期化
        linearLayout_playerlist.removeAllViews();

        int row = 0;

        for(String player:playerslist){
            addPlayer(row,player,addMode.PLAYER,linearLayout_playerlist);
            row ++;
        }
        addPlayer(row,"",addMode.NEW_LINE,linearLayout_playerlist);

    }
    //endregion

    //region レイアウトの追加
    private void addPlayer(int row,String player,addMode mode,ViewGroup vg){

        //プレイヤー追加用レイアウトの追加
        getLayoutInflater().inflate(R.layout.playeradd_layout,vg);
        final LinearLayout layout = (LinearLayout)linearLayout_playerlist.getChildAt(row);

        //ImageButtonの取得
        ImageButton btn = (ImageButton)(layout.findViewById(R.id.pm_button));
                //TextViewの取得
        final TextView textView = (TextView)(layout.findViewById(R.id.player_text));

        if(mode == addMode.PLAYER){
            //アイコンの変更
            btn.setImageResource(R.drawable.icon_minus);
            //タグを設定
            btn.setTag(player);
            //プレイヤー名の表示
            textView.setText(player);

        }else{
            //タグを設定
            btn.setTag(iconType.PLUS);
        }

        //ボタンにリスナーを登録
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                iconType type = iconType.MINUS;
                String player = null;

                try{
                   type = (iconType)v.getTag();

                }catch (Exception e){

                    player = (String)v.getTag();

                }

                switch (type){
                    case PLUS:
                        //テキスト入力ダイアログを表示
                        showDialog();

                        break;
                    case MINUS:
                        //確認ダイアログを表示
                        showConfirmationDialog(player);

                        break;
                }
            }
        });
    }

    enum iconType{
        PLUS,
        MINUS,
    }

    enum addMode{
        PLAYER,
        NEW_LINE,
    }
    //endregion

    //region 登録ダイアログを表示する
    private void showDialog(){
        //テキストを受け取るViewを設定
        final EditText editText = new EditText(this);
        //改行を禁止
        editText.setInputType(TYPE_CLASS_TEXT);

        //ダイアログを設定
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle(getString(R.string.dialog_title_registPlayer))
                .setView(editText)
                .setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //OKボタン押下処理

                        String text = editText.getText().toString();

                        //入力チェック
                        if(text.equals("") || text.trim().equals("")){
                            return;
                        }
                        //同名プレイヤーの禁止
                        if(accountInfo.checkRegistInfo(playerslist,text)){
                            //エラーダイアログを表示
                            errorDialogShow(getString(R.string.dialog_title_writeError),getString(R.string.dialog_message_CanNotSameNamePlayer));
                            return;
                        }

                        //プレイヤーリストに前後の空白を削除して追加する
                        playerslist.add(text.trim());
                        //画面を更新する
                        setPlayerslist();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //キャンセル処理
                    }
                })
                .show();
    }
    //endregion

    //region エラーダイアログを表示
    private void errorDialogShow(String title,String message){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    //endregion

    //region 削除確認ダイアログを表示
    private void showConfirmationDialog(String player){

        final String removePlayer = player;

        //ダイアログを設定
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle(getString(R.string.dialog_title_deletePlayer))
                .setMessage("このプレイヤーを削除しますか？")
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //OKボタン押下処理
                        if(removePlayer != null){
                            //リストから削除
                            playerslist.remove(removePlayer);
                            //削除リストに追加
                            addRemoveList(removePlayer);
                            //画面を再表示
                            setPlayerslist();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //キャンセル処理
                    }
                })
                .show();
    }
    //endregion

    //region プレイヤー情報の登録
    private void savePlayers(){

        //削除リストのプレイヤー情報を削除する
        removePlayers();

        //登録文字列の作成
        String str = accountInfo.createStringByStringList(playerslist);

        //プリファレンスに保存
        SharedPreferences.Editor e = preferences.edit();
        e.putString(player_key,str);
        e.commit();
    }
    //endregion

    //region 削除リストに追加
    private void addRemoveList(String removePlayer){
        try{
            String players = preferences.getString(player_key,"");

            if(players.equals("")) return;

            //削除リストに追加済みか確認
            for(String str:removeList){
                if(str.equals(removePlayer)){
                    return;
                }
            }

            List<String> playerList = accountInfo.getListToString(players);
            for(String player:playerList){
                if(player.equals(removePlayer)){
                    //削除リストに追加
                    removeList.add(removePlayer);
                }
            }


        }catch (Exception e){

        }
    }
    //endregion

    //region プレイヤー情報の削除
    private void removePlayers(){

        if(removeList.size() == 0){
            return;
        }

        try{
            String group_key = accountInfo.creatGameInfoKeyByGroup(this.key);
            SharedPreferences.Editor editor = gameInfoPre.edit();
            for(String player:removeList){
                String pay_key = accountInfo.creatKeyByGroupKeyWithPlayer(group_key,player,AccountInfo.PAY_KEY);
                String round_key = accountInfo.creatKeyByGroupKeyWithPlayer(group_key,player,AccountInfo.ROUND_KEY);
                editor.remove(pay_key);
                editor.remove(round_key);
            }
            editor.commit();
        }catch (Exception e){
            new AlertDialog.Builder(this).setMessage("削除エラー").show();
        }

    }
    //endregion

    //region 戻るボタン処理
    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            ShowDialog();
            return true;
        }
        return false;
    }

    //終了ダイアログの表示
    private void ShowDialog(){

        //final boolean result = false;

        new AlertDialog.Builder(this).setTitle("グループ設定画面に戻る")
                .setMessage("設定を保存してない場合、変更内容は破棄されます。")
                .setPositiveButton("OK", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface daialog, int which){

                        UserConfigActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog,int which){

                    }
                }).show();
    }
    //endregion


}
