package com.example.kazuya.gachaapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.concurrent.CountDownLatch;

import static android.text.InputType.TYPE_CLASS_TEXT;

/**
 * Created by Kazuya on 2017/08/26.
 */

public class Dialog extends android.app.Dialog  {

    //region 変数
    public static String INPUT_TEXT;

    public Dialog(@NonNull Context context) {
        super(context);
    }
    //endregion

    //region プロパティ
    public static AlertDialog.Builder defaultDialog;

    //エラーダイアログ
    public static AlertDialog.Builder ERROR_DIALOG;

    //Viewを保持するダイアログ
    public static AlertDialog.Builder INSERT_EDITTEXT_DIALOG;

    //endregion

    //region ダイアログ作成メソッド
    //エラーダイアログ
    public static void errorDiarogShow(String title,String messege,String positiveBtn,Context context){
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(messege)
                .setPositiveButton(positiveBtn, new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    //Viewダイアログ
    public static void creatEditTextDialog(String title,Context context){

    }
    //endregion



    public static void creatDefaultDialog(Context context){
        defaultDialog = new AlertDialog.Builder(context)
                .setIcon(null)
                .setTitle("Defoult");

    }



    public static final void showDialogWaitDismiss(final Handler handler, final AlertDialog.Builder dialog, final OnDismissListener dismissListener){
        final CountDownLatch signal = new CountDownLatch(1);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener(){

            @Override
            public void onDismiss(DialogInterface dialog) {
                try{
                    if(dismissListener != null){
                        dismissListener.onDismiss(dialog);
                    }
                }finally {
                    signal.countDown();
                }
            }
        });
        if(Thread.currentThread() != handler.getLooper().getThread()){
            handler.post(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
            try{
                signal.await();
            }catch (InterruptedException e){

            }
        }else{
            dialog.show();
        }
    }

    //region テキスト入力ダイアログを表示する
    public static RESULT showEditTextDialog(String title,Context context,Handler handler){
        //テキストを受け取るViewを設定
        final EditText editText = new EditText(context);
        //改行を禁止
        editText.setInputType(TYPE_CLASS_TEXT);

        final RESULT[] result = new RESULT[1];

        //ダイアログを設定
        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                .setIcon(null)
                .setTitle(title)
                .setView(editText)
                .setPositiveButton("OK", new OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        //OKボタン押下処理

                        String text = editText.getText().toString();

                        if(text.equals("") || text.trim().equals("")){
                            result[0] = RESULT.ERROR;
                            return;
                        }

                        INPUT_TEXT = text;
                        result[0] = RESULT.OK;

                    }
                })
                .setNegativeButton("Cancel", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //キャンセル処理
                        result[0] = RESULT.CANCEL;
                    }
                });

        showDialogWaitDismiss(handler, dialog, new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });


        return result[0];
    }


    //endregion

    //region ダイアログリザルト
    enum RESULT{
        OK,
        CANCEL,
        ERROR,
    }
    //endregion
}
