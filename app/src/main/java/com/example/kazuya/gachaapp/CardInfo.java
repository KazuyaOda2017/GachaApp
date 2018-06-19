package com.example.kazuya.gachaapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kazuya on 2017/08/14.
 */

public class CardInfo extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInFlater;

    //region 出現率
    //ノーマル４２％
    public static float RATE_NOMAL = 4200;
    //レア32%
    public static float RATE_RARE = 3200;
    //SR１７％
    public static float RATE_SR = 1700;
    //SSR6%
    public static float RATE_SSR = 600;
    //UL2.95%
    public static float RATE_UR = 295;
    //LR0.05%
    public static float RATE_LR = 5;
    //endregion

    private String[] cardNameList = {
            "oda_n",
            "nakano_n",
            "shinkawa_n",
            "nakano_r",
            "shinkawa_r",
            "nakahashi_r",
            "tanaka_r",
            "shinkawa_sr",
            "nakahashi_sr",
            "nakahashi_ssr",
            "tanaka_ssr",
            "nakano_ur",
            "tanaka_ur",
            "mitsuhashi"
    };

    private Integer[] cardIDArray = {
            R.drawable.oda_n,
            R.drawable.nakano_n,
            R.drawable.shinkawa_n,
            R.drawable.nakano_r,
            R.drawable.shinkawa_r,
            R.drawable.nakahashi_r,
            R.drawable.tanaka_r,
            R.drawable.shinkawa_sr,
            R.drawable.nakahashi_sr,
            R.drawable.nakahashi_ssr,
            R.drawable.tanaka_ssr,
            R.drawable.nakano_ur,
            R.drawable.tanaka_ur,
            R.drawable.mitsuhashi
    };

    private Integer[] cardIconArray = {
            R.drawable.icon_oda_n,
            R.drawable.icon_nakano_n,
            R.drawable.icon_shinkawa_n,
            R.drawable.icon_nakano_r,
            R.drawable.icon_shinkawa_r,
            R.drawable.icon_nakahashi_r,
            R.drawable.icon_tanaka_r,
            R.drawable.icon_shinkawa_sr,
            R.drawable.icon_nakahashi_sr,
            R.drawable.icon_nakahashi_ssr,
            R.drawable.icon_tanaka_ssr,
            R.drawable.icon_nakano_ur,
            R.drawable.icon_tanaka_ur,
            R.drawable.icon_mitsuhashi
    };

    public CardInfo() {

    }

    public static class ViewHolder{
        public ImageView hueImageView;

    }

    public int maxNum(){
        return cardNameList.length;
    }

    public List<String> getCardNameList(){
        List<String> list = new ArrayList<String>();
        for(int i = 0; i < maxNum(); i++){
            list.add(cardNameList[i]);
        }
        return list;
    }

    public CardInfo(Context context){
        mContext = context;
        mLayoutInFlater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return cardNameList.length;
    }

    @Override
    public Object getItem(int position) {
        return cardNameList[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public Object getCardID(int position){
        return cardIDArray[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = mLayoutInFlater.inflate(R.layout.grid_items,null);
            holder = new ViewHolder();
            holder.hueImageView = ( ImageView)convertView.findViewById(R.id.cardlist_image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.hueImageView.setImageResource(cardIconArray[position]);

        return convertView;
    }
}
