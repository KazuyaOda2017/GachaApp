package com.example.kazuya.gachaapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private int num = 0;
    private GameInfo gameInfo;
    private Spinner spinner;
    private List<String> spinnerItems;
    private boolean playerSelectFlag;
    private String spinner_message;
    private boolean gachaFinishFlag;
    private TextView exceptionText;
    private String exception_text;
    private int count_gachaFinishFlag;
    private Button gachaBtn;

    static final int RESULT_REQUEST = 1000;


    public MainActivity(){
        gameInfo = new GameInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //ゲーム情報の取得
        gameInfo = (GameInfo)getIntent().getSerializableExtra("GameInfo");

        //プレイヤー選択文字列取得
        spinner_message = getString(R.string.text_select_player);

        //警告文テキストの設定
        exceptionText = (TextView)findViewById(R.id.exception_text_gacha);

        //プルダウンメニューを作成
        spinner = (Spinner)findViewById(R.id.spinner);
        //プルダウンアイテムの作成
        SetSpinnerItems();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //プルダウンをリスナーに登録
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)spinner.getSelectedItem();

                //プレイヤー選択のチェック
                if(item.equals(spinner_message)){
                    playerSelectFlag = false;
                    exceptionText.setVisibility(View.INVISIBLE);
                }else{
                    playerSelectFlag = true;
                }

                //ガチャフラグの更新
                for(PlaylerInfo playlerInfo: gameInfo.getPlayerInfoList()){
                    if(playlerInfo.getPlayerName().equals(item)){
                        playlerInfo.setGachaFlag(true);
                        //ガチャフラグの取得
                        gachaFinishFlag = playlerInfo.getGachaFinishFlag();
                    }else{
                        playlerInfo.setGachaFlag(false);
                    }
                }

                //ガチャ済みフラグチェック
                if(gachaFinishFlag){
                    exception_text = getString(R.string.text_gachaFinish);
                    exceptionText.setText(exception_text);
                    exceptionText.setVisibility(View.VISIBLE);
                }else{
                    exceptionText.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //ボタン設定
        gachaBtn = (Button)findViewById(R.id.gacha_btn);

        //リスナーをボタンに登録
        gachaBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //ボタンクリック処理

                //全員ガチャ済みかどうか
                if(!(count_gachaFinishFlag == gameInfo.getPlayerNum())){
                    //プレイヤー選択のチェック
                    if(!playerSelectFlag){
                        exception_text = getString(R.string.text_select_error);
                        exceptionText.setText(exception_text);
                        exceptionText.setVisibility(View.VISIBLE);
                        return;
                    }
                    //ガチャ済みフラグのチェック
                    if(gachaFinishFlag){
                        return;
                    }


                    //画面移行情報設定
                    Intent intent = new Intent(getApplication(),Result.class);
                    intent.putExtra("GameInfo",gameInfo);
                    int requestCode = RESULT_REQUEST;
                    startActivityForResult(intent, requestCode );
                }
                else{
                    //集計結果画面へ移行
                    Intent intent = new Intent(getApplication(), Aggregate.class);
                    intent.putExtra("GameInfo",gameInfo);
                    startActivity(intent);
                    MainActivity.this.finish();//この画面を終了する

                }

            }
        });
    }

    //終了ダイアログの表示
    private void ShowDialog(){

        //final boolean result = false;

        new AlertDialog.Builder(this).setTitle("設定画面に戻る")
                .setMessage("ゲームをリセットして設定画面に戻ります。よろしいですか？")
                .setPositiveButton("YES", new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface daialog, int which){

                        MainActivity.this.finish();
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


    //プルダウンメニューアイテムの作成
    private void SetSpinnerItems(){
        //初期化
        spinnerItems = new ArrayList<String>();

        //最初は定型文をセット
        spinnerItems.add(spinner_message);

        for(PlaylerInfo playlerInfo: gameInfo.getPlayerInfoList()){
            spinnerItems.add(playlerInfo.getPlayerName());
        }
    }

    //Resultから返しの結果を受け取る
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == RESULT_OK && requestCode == RESULT_REQUEST && null != intent){
            gameInfo = (GameInfo)intent.getSerializableExtra("ResultInfo") ;

            //プルダウンメニューを一番上に設定
            spinner.setSelection(0);

            //ガチャ済みフラグ数をカウントする
            count_gachaFinishFlag = CountGachaFinishFlag();
            if(gameInfo.getLRFlag() ||count_gachaFinishFlag == gameInfo.getPlayerNum()){
                //ガチャボタンテキストを再設定
                String str = getString(R.string.result_btn);
                gachaBtn.setText(str);

                //プルダウンメニューを非表示にする
                spinner.setVisibility(View.INVISIBLE);
            }
        }
    }

    //ガチャ済みカウントメソッド
    private int CountGachaFinishFlag(){
        int count = 0;

        for(PlaylerInfo p:gameInfo.getPlayerInfoList()){
            if(p.getGachaFinishFlag()){
                count++;
            }
        }

        return count;
    }


}
