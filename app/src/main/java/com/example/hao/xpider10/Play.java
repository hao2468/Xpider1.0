package com.example.hao.xpider10;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.sdk.DisplayView;
import com.demo.sdk.Enums;
import com.demo.sdk.Module;
import com.demo.sdk.Player;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.hao.xpider10.PermisionUtils.verifyStoragePermissions;


public class Play extends AppCompatActivity implements View.OnClickListener,View.OnTouchListener{
    private Module _module;
    private String _moduleIp="192.168.100.1";
    private Player _player;
    private DisplayView _displayView;
    private int _viewWidth;
    private int _viewHeight;
    private Enums.Pipe _pipe = Enums.Pipe.H264_PRIMARY;
    private byte[] bqian = "w\r".getBytes();
    private byte[] bzuo = "a\r".getBytes();
    private byte[] byou = "d\r".getBytes();
    private byte[] bhou = "s\r".getBytes();
    private byte[] bstop = "o\r".getBytes();
    private byte[] bdiscon = "6\r".getBytes();
    private byte[] onl = "lon\r".getBytes();
    private byte[] offl = "loff\r".getBytes();
    private byte[] rt = "ok\r".getBytes();
    private byte[] bup = "M\r".getBytes();
    private byte[] bdown = "N\r".getBytes();
    private byte byteqian = 'W';
    private byte bytezuo = 'L';
    private byte byteyou = 'R';

    private int three = 0;
    private boolean testflag = false;

    SharedPreferences savedata;
    SharedPreferences.Editor dataeditor;
    int lowh = 103;
    int lows = 154;
    int lowv = 125;
    int highh = 206;

    private List<String>plandata = new ArrayList<>();
    int plandataflag = 0;

    private TextView text;

    private  boolean flaglight = false;
    private  boolean _recording = false;

    static private BluetoothSocket socket;

    private TextView playstate;
    private Handler handler;
    ImageButton qian;
    ImageButton zuo;
    ImageButton you;
    ImageButton play;
    ImageButton start;
    ImageButton take;
    ImageButton record;
    ImageButton checkviews;
    ImageButton light;
    ImageButton auto;
    ImageButton hou;
    ImageButton up,down;
    ImageButton set;

    //Button test;

    private int tan;
    private int tannow = 0;

    BluetoothAdapter ba;
    BluetoothDevice device;

    private int modeju = 2222;
    private int modeyuan = 1111;

    private boolean threadflag = false;
    private int followflag = 0;
    private int flagju = 0;
    private int flagyuan = 0;
    private double xdistance = 0;
    private double ydistance = 0;
    private byte speed = 0;
    private double xhalfsrc;
    private double yhalfsrc;
    private Mythread mythread;
    private static List<Bitmap> bitmaplist= new ArrayList<Bitmap>();
    static boolean ok = false;
    private boolean planflag = false;
    private static planthread pt;

    private int lines[] = new int[5];
    //private int re = 0;
    private int ultgo;

    ImageButton shibie;
    SurfaceView sfv;

    Application app;
    ConnectThread conT;
    StartplanT st;
    boolean stflag = false;
    Autodialog autodialog;

    //handlethread mhandlethread;

    private static String xVideo="/XpiderVideo";
    private static String xVideo_Photo="/XpiderVideo/Photo";
    private static String xVideo_Video="/XpiderVideo/Video";
    private String photopath;
    private File X;
    private File Fphoto;
    private File Fvideo;
    private  FileOutputStream fos;


    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        ba = BluetoothAdapter.getDefaultAdapter();
        if(!ba.isEnabled())
        {
            ba.enable();
        }
        device = ba.getRemoteDevice("00:18:E5:03:90:C6");//"00:18:E5:03:90:C6"    mx3 "22:22:0A:E9:34:74"   "20:17:05:02:93:64"
        createdir();
        savedata = Play.this.getPreferences(MODE_PRIVATE);
        dataeditor = savedata.edit();
        datainit();

        app = (Application)getApplication();
        conT = app.getconT();
        text = (TextView)findViewById(R.id.text);
        playstate = (TextView)findViewById(R.id.playstate);
        _displayView = (DisplayView)findViewById(R.id.sview);
        qian = (ImageButton)findViewById(R.id.qian);
        zuo = (ImageButton)findViewById(R.id.zuo);
        you = (ImageButton)findViewById(R.id.you);
        hou = (ImageButton)findViewById(R.id.hou);
        play = (ImageButton)findViewById(R.id.open);
        start = (ImageButton)findViewById(R.id.start);
        take = (ImageButton)findViewById(R.id.take);
        record = (ImageButton)findViewById(R.id.record);
        checkviews = (ImageButton)findViewById(R.id.views);
        light = (ImageButton)findViewById(R.id.light);
        auto = (ImageButton)findViewById(R.id.auto);
        shibie = (ImageButton)findViewById(R.id.shibie);
        up = (ImageButton)findViewById(R.id.up);
        down = (ImageButton)findViewById(R.id.down);
        set = (ImageButton)findViewById(R.id.set);

       // test = (Button)findViewById(R.id.test);

        up.setOnClickListener(this);
        down.setOnClickListener(this);

        DisplayMetrics metric = new DisplayMetrics();
        _viewWidth = metric.widthPixels;
        _viewHeight = metric.heightPixels;

