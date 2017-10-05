package com.example.miyazakikazuki.mytoratora;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity {
    final int JANKEN_GU = 0;
    final int JANKEN_CHOKI = 1;
    final int JANKEN_PA = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        int myHand = 0;
        Intent intent = getIntent();
        int id = intent.getIntExtra("MY_HAND",0);


        //自分お出した手を出力する
        ImageView myHandImageView = (ImageView) findViewById(R.id.win);
        switch (id){
            case R.id.gu:
                myHandImageView.setImageResource(R.drawable.s_samurai);
                myHand = JANKEN_GU;
                break;
            case R.id.choki:
                myHandImageView.setImageResource(R.drawable.s_mig);
                myHand = JANKEN_CHOKI;
                break;
            case R.id.pa:
                myHandImageView.setImageResource(R.drawable.s_baba);
                myHand = JANKEN_PA;
                break;
            default:
                myHand = JANKEN_GU;
                break;

        }

        //コンピュータの手を決める
        int comHand = getHand();
        ImageView comHandImageView =
                (ImageView)findViewById(R.id.lose);
        switch (comHand){
            case JANKEN_GU:
                comHandImageView.setImageResource(R.drawable.s_samurai);
                break;
            case JANKEN_CHOKI:
                comHandImageView.setImageResource(R.drawable.s_mig);
                break;
            case JANKEN_PA:
                comHandImageView.setImageResource(R.drawable.s_baba);
                break;
        }

        //勝敗を判定する
        TextView resultLabel = (TextView)findViewById(R.id.result_text);
        int gameResult = (comHand - myHand + 3) % 3;
        switch (gameResult){
            //引き分け
            case 0:
                resultLabel.setText(R.string.result_draw);
                break;
            //勝利
            case 1:
                resultLabel.setText(R.string.result_win);
                break;
            //敗北
            case 2:
                resultLabel.setText(R.string.result_lose);
                break;
        }
        // じゃんけんの結果を保存
        saveData(myHand,comHand,gameResult);


    }
    public void onBackButtonTapped(View view){
        finish();
    }
    private void saveData(int myHand,int comHand,int gameResult){
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = pref.edit();

        int gameCount = pref.getInt("Game_COUNT",0);
        int winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0);
        int lastComHand = pref.getInt("LAST_COM_HAND",0);
        int lastGameResult = pref.getInt("GAME_RESULT",-1);
        editor.putInt("GAME_COUNT",gameCount + 1);
        if(lastGameResult == 2 && gameResult ==2){
            // コンピュータが連勝した場合
            editor.putInt("WINNING_STREAK_COUNT",winningStreakCount +1);
        }else{
            editor.putInt("WINNING_STREAK_COUNT",0);
        }
        editor.putInt("LAST_MY_HAND",myHand);
        editor.putInt("LAST_COM_HAND",comHand);
        editor.putInt("BEFORE_LAST_COM_HAND",lastComHand);
        editor.putInt("GAME_RESULT",gameResult);

        editor.commit();
    }

    private  int getHand(){
        int hand = (int)(Math.random() * 3);
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        int gameCount = pref.getInt("GAME_COUNT",0);
        int winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0);
        int lastMyHand = pref.getInt("LAST_MY_HAND",0);
        int lastComHand = pref.getInt("LAST_COM_HAND",0);
        int beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND",0);
        int gameResult = pref.getInt("GAME_RESULT",-1);

        if(gameCount == 1){
            if(gameResult == 2){
                // 前回の勝負が１回目で、コンピュータが勝った場合、
                // コンピュータが次に出す手を変える
                while (lastComHand == hand){
                    hand = (int)(Math.random() * 3);
                }
            }else if(gameResult == 1){
                // 前回の勝負が１回目で、コンピュータ負けた場合
                // 相手の出した手に勝つ手を出す
                hand = (lastMyHand - 1 + 3)% 3;
            }
        }else if(winningStreakCount > 0){
            if(beforeLastComHand == lastComHand){
                //同じ手で連勝した場合は手を変える
                while (lastComHand == hand){
                    hand = (int)(Math.random() * 3);
                }
            }
        }
        return hand;
    }
}
