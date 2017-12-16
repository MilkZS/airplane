package com.example.milkdz.airplane;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private int tableWidth; // 桌面的宽度
    private int tableHeight; // 高度

    private int racketY; //球拍的垂直位置

    //球拍的宽度和高度
    private final int RACHET_HEIGHT = 30;
    private final int RACKET_WIDTH = 90;

    private int BALL_SIZE = 16;// 小球的大小

    //返回一个-0.5 - 0.5 的比率，控制小球的方向
    private int ySpeed = 15;
    Random rand = new Random();
    private double xyRate = rand.nextDouble() - 0.5;
    private int xSpeed = (int) (ySpeed * xyRate * 2);

    //小球坐标
    private int ballX = rand.nextInt(200) + 20;
    private int ballY = rand.nextInt(10) + 20;

    private int racketX = 50;//rand.nextInt(200); // 球拍的坐标

    private boolean isLose = false; // 游戏是否结束

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉窗口标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏显示
        final GameView gameView = new GameView(this);



        //获取窗口管理器
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        ///获取屏幕的宽和高
        tableHeight = metrics.heightPixels;
        tableWidth = metrics.widthPixels;
        racketY = tableHeight - 500;
        setContentView(gameView);
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x123){
                    gameView.invalidate();
                }
            }
        };

        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 撞墙或球拍反弹球
                if (ballX <= 0 || ballX >= tableWidth - BALL_SIZE){
                    xSpeed = -xSpeed;
                }
                // game over
                if(ballY >= racketY - BALL_SIZE && (ballX < racketX || ballX > racketX + RACKET_WIDTH)){
                    timer.cancel();
                    isLose = true;
                    return;
                }
                if(ballY <= 0 || (ballY >= racketY - BALL_SIZE && ballX > racketX && ballX <= racketX + RACKET_WIDTH)){
                    ySpeed = -ySpeed;
                }

                ballX += xSpeed;
                ballY += ySpeed;

                handler.sendEmptyMessage(0x123);
            }
        },0,100);




    }

    class GameView extends View{

        Paint paint = new Paint();

        public GameView(Context context) {
            super(context);
            setFocusable(true);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            super.onTouchEvent(event);
            racketX = (int) event.getX();

            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true); // 去除锯齿
            if (isLose){
                paint.setColor(Color.RED);
                paint.setTextSize(40);
                canvas.drawText("Game over", tableWidth / 2 - 100,200,paint);
            }else{
                paint.setColor(Color.rgb(255,0,0));
                canvas.drawCircle(ballX,ballY,BALL_SIZE,paint); // 绘制小球
                paint.setColor(Color.rgb(255,0,0));
                canvas.drawRect(racketX,racketY,racketX + RACKET_WIDTH,racketY + RACHET_HEIGHT,paint); //绘制球拍
                Log.d("racketX = ","" +racketX);
                Log.d("racketY = ","" +racketY);

            }
        }
    }
}
