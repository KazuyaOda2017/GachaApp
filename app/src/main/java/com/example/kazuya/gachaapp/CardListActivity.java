package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kazuya on 2017/08/14.
 */

public class CardListActivity extends Activity {

    //カードリスト
    private List<String> cardList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardlist_activity);

        //カード出現率を表示
        TextView textView_n = (TextView)findViewById(R.id.tablerow_nomal_text);
        textView_n.setText(getRateToString(CardInfo.RATE_NOMAL));

        TextView textView_r = (TextView)findViewById(R.id.tablerow_rare_text);
        textView_r.setText(getRateToString(CardInfo.RATE_RARE));

        TextView textView_sr = (TextView)findViewById(R.id.tablerow_sr_text);
        textView_sr.setText(getRateToString(CardInfo.RATE_SR));

        TextView textView_ssr = (TextView)findViewById(R.id.tablerow_ssr_text);
        textView_ssr.setText(getRateToString(CardInfo.RATE_SSR));

        TextView textView_ur = (TextView)findViewById(R.id.tablerow_ur_text);
        textView_ur.setText(getRateToString(CardInfo.RATE_UR));

        TextView textView_lr = (TextView)findViewById(R.id.tablerow_lr_text);
        textView_lr.setText(getRateToString(CardInfo.RATE_LR));

       GridView gridView = (GridView)findViewById(R.id.gridView);
        gridView.setAdapter(new CardInfo(this));
        //GridViewのタッチ処理を実装
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView parent,View view, int position,long id){
                ShowDialogImage(position);
            }
        });

        //戻るボタンを設定
        Button btn = (Button)findViewById(R.id.back_btn);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });
    }

    //画像を拡大表示する
    private void ShowDialogImage(int position){
        //カード画像を取得する
        ImageView imageView = new ImageView(CardListActivity.this);
        //Bitmap bmp = ((BitmapDrawable)tapView.getDrawable()).getBitmap();
        CardInfo cInfo = new CardInfo();
        imageView.setImageResource((Integer) cInfo.getCardID(position));

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(Integer.parseInt(String.valueOf(R.color.steelblue)));

        int imageWidth = 1000;
        int imageHight = 1200;

        //ImageViewをレイアウトに張り付け
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageWidth,imageHight);
        imageView.setLayoutParams(params);
        layout.addView(imageView);

        //ディスプレイ幅を取得
        Display disp = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        int width = size.x;

        float factor = width / (float)imageWidth;
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        //ダイアログを作成する
        Dialog dialog = new Dialog(CardListActivity.this);
        //タイトルを非表示にする
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setLayout((int)(imageWidth*factor),(int)(imageHight*factor));

        dialog.show();
    }

    private String getRateToString(float num){
        String str = String.valueOf(num/100);
        str += "%";
        return  str;
    }


}