        qian.setOnTouchListener(this);
        zuo.setOnTouchListener(this);
        you.setOnTouchListener(this);
        hou.setOnTouchListener(this);

        if(conT != null) {
            if (conT.socket != null) {
                if (conT.socket.isConnected()) {
                    start.setImageResource(R.drawable.c2);
                    qian.setClickable(true);
                    hou.setClickable(true);
                    zuo.setClickable(true);
                    you.setClickable(true);

                }else {
                    qian.setClickable(false);
                    hou.setClickable(false);
                    zuo.setClickable(false);
                    you.setClickable(false);
                }
            }else {
                qian.setClickable(false);
                hou.setClickable(false);
                zuo.setClickable(false);
                you.setClickable(false);
            }
        }else {
            qian.setClickable(false);
            hou.setClickable(false);
            zuo.setClickable(false);
            you.setClickable(false);
        }


        checkviews.setOnClickListener(this);
        play.setOnClickListener(this);
        start.setOnClickListener(this);
        take.setOnClickListener(this);
        record.setOnClickListener(this);
        light.setOnClickListener(this);
        shibie.setOnClickListener(this);
        auto.setOnClickListener(this);
        set.setOnClickListener(this);
//        test.setOnClickListener(this);

        if (_module == null ){
            _module = new Module(this);
        } else {
        _module.setContext(this);
        }
		_module.setLogLevel(Enums.LogLevel.VERBOSE);
		_module.setUsername("admin");
		_module.setPassword("admin");
		_module.setPlayerPort(554);
		_module.setModuleIp(_moduleIp);

        _player = _module.getPlayer();
        _player.setTimeout(10000);
        _player.setRecordFrameRate(20);
        _player.setAudioOutput(false);
        _player.setDisplayView(getApplication(),_displayView,null,0);
        _player.changePipe(_pipe);
        _player.setImageSize(1280,720);
        _player.setViewSize(_viewHeight,_viewWidth);
        _displayView.setFullScreen(true);
        _player.startGetYUVData(true);
        _player.setOnGetYUVDataListener(new Player.OnGetYUVDataListener() {
            @Override
            public void onResult(int width, int height, byte[] yData, byte[] uData, byte[] vData) {
                //Log.e("YUVData==>","width==> "+width+"height==>"+height);
            }
        });


        _player.setOnStateChangedListener(new Player.OnStateChangedListener() {
            @Override
            public void onStateChanged(Enums.State state) {
                getState(state);
            }
        });


