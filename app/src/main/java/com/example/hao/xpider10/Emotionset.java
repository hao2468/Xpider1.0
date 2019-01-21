package com.example.hao.xpider10;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Emotionset extends AppCompatActivity {
    TextView emotiondata;
    RelativeLayout emotionlayout;
    ImageView emotionview;
    ImageButton ok;
    ConnectThread conT;
    private int emotionX = 0;
    private int emotionY = 0;
    private String intemX;
    private String intemY;
    int[] location = new int[2];
    byte[] senddata;
    byte[] flagdata = new byte[1];
    byte[] dataX;
    byte[] dataY;
    Application app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emotionset);
        app = (Application)getApplication();
        conT = app.getconT();
        /*if (conT != null){
            conT.send("x\r".getBytes());
            boolean emotionflag = false;
            while(!emotionflag){
                if (conT.getEmotionX() != 999)
                {
                    emotionflag = true;
                }
                conT.send("x\r".getBytes());
            }
            conT.send("y\r".getBytes());
            emotionflag = false;
            while(!emotionflag){
                conT.send("y\r".getBytes());
                if (conT.getEmotionY() != 999)
                {
                    emotionflag = true;
                }
            }
            emotionX = conT.getEmotionX();
            emotionY = conT.getEmotionY();
        }*/
        emotiondata = (TextView)findViewById(R.id.emotiondata);
        emotionlayout = (RelativeLayout)findViewById(R.id.emotionlayout);
        emotionview = (ImageView)findViewById(R.id.emotionView);
        emotiondata.setText("("+String.valueOf(emotionX)+","+String.valueOf(emotionY)+")");
        ok = (ImageButton)findViewById(R.id.emotionok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Senddata();
                /*Intent intent = new Intent(Emotionset.this,Play.class);
                startActivity(intent);*/
            }
        });
        emotionview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        emotionX = (int)(event.getX()-emotionview.getWidth()/2);
                        emotionY = (int)(emotionview.getHeight()/2-event.getY());
                        emotiondata.setText("("+emotionX+","+emotionY+")");
                        app.setEmotionX(emotionX);
                        app.setEmotionY(emotionY);
                        break;
                    case MotionEvent.ACTION_DOWN:
                        emotionX = (int)(event.getX()-emotionview.getWidth()/2);
                        emotionY = (int)(emotionview.getHeight()/2-event.getY());
                        emotiondata.setText("("+emotionX+","+emotionY+")");
                        app.setEmotionX(emotionX);
                        app.setEmotionY(emotionY);
                        break;
                }
                return true;
            }
        });
    }
    //发送感情坐标
    public void Senddata(){
        if (conT != null) {
            if (conT.socket != null) {
                if (conT.socket.isConnected()) {
                    dataX = new byte[6];
                    dataX[0] = 'X';
                    dataX[5] = '\r';
                    if (emotionX != 0) {
                        if (emotionX > 0) {
                            dataX[1] = '0';
                        }
                        if (emotionX < 0) {
                            dataX[1] = '-';
                        }
                        dataX[2] = (byte) (Math.abs(emotionX) / 100 + 48);
                        dataX[3] = (byte) (Math.abs(emotionX) / 10 - Math.abs(emotionX) / 100 * 10 + 48);
                        dataX[4] = (byte) (Math.abs(emotionX) - Math.abs(emotionX) / 10 * 10 + 48);
                    }
                    conT.send(dataX);
                    try {
                        Thread.sleep(200);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    dataY = new byte[6];
                    dataY[0] = 'Y';
                    dataY[5] = '\r';
                    if (emotionY != 0) {
                        if (emotionY > 0) {
                            dataY[1] = '0';
                        }
                        if (emotionY < 0) {
                            dataY[1] = '-';
                        }
                        dataY[2] = (byte) (Math.abs(emotionY) / 100 + 48);
                        dataY[3] = (byte) (Math.abs(emotionY) / 10 - Math.abs(emotionY) / 100 * 10 + 48);
                        dataY[4] = (byte) (Math.abs(emotionY) - Math.abs(emotionY) / 10 * 10 + 48);
                    }
                    conT.send(dataY);

                    /*intemX = String.valueOf((int) (emotionX * 10));
                    intemY = String.valueOf((int) (emotionY * 10));
                    dataX = intemX.getBytes();
                    flagdata[0] = 120;
                    senddata = new byte[flagdata.length + dataX.length];
                    System.arraycopy(flagdata, 0, senddata, 0, 1);
                    System.arraycopy(dataX, 0, senddata, 1, dataX.length);
                    conT.send(senddata);
                    senddata = null;

                    dataY = intemY.getBytes();
                    flagdata[0] = 121;
                    senddata = new byte[flagdata.length + dataY.length];
                    System.arraycopy(flagdata, 0, senddata, 0, 1);
                    System.arraycopy(dataY, 0, senddata, 1, dataY.length);
                    conT.send(senddata);
                    senddata = null;*/
                }
            }
        }
    }
}
