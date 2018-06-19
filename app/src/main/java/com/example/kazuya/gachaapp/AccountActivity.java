package com.example.kazuya.gachaapp;

import android.app.*;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.inputType;
import static android.text.InputType.TYPE_CLASS_TEXT;

/**
 * Created by Kazuya on 2017/08/24.
 */

public class AccountActivity extends Activity implements AdapterView.OnItemClickListener {

    //ユーザーリスト
    List<String> userslist = new ArrayList<String>();
    SharedPreferences preferences;
    ListView listView;
    AccountInfo accountInfo;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.acount_layout);

        //リストビューを取得
        listView = (ListView)findViewById(R.id.listview);
        listView.setOnItemClickListener(this);

        //アカウントデータ読み込み
        String users;
        accountInfo = new AccountInfo();

        try{
             preferences = getSharedPreferences(AccountInfo.PREF_NAME_ACCOUNT,MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
            users = preferences.getString(AccountInfo.USER_KEY,"");
            //users = "";
        }catch (Exception e) {
            users = "";
        }


        //読み込んだ情報でリストビューを作成する
        if(users.trim() != ""){
            userslist = accountInfo.getListToString(users);

            creatListView(userslist);

        }
  //新規登録ボタンを設定
        Button registBtn = (Button)findViewById(R.id.regist_button);
        registBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //グループ数上限を設定
                if(userslist.size() == Const.GROUP_COUNT_MAX){
                    //エラーメッセージの表示
                    errordialogShow(getString(R.string.dialog_title_error),getString(R.string.dialog_message_GroupCountIsMax));
                    return;
                }

                //ダイアログを表示
                showDialog();
            }
        });


    }

    //region 登録ダイアログを表示する
    private void showDialog(){
        //テキストを受け取るViewを設定
        final EditText editText = new EditText(this);
        //改行を禁止
        editText.setInputType(TYPE_CLASS_TEXT);

        //ダイアログを設定
        new AlertDialog.Builder(this)
                .setIcon(null)
                .setTitle(getString(R.string.dialog_title_registGroup))
                .setView(editText)
                .setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //OKボタン押下処理

                        String text = editText.getText().toString();

                        //入力チェック
                        if(text.equals("") || text.trim().equals("")){
                            return;
                        }
                        //同名グループの登録を禁止
                        if(accountInfo.checkRegistInfo(userslist,text)){
                            //エラーダイアログを表示
                            errordialogShow(getString(R.string.dialog_title_writeError),getString(R.string.dialog_message_CanNotSameNameGroup));
                            return;
                        }

                        //前後の空白を削除して登録
                        registUser(text.trim());
                        creatListView(userslist);
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

    //region エラーダイアログを表示
    private void errordialogShow(String title,String messege){
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(messege)
                .setPositiveButton(getString(R.string.dialog_OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }
    //endregion

    //region ユーザー登録
    private void registUser(String user){

        //リストに追加
        userslist.add(user);
        //カンマ区切りでユーザーを取得
        String users = accountInfo.createStringByStringList(userslist);

       // preferences = getSharedPreferences("account",MODE_WORLD_READABLE|MODE_WORLD_WRITEABLE);
        SharedPreferences.Editor e = preferences.edit();
        e.putString(AccountInfo.USER_KEY,users);
        e.commit();
    }
    //endregion

    //region ListViewを作成する
    private void creatListView(List<String> list){

        if(list.size() == 0) return;

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.userlist, list);
        listView.setAdapter(arrayAdapter);
    }
    //endregion

    //region ListView Item クリック処理
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
        //画面遷移先を取得
        Intent intent = new Intent(getApplication(), UserConfigActivity.class);

        //遷移先に渡す情報を作成
        String str = userslist.get(position);

        intent.putExtra("Key", str);

        startActivity(intent);
    }
    //endregion

}