        handler = new Handler(){
           public void handleMessage(Message msg) {
              if (msg.what == 0x00){
                  playstate.setText("空闲");
                  take.setClickable(false);
                  record.setClickable(false);
                  //auto.setClickable(false);
                  //shibie.setClickable(false);
              }
              if (msg.what == 0x01){
                  playstate.setText("准备播放");
              }
              if (msg.what == 0x02){
                  playstate.setText("正在播放");
                  take.setClickable(true);
                  auto.setClickable(true);
                  record.setClickable(true);
                  shibie.setClickable(true);
              }
              if (msg.what == 0x03){
                  playstate.setText("停止播放");
                  take.setClickable(false);
                  record.setClickable(false);
                 // auto.setClickable(false);
                 // shibie.setClickable(false);
              }
               if (msg.what == 0x04){
                   Toast.makeText(getApplicationContext(),"连接成功",Toast.LENGTH_LONG).show();
                   start.setImageResource(R.drawable.c2);
                   qian.setClickable(true);
                   hou.setClickable(true);
                   zuo.setClickable(true);
                   you.setClickable(true);
               }
               if (msg.what == 0x05 ){
                   Toast.makeText(getApplicationContext(),"连接失败",Toast.LENGTH_LONG).show();
                   conT.socket = null;
                   conT = null;
               }
               if (msg.what == 0x06 ){
                   play.setImageResource(R.drawable.s2);
               }
               if (msg.what == 0x07 ){
                   play.setImageResource(R.drawable.s1);
               }
               if (msg.what == 0x08 ){
                   Toast.makeText(getApplicationContext(),"录像已保存至"+Environment.getExternalStorageDirectory().getPath()+xVideo_Video,Toast.LENGTH_LONG).show();
                   record.setImageResource(R.drawable.record);
               }
               if (msg.what == 0x09 ){
                   Toast.makeText(getApplicationContext(),"开始录像",Toast.LENGTH_LONG).show();
                   record.setImageResource(R.drawable.stop);
               }
               if (msg.what == 0x0a ){
                   Toast.makeText(getApplicationContext(),"图片已保存至"+Environment.getExternalStorageDirectory().getPath() +xVideo_Photo,Toast.LENGTH_SHORT).show();
               }
               if (msg.what == 0x0b ){
                   Toast.makeText(getApplicationContext(),"请先开启摄像头",Toast.LENGTH_LONG).show();
               }
               if (msg.what == 0x0c){
                  qian.setImageResource(R.drawable.gostop);
               }
               if (msg.what == 0x0d){
                   zuo.setImageResource(R.drawable.gostop);
               }
               if (msg.what == 0x0e){
                   you.setImageResource(R.drawable.gostop);
               }
               if (msg.what == 0x1a){
                   hou.setImageResource(R.drawable.gostop);
               }
               if (msg.what == 0x0f){
                   qian.setImageResource(R.drawable.fq);
               }
               if (msg.what == 0x10){
                   zuo.setImageResource(R.drawable.fz);
               }
               if (msg.what == 0x11){
                   you.setImageResource(R.drawable.fy);
               }
               if (msg.what == 0x12){
                  light.setImageResource(R.drawable.onlight);
               }
               if (msg.what == 0x13){
                  light.setImageResource(R.drawable.offlight);
               }
              if (msg.what == 0x14){
                  auto.setImageResource(R.drawable.fstop);
              }
              if (msg.what == 0x15){
                  auto.setImageResource(R.drawable.auto);
              }
              if (msg.what == 0x16){
                  hou.setImageResource(R.drawable.fh);
              }
              if (msg.what == 0x17){
                  start.setImageResource(R.drawable.c1);
                  qian.setClickable(false);
                  hou.setClickable(false);
                  zuo.setClickable(false);
                  you.setClickable(false);
              }
              if (msg.what == 0x18)
              {
                  text.setText(msg.obj.toString());
              }
              if (msg.what == 0x19)
              {
                  text.setText("");
              }
           }
        };
        getState(_player.getState());
        verifyStoragePermissions(Play.this);
    }
    public void getState(Enums.State state) {
        Message msg = new Message();
        switch (state) {
            case IDLE:
                msg.what = 0;
                handler.sendMessage(msg);
                break;
            case PREPARING:
                msg.what = 1;
                handler.sendMessage(msg);
                break;
            case PLAYING:
                msg.what = 2;
                handler.sendMessage(msg);
                break;
            case STOPPED:
                msg.what = 3;
                handler.sendMessage(msg);
                break;
        }
    }

    public boolean onTouch(View v, MotionEvent event){
        switch (v.getId()) {
            case R.id.qian:
                if (conT != null) {
                    if (socket != null) {
                        if (socket.isConnected()) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                conT.send(bqian);
                                Message msg = new Message();
                                msg.what = 0x0c;
                                handler.sendMessage(msg);
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                conT.send(bstop);
                                Timer timer = new Timer();
                                TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        conT.send(bstop);
                                    }
                                };
                                timer.schedule(task, 20);
                                Message msg = new Message();
                                msg.what = 0x0f;
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }
                break;
            case R.id.zuo:
                if (conT != null) {
                    if (socket != null) {
                        if (socket.isConnected()) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                conT.send(bzuo);
                                Message msg = new Message();
                                msg.what = 0x0d;
                                handler.sendMessage(msg);
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                conT.send(bstop);
                                Timer timer = new Timer();
                                TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        conT.send(bstop);
                                    }
                                };
                                timer.schedule(task, 20);
                                Message msg = new Message();
                                msg.what = 0x10;
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }
                break;
            case R.id.you:
                if (conT != null) {
                    if (socket != null) {
                        if (socket.isConnected()) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                conT.send(byou);
                                Message msg = new Message();
                                msg.what = 0x0e;
                                handler.sendMessage(msg);
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                conT.send(bstop);
                                Timer timer = new Timer();
                                TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        conT.send(bstop);
                                    }
                                };
                                timer.schedule(task, 20);
                                Message msg = new Message();
                                msg.what = 0x11;
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }
                break;
            case R.id.hou:
                if (conT != null) {
                    if (socket != null) {
                        if (socket.isConnected()) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                conT.send(bhou);
                                Message msg = new Message();
                                msg.what = 0x1a;
                                handler.sendMessage(msg);
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                conT.send(bstop);
                                Timer timer = new Timer();
                                TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {
                                        conT.send(bstop);
                                    }
                                };
                                timer.schedule(task, 20);
                                Message msg = new Message();
                                msg.what = 0x16;
                                handler.sendMessage(msg);
                            }
                        }
                    }
                }
                break;
        }
        return true;
    }
    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.light:
                if (conT != null) {
                    if (conT.socket.isConnected()) {
                        if (!flaglight) {
                            conT.send(onl);
                            Message msg = new Message();
                            msg.what = 0x12;
                            handler.sendMessage(msg);
                            flaglight = true;
                        } else {
                            conT.send(offl);
                            Message msg = new Message();
                            msg.what = 0x13;
                            handler.sendMessage(msg);
                            flaglight = false;
                        }
                    }
                }
                break;
            case R.id.open:
                if (_player.getState() == Enums.State.IDLE) {
                    _player.play(_pipe, Enums.Transport.TCP);
                    Message msg = new Message();
                    msg.what = 0x06;
                    handler.sendMessage(msg);
                } else {
                    _player.stop();
                    Message msg = new Message();
                    msg.what = 0x07;
                    handler.sendMessage(msg);
                }
                break;
            case R.id.start:
                if (socket == null) {
                    conT = null;
                    conT = new ConnectThread(handler, device);
                    conT.start();
                    app.setConT(conT);
                    /*mhandlethread = new handlethread();
                    mhandlethread.start();*/

                } else {
                    try {
                        if (socket.isConnected()) {
                            conT.send(bdiscon);
                            socket.close();
                            socket = null;
                            conT.socket = null;
                            conT = null;
                        } else {
                            socket = null;
                            if (conT != null) {
                                conT.socket = null;
                                conT = null;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Message msg = new Message();
                    msg.what = 0x17;
                    handler.sendMessage(msg);
                }
                break;
            case R.id.take:
                takephoto();
                break;
            case R.id.record:
                if (_player.getState() == Enums.State.PLAYING) {
                    if (_recording) {
                        _player.endRecord();
                        _recording = false;
                        Message msg = new Message();
                        msg.what = 0x08;
                        handler.sendMessage(msg);
                    } else {
                        File[] filephoto = Fvideo.listFiles();
                        SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss");
                        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                        String str = formatter.format(curDate);
                        int photolength = filephoto.length;
                        int length = 1;
                        for (int i = 0; i < photolength; i++) {
                            int start = filephoto[i].getName().indexOf("_");
                            int end = filephoto[i].getName().indexOf("  ");
                            String aa = filephoto[i].getName().substring(start + 1, end);
                            if (Integer.parseInt(aa) >= length) {
                                length = Integer.parseInt(aa);
                                length = length + 1;
                            }
                        }
                        _player.beginRecord0(Environment.getExternalStorageDirectory().getPath() + xVideo_Video, "/VIDEO " + "_" + length + "  " + str);
                        _recording = true;
                        Message msg = new Message();
                        msg.what = 0x09;
                        handler.sendMessage(msg);
                    }
                } else {
                    Message msg = new Message();
                    msg.what = 0x0b;
                    handler.sendMessage(msg);
                }
                break;
            case R.id.views:
                Intent intent = new Intent(Play.this, Checkview.class);
                startActivity(intent);
                break;
            case R.id.set:
                Intent intent1 = new Intent(Play.this, Emotionset.class);
                startActivity(intent1);
                break;
            case R.id.shibie:
                if (socket != null) {
                    conT.send("Q\r".getBytes());
                }
                autodialog = new Autodialog(Play.this, R.style.autodialog);
                View view = autodialog.getDialogview();
                sfv = (Mysuf) view.findViewById(R.id.mysuf);
                final ImageButton follow = (ImageButton) view.findViewById(R.id.follow);
                final ImageButton autotest = (ImageButton)view.findViewById(R.id.autotest);
                autotest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!testflag) {
                            testflag = true;
                            autotest.post(new Runnable() {
                                @Override
                                public void run() {
                                    autotest.setImageResource(R.drawable.on);
                                }
                            });
                        }else {
                            testflag = false;
                            autotest.post(new Runnable() {
                                @Override
                                public void run() {
                                    autotest.setImageResource(R.drawable.off);
                                }
                            });
                        }
                    }
                });
                ImageButton ju = (ImageButton) view.findViewById(R.id.juxing);
                ju.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /*if (FLAG == 0){
                            FLAG = modeju;
                        }
                        if (FLAG == modeju){
                            FLAG = 0;
                        }else {
                            FLAG = modeju;
                        }*/
                        findsquare();
                    }
                });
                ImageButton yuan = (ImageButton) view.findViewById(R.id.yuan);
                yuan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       /* if (FLAG == 0){
                            FLAG = modeyuan;
                        }
                        if (FLAG == modeyuan){
                            FLAG = 0;
                        }else {
                            FLAG = modeyuan;
                        }*/
                        findcircle();
                    }
                });
                ImageButton back = (ImageButton) view.findViewById(R.id.back);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        threadflag = false;
                        ok = false;
                        autodialog.cancel();
                        Timer timer = new Timer();
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {

                            }
                        };
                        timer.schedule(task, 500);
                        autodialog = null;
                        sfv = null;
                        if (socket != null) {
                            conT.send("q".getBytes());
                        }
                    }
                });
                final TextView hmax = (TextView)view.findViewById(R.id.HmaxZ);
                final TextView hmin = (TextView)view.findViewById(R.id.HminZ);
                final TextView s = (TextView)view.findViewById(R.id.SminZ);
                final TextView vv = (TextView)view.findViewById(R.id.VminZ);
                SeekBar Hmax = (SeekBar)view.findViewById(R.id.Hmax);
                Hmax.setProgress(highh);
                Hmax.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                        highh = progress;
                        dataeditor.putInt("highh",highh);
                        dataeditor.apply();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                SeekBar Hmin = (SeekBar)view.findViewById(R.id.Hmin);
                Hmin.setProgress(lowh);
                Hmin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                        lowh = progress;
                        dataeditor.putInt("lowh",lowh);
                        dataeditor.apply();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                SeekBar S = (SeekBar)view.findViewById(R.id.Smin);
                S.setProgress(lows);
                S.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                        lows = progress;
                        dataeditor.putInt("lows",lows);
                        dataeditor.apply();
                    }
                    //        highh = 93  lowh=53  lows63  lowv43
                            //        86       41      88      71
                           // r       206       103   154    125

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                SeekBar V = (SeekBar)view.findViewById(R.id.Vmin);
                V.setProgress(lowv);
                V.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                        lowv = progress;
                        dataeditor.putInt("lowv",lowv);
                        dataeditor.apply();
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
                if (socket != null){
                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (followflag == 0) {
                                if (conT != null) {
                                    if (conT.socket.isConnected()) {
                                        if (mythread != null) {
                                            follow.setImageResource(R.drawable.fstop);
                                            followflag = 1;
                                           /* Timer timer = new Timer();
                                            TimerTask task = new TimerTask() {
                                                @Override
                                                public void run() {

                                                }
                                            };
                                            timer.schedule(task, 500, 500);*/
                                        }
                                    }
                                }
                            } else {
                                followflag = 0;
                                follow.setImageResource(R.drawable.follow);
                            }
                        }
                    });
                }
                    autodialog.show();

                break;
            case R.id.auto:
                if (!planflag) {
                    if (st == null) {
                        stflag = true;
                        st = new StartplanT();
                        st.start();
                    }
                }else {
                    stflag = false;
                    st = null;
                    planflag = false;
                    pt = null;
                    plandata = new ArrayList<>();
                    plandataflag = 0;
                    mystop();
                    Message msg = new Message();
                    msg.what = 0x15;
                    handler.sendMessage(msg);
                }
                break;
            case R.id.up:
                if (socket != null){
                    if (socket.isConnected()){
                        conT.send(bup);
                    }
                }
                break;
            case R.id.down:
                if (socket != null){
                    if (socket.isConnected()){
                        conT.send(bdown);
                    }
                }
                break;
            /*case R.id.test:
                Intent intent2 = new Intent(Play.this,Remote.class);
                startActivity(intent2);
                break;*/
        }
    }

    public void createdir(){
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
            X = new File(Environment.getExternalStorageDirectory().getPath() +xVideo);
            Fphoto = new File(Environment.getExternalStorageDirectory().getPath() +xVideo_Photo);
            Fvideo = new File(Environment.getExternalStorageDirectory().getPath() +xVideo_Video);
            if (!X.exists()){
                X.mkdir();
            }
            if (!Fphoto.exists()){
                Fphoto.mkdir();
            }
            if (!Fvideo.exists()){
                Fvideo.mkdir();
            }
        }
    }

    //调用auto，更新surfaceview
    private class  Mythread extends Thread{
        int x;
        private Mythread(int x){
            this.x = x;
            threadflag = true;
        }
        @Override
        public void run(){
            while (threadflag) {
               // synchronized (lock) {
                bitmaplist.add(auto(x));
                ok = true;
                    /*if (bitmaps[0] == null) {
                        bitmaps[0] = auto(x,plan);
                    }*/
                    /*try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                   // lock.notify();
               // }
            }
        }
    }

    public static Bitmap getbitmap(){
        return bitmaplist.get(0);
    }

    public static void clearbitmap(){
        bitmaplist.get(0).recycle();
        bitmaplist.remove(0);
    }

    public static int getbitmaplistsize(){
        return bitmaplist.size();
    }

    //调用mycpp，进行识别,2个模式
    private Bitmap auto(int x){
        Bitmap p;
        double[] s = new double[2];
        p = _player.takePhoto();
        Mat src = new Mat();
        if (p != null) {
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f, 0.5f);
            p = Bitmap.createBitmap(p, 0, 0, p.getWidth(), p.getHeight(), matrix, true);
            Utils.bitmapToMat(p, src);
            xhalfsrc = src.cols()/2;
            yhalfsrc = src.rows()/2;
            if (x == modeju) {
                xdistance = s[0];
            }
            if (x == modeyuan) {
                s = Mycpp.getcircle(src.getNativeObjAddr(),lowh,lows,lowv,highh,testflag);
                if (three == 1) {
                    three = 0;
                    xdistance = s[0];
                    ydistance = s[1];
                    if (followflag == 1) {
                        byte[] order = new byte[3];
                        order[2] = '\r';
                        if (xdistance == -320 || ydistance == -180) {
                            mystop();
                        } else {
                            if (xdistance > 160 || xdistance < -160) {
                                if (xdistance < 0) {
                                    order[0] = 'A';
                                    byte ss = (byte) (Math.abs(xdistance) / xhalfsrc * 100);
                                    order[1] = ss;
                                }
                                if (xdistance > 0) {
                                    order[0] = 'D';
                                    byte ss = (byte) (Math.abs(xdistance) / xhalfsrc * 100);
                                    order[1] = ss;
                                }
                            }
                            if (xdistance < 160 && xdistance > -160) {
                                if (ydistance > 90 || ydistance < 45) {
                                    if (ydistance < 45) {
                                        order[0] = 'W';
                                        byte ss = (byte) (Math.abs(ydistance) / yhalfsrc * 100);
                                        order[1] = ss;
                                    }
                                    if (ydistance > 90) {
                                        order[0] = 'S';
                                        byte ss = (byte) (Math.abs(xdistance) / xhalfsrc * 100);
                                        order[1] = ss;
                                    }
                                }
                            }
                            if (order[0] != 0) {
                                conT.send(order);
                            } else {
                                mystop();
                            }
                        }
                    }
                }
            }
            three++;
            Utils.matToBitmap(src, p);
            src.release();
        }
        return p;
    }

    //开启圆识别
    private void findcircle() {
        if (threadflag){
            threadflag = false;
            mythread = null;
            flagyuan = 0;
        }else {
            if (flagyuan == 0) {
                mythread = new Mythread(modeyuan);
                mythread.start();
                flagyuan = 1;
            } else {
                threadflag = false;
                mythread = null;
                flagyuan = 0;
            }
        }
    }

    //开启矩形识别
    private void findsquare() {
        if (threadflag){
            threadflag = false;
            mythread = null;
            flagju = 0;
        }else {
            if (flagju == 0) {
                mythread = new Mythread(modeju);
                mythread.start();
                flagju = 1;
            } else {
                threadflag = false;
                mythread = null;
                flagju = 0;
            }
        }
    }

    //路线规划（延时）
    private void plan1() {
        Bitmap p = BitmapFactory.decodeResource(getResources(),R.drawable.auto);
        //Mat src = new Mat();
        Message msg1 = new Message();
        msg1.what = 0x25;
        handler.sendMessage(msg1);
        /*byte ultdisdance = 30;
        boolean ultflag;
        ultgo = 0;

        getult(ultdisdance);*/
        plansend(0);
        mysleep(1500);
        int pflag = 1;

        conT.send(bzuo);
        Timer timer1 = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        };
        timer1.schedule(task,400);
        mysleep(1000);
        /*getult(ultdisdance);*/
        mysleep(1000);
        plansend(pflag);
        pflag++;

       /* conT.send(bzuo);
        conT.sendorder();
        mysleep(500);
        stop();
        mysleep(1000);*/

        conT.send(bzuo);
        Timer timer2 = new Timer();
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                stop();
            }
        };
        timer2.schedule(task2,400);
        mysleep(1000);
       /* getult(ultdisdance);*/
        mysleep(1000);
        plansend(pflag);
        pflag++;

       /* conT.send(bzuo);
        conT.sendorder();
        mysleep(500);
        stop();
        mysleep(1000);
        ultflag = getult(ultdisdance);
        mysleep(500);
        if (ultflag) {
            plansend(p, src, pflag);
        }else {
            ultgo++;
        }
        pflag++;*/

        conT.send(byou);
        mysleep(1200);
        stop();
        mysleep(1000);
        /*getult(ultdisdance);*/
        mysleep(1000);
        plansend(pflag);
        pflag++;

        conT.send(bup);
        mysleep(800);
        takephoto();
        conT.send(bdown);
        mysleep(800);

        conT.send(byou);
        mysleep(400);
        stop();
        mysleep(1000);
      /*  getult(ultdisdance);*/
        mysleep(1000);
        plansend(pflag);

        conT.send(bzuo);
        mysleep(800);
        stop();
        mysleep(1500);

        int min = lines[0];
        int minflag = 0;
        for (int n = 0;n < 5;n++)
        {
            if(lines[n] < min)
            {
                min = lines[n];
                minflag++;
            }
        }
        switch (minflag){
            case 1:conT.send(bzuo);mysleep(400);mystop();mysleep(500);break;
            case 2:conT.send(bzuo);mysleep(800);mystop();mysleep(500);break;
            case 3:conT.send(byou);mysleep(400);mystop();mysleep(500);break;
            case 4:conT.send(byou);mysleep(800);mystop();mysleep(500);break;
            case 0:break;
        }
        conT.send(bqian);
        mysleep(2000);
        mystop();
        Message msg = new Message();
        msg.what = 0x19;
        handler.sendMessage(msg);
        mysleep(500);
    }

    //路线规划（陀螺仪）
    private void plan (){
       // mysleep(200);
        conT.send("C\r".getBytes());
        while (conT.getTan() == 999) {
            conT.send("C\r".getBytes());
            mysleep(100);
        }
        tannow = conT.getTan();
        conT.cleartan();
        int tt = tannow-tan;
        if (tt > -20 && tt < 20 && planflag) {
            module0();
        }
        if (tt > -70 && tt < -19 && planflag) {
            moduler35();
        }
        if (tt < -69 && planflag) {
            moduler70();
        }
        if (tt > 19 && tt < 71 && planflag) {
            modulel35();
        }
        if (tt > 70 && planflag) {
            modulel70();
        }

           if (planflag) {
               conT.send("F\n".getBytes());
               while (conT.gettanflag()) {
               }
               conT.changetanflag();

           }
    }

    public void takephoto() {
        if (_player.getState() == Enums.State.PLAYING) {
            if (Fphoto.exists()) {
                photopath = Environment.getExternalStorageDirectory().getPath() + xVideo_Photo;
                SimpleDateFormat formatter = new SimpleDateFormat("HH-mm-ss");
                Date curDate = new Date(System.currentTimeMillis());
                String str = formatter.format(curDate);
                File[] filephoto = Fphoto.listFiles();
                int length = 1;
                int photolength = filephoto.length;
                for (int i = 0; i < photolength; i++) {
                    int start = filephoto[i].getName().indexOf("_");
                    int end = filephoto[i].getName().indexOf("  ");
                    String aa = filephoto[i].getName().substring(start + 1, end);
                    if (Integer.parseInt(aa) >= length) {
                        length = Integer.parseInt(aa);
                        length = length + 1;
                    }
                }
                try {
                    fos = new FileOutputStream(photopath + "/IMG " + "_0" + length + "  " + str + ".jpg");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = _player.takePhoto();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Message msg = new Message();
                msg.what = 0x0a;
                handler.sendMessage(msg);
            }
        }else {
            Message msg = new Message();
            msg.what = 0x0b;
            handler.sendMessage(msg);
        }
    }


    //路线规划线程
    class planthread extends Thread{
        @Override
        public void run() {
            if (conT != null ) {
                while (planflag) {
                    plan();

                }
            }
        }
    }

    //发送停止指令
    private void stop(){
        if (conT != null) {
            if (conT.socket != null) {
                if (conT.socket.isConnected()) {
                    conT.send(bstop);
                    Timer timer = new Timer();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            conT.send(bstop);
                        }
                    };
                    timer.schedule(task, 20);
                }
            }
        }
    }

    //路线规划获取分析结果
    private void plansend(int n) {
        Bitmap p = _player.takePhoto();
        Mat src = new Mat();
        Utils.bitmapToMat(p,src);
        lines[n] = Mycpp.getlines(src.getNativeObjAddr());
        String line = String.valueOf(lines[n]);
        String x = text.getText().toString();
        x = x+"\n"+line;
    }

    //线程阻塞
    private void mysleep(int x){
        try{
            Thread.sleep(x);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private void mystop(){
        conT.send(bstop);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                conT.send(bstop);
            }
        } ;
        timer.schedule(task,20);
    }


    static public void setSocket(BluetoothSocket s){
        socket = s;
    }
    public void showmessage(){
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(Play.this);
        PendingIntent intent2 = PendingIntent.getActivity(Play.this,100,new Intent(this,Showmessage.class),PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentText("你的宠物给你发送了一张照片");
        builder.setContentTitle("RobotPet");
        builder.setContentIntent(intent2);
        builder.setDefaults(Notification.DEFAULT_ALL);
        builder.setSmallIcon(R.drawable.auto);
        Notification notification = builder.build();
        manager.notify(0,notification);
    }
    private void module0(){
        byte l35 = 35;
        byte l70 = 70;
        byte r35 = 35;
        byte r70 = 70;
        byte r105 = 105;
        plansend(0);
        int flag = 1;
        mysleep(500);

        conT.sendplan(bytezuo, l35);
        while (!conT.gettanflag()) { }
        conT.changetanflag();
        plansend(flag);
        flag++;

        conT.sendplan(bytezuo, l35);
        while (!conT.gettanflag()) { }
        conT.changetanflag();
        plansend(flag);
        flag++;

        conT.sendplan(byteyou,r105);
        while (!conT.gettanflag()){ }
        conT.changetanflag();
        plansend(flag);
        flag++;

        conT.sendplan(byteyou,r35);
        while (!conT.gettanflag()){ }
        conT.changetanflag();
        plansend(flag);

        conT.sendplan(bytezuo,l70);
        while (!conT.gettanflag()){ }
        conT.changetanflag();
        int min = lines[0];
        int minflag = 0;
        for (int n = 0;n < 5;n++)
        {
            if(lines[n] < min)
            {
                min = lines[n];
                minflag++;
            }
        }
        switch (minflag){
            case 1:
                conT.sendplan(bytezuo,l35);
                while (!conT.gettanflag()){ }
                conT.changetanflag();
                plandata.add("l35");
                break;
            case 2:
                conT.sendplan(bytezuo,l70);
                while (!conT.gettanflag()){ }
                conT.changetanflag();
                plandata.add("l70");
                break;
            case 3:
                conT.sendplan(byteyou,r35);
                while (!conT.gettanflag()){ }
                conT.changetanflag();
                plandata.add("r35");
                break;
            case 4:
                conT.sendplan(byteyou,r70);
                while (!conT.gettanflag()){ }
                conT.changetanflag();
                plandata.add("r70");
                break;
            case 0:
                plandata.add("0");
                break;
        }
    }
    private void modulel35(){
        byte l35 = 35;
        byte l70 = 70;
        byte r35 = 35;
        byte r70 = 70;
        byte r105 = 105;
        plansend(0);
        int flag = 1;

        conT.sendplan(bytezuo, l35);
        while (!conT.gettanflag()) { }
        conT.changetanflag();
        plansend(flag);
        flag++;

        conT.sendplan(byteyou,r70);
        while (!conT.gettanflag()){ }
        conT.changetanflag();
        plansend(flag);
        flag++;


            conT.sendplan(byteyou, r35);
            while (!conT.gettanflag()) {
            }
            conT.changetanflag();
            plansend(flag);


        if (planflag) {
            conT.sendplan(bytezuo, l70);
            while (!conT.gettanflag()) {
            }
            conT.changetanflag();
            int min = lines[0];
            int minflag = 0;
            for (int n = 0; n < 5; n++) {
                if (lines[n] < min) {
                    if (lines[n] != 0) {
                        min = lines[n];
                        minflag = n;
                    }
                }
            }
            switch (minflag) {
                case 1:
                    conT.sendplan(bytezuo, l35);
                    while (!conT.gettanflag()) {
                    }
                    conT.changetanflag();
                    plandata.add("l35");
                    break;
                case 2:
                    conT.sendplan(byteyou, r35);
                    while (!conT.gettanflag()) {
                    }
                    conT.changetanflag();
                    plandata.add("r35");
                    break;
                case 3:
                    conT.sendplan(byteyou, r70);
                    while (!conT.gettanflag()) {
                    }
                    conT.changetanflag();
                    plandata.add("r70");
                    break;
                case 0:
                    plandata.add("0");
                    break;
            }
        }
    }
    private void modulel70(){
        byte l35 = 35;
        byte l70 = 70;
        byte r35 = 35;
        byte r70 = 70;
        byte r105 = 105;
        plansend(0);
        int flag = 1;

        conT.sendplan(byteyou,r35);
        while (!conT.gettanflag()){ }
        conT.changetanflag();
        plansend(flag);
        flag++;

            conT.sendplan(byteyou, r35);
            while (!conT.gettanflag()) { }
            conT.changetanflag();
            plansend(flag);

            conT.sendplan(bytezuo, l70);
            while (!conT.gettanflag()) { }
            conT.changetanflag();

        if (planflag) {
            int min = lines[0];
            int minflag = 0;
            for (int n = 0; n < 5; n++) {
                if (lines[n] < min) {
                    if (lines[n] != 0) {
                        min = lines[n];
                        minflag = n;
                    }
                }
            }
            switch (minflag) {
                case 1:
                    conT.sendplan(byteyou, r35);
                    while (!conT.gettanflag()) { }
                    conT.changetanflag();
                    plandata.add("r35");
                    break;
                case 2:
                    conT.sendplan(byteyou, r70);
                    while (!conT.gettanflag()) { }
                    conT.changetanflag();
                    plandata.add("r70");
                    break;
                case 0:
                    plandata.add("0");
                    break;
            }
        }
    }
    private void moduler35(){
        byte l35 = 35;
        byte l70 = 70;
        byte r35 = 35;
        byte r70 = 70;
        byte r105 = 105;
        plansend(0);
        int flag = 1;

        conT.sendplan(bytezuo, l35);
        while (!conT.gettanflag()) { }
        conT.changetanflag();
        plansend(flag);
        flag++;

        conT.sendplan(bytezuo, l35);
        while (!conT.gettanflag()) { }
        conT.changetanflag();
        plansend(flag);
        flag++;

            conT.sendplan(byteyou, r105);
            while (!conT.gettanflag()) {
            }
            conT.changetanflag();
            mysleep(600);
            plansend(flag);
            flag++;


            conT.sendplan(bytezuo, l35);
            while (!conT.gettanflag()) {
            }
            conT.changetanflag();

        if (planflag) {
            int min = lines[0];
            int minflag = 0;
            for (int n = 0; n < 5; n++) {
                if (lines[n] < min) {
                    if (lines[n] != 0) {
                        min = lines[n];
                        minflag = n;
                    }
                }
            }
            switch (minflag) {
                case 1:
                    conT.sendplan(bytezuo, l35);
                    while (!conT.gettanflag()) {
                    }
                    conT.changetanflag();
                    plandata.add("l35");
                    break;
                case 2:
                    conT.sendplan(bytezuo, l70);
                    while (!conT.gettanflag()) {
                    }
                    conT.changetanflag();
                    plandata.add("l70");
                    break;
                case 3:
                    conT.sendplan(byteyou, r35);
                    while (!conT.gettanflag()) {
                    }
                    conT.changetanflag();
                    plandata.add("r35");
                    break;
                case 0:
                    plandata.add("0");
                    break;
            }
        }
    }
    private void moduler70() {
        byte l35 = 35;
        byte l70 = 70;
        byte r35 = 35;
        byte r70 = 70;
        byte r105 = 105;
        plansend(0);
        int flag = 1;

        if (planflag) {
            conT.sendplan(bytezuo, l35);
            while (!conT.gettanflag()) {
            }
            conT.changetanflag();
            plansend(flag);
            flag++;
        }
        if (planflag) {
            conT.sendplan(bytezuo, l35);
            while (!conT.gettanflag()) {
            }
            conT.changetanflag();
            plansend(flag);

            conT.sendplan(byteyou, l70);
            while (!conT.gettanflag()) {
            }
            conT.changetanflag();
        }
        if (planflag) {
            int min = lines[0];
            int minflag = 0;
            for (int n = 0; n < 5; n++) {
                if (lines[n] < min) {
                    if (lines[n] != 0) {
                        min = lines[n];
                        minflag = n;
                    }
                }
            }
            switch (minflag) {
                case 1:
                    conT.sendplan(bytezuo, l35);
                    while (!conT.gettanflag()) {
                    }
                    conT.changetanflag();
                    plandata.add("l35");
                    break;
                case 2:
                    conT.sendplan(bytezuo, l70);
                    while (!conT.gettanflag()) {
                    }
                    conT.changetanflag();
                    plandata.add("l70");
                    break;
                case 0:
                    plandata.add("0");
                    break;
            }
        }
    }
    private void datainit(){
        if (savedata.getInt("lowh",0) != 0){
            lowh = savedata.getInt("lowh",0);
        }
        if (savedata.getInt("lows",0) != 0){
            lows = savedata.getInt("lows",0);
        }
        if (savedata.getInt("lowv",0) != 0){
            lowv = savedata.getInt("lowv",0);
        }
        if (savedata.getInt("highh",0) != 0){
            highh = savedata.getInt("highh",0);
        }
    }
    private class StartplanT extends Thread{
        @Override
        public void run(){
            planflag = true;
            conT.send("C\r".getBytes());
            stflag = true;
            while(stflag){
                if (conT.getTan() != 999){
                    stflag = false;
                    conT.send("C\r".getBytes());
                }
            }
            tan = conT.getTan();
            conT.cleartan();
            pt = new planthread();
            pt.start();
            Message msg = new Message();
            msg.what = 0x14;
            handler.sendMessage(msg);
        }
    }
}

