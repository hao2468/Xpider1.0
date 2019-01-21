package com.example.hao.xpider10;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class ConnectThread extends Thread {
    OutputStream os;
    InputStream is;
    Handler handler;
    BluetoothSocket socket;
    //Socket socket;
    private boolean sendflag = false;
    private String robotmsg = "xxxx";
    private byte order[];
    private byte[] Rbf = new byte[6];

    private int tan = 999;
    private static boolean planflag = false;
    private static boolean takephoto = false;
    private static int emotionX = 999;
    private static int emotionY = 999;
    private static boolean tanflag = false;

    private UUID My_uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothDevice device;
    public ConnectThread(Handler h,BluetoothDevice d) {
        device = d;
        handler = h;

    }
    public void run() {
        try
        {
            //socket = new Socket("192.168.100.1",80);
          //  socket.connect(address,3000);
            socket = device.createRfcommSocketToServiceRecord(My_uuid);
            Play.setSocket(socket);
            socket.connect();

            if(socket.isConnected())
            {
                Message msg = new Message();
                msg.what = 0x04;
                handler.sendMessage(msg);
                /*send send = new send();
                send.start();*/
            }
            is = socket.getInputStream();
            while(socket.isConnected())
            {
                byte[] bf = new byte[10];
                if(is.available() > 0){
                    try{
                        Thread.sleep(100);
                        is.read(bf,0,6);
                        handlemessage(bf);
                        robotmsg = new String(bf);
                        bf = new byte[6];
                        Rbf = bf;
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }

            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Message msg = new Message();
            msg.what = 0x05;
            handler.sendMessage(msg);
        }
    }
    public void send(byte[] b)
    {
        if (socket.isConnected())
        {
            try {
                os = socket.getOutputStream();
                byte[] bf;
                bf = b;
                os.write(bf);
                os.flush();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*class send extends Thread
    {
        @Override
        public void run() {
            try {
                os = new DataOutputStream(socket.getOutputStream());
            }catch (IOException e)
            {
                e.printStackTrace();
            }
            while (true) {
                if (sendflag) {
                    try {
                        byte[] a = {0x01,0x55};
                        byte[] b = order;
                        byte[] x = new byte[a.length+b.length];
                        System.arraycopy(a,0,x,0,a.length);
                        System.arraycopy(b,0,x,a.length,b.length);
                        os.write(x);
                        os.flush();
                        order = null;
                        sendflag = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }*/
    public void clearmsg(){
        robotmsg = "xxxx";
    }
    public String getmsg(){
        return robotmsg;
    }
    public void sendplan(byte s,byte tans){
        order = new byte[4];
        order[0] = s;
        order[1] = tans;
        order[2] = '\r';
        send(order);

    }
    public void ult(byte u){
        order = new byte[3];
        order[0] = 'u';
        order[1] = u;
        order[2] = '\r';
        mysleep(50);
        send(order);
        order = null;
    }
    public byte[] getRbf(){
        return Rbf;
    }
    private void handlemessage(byte[] b){
            if (b[0] == '%') {
                if (b[1] == '-') {
                    tan = ((b[2] - 48) * 100 + (b[3] - 48) * 10 + (b[4] - 48)) * (-1);
                }
                if (b[1] == 48) {
                    tan = (b[2] - 48) * 100 + (b[3] - 48) * 10 + (b[4] - 48);
                }
            }
            if (b[0] == 'g') {
                planflag = true;
            }
            if (b[0] == 's') {
                takephoto = true;
            }
            if (b[0] == 'X') {
                if (b[1] == '-') {
                    emotionX = ((b[2] - 48) * 100 + (b[3] - 48) * 10 + (b[4] - 48)) * (-1);
                }
                if (b[1] == 48) {
                    emotionX = (b[2] - 48) * 100 + (b[3] - 48) * 10 + (b[4] - 48);
                }
            }
            if (b[0] == 'Y') {
                if (b[1] == '-') {
                    emotionY = ((b[2] - 30) * 100 + (b[3] - 30) * 10 + (b[4] - 30)) * (-1);
                }
                if (b[1] == 30) {
                    emotionY = (b[2] - 30) * 100 + (b[3] - 30) * 10 + (b[4] - 30);
                }
            }
            if (b[0] == 'o') {
                tanflag = true;
            }

    }
    public int getTan (){
        return tan;
    }
    public boolean getPlanflag(){
        return planflag;
    }
    public boolean getPhotoflag(){
        return takephoto;
    }
    public int getEmotionX(){
        return emotionX;
    }
    public int getEmotionY(){
        return emotionY;
    }
    public boolean gettanflag(){
        return tanflag;
    }
    public void changetanflag(){
        tanflag = false;
    }
    public void cleartan(){
        tan = 999;
    }
    /*public void send(byte[] b){
        order = b;
    }*/
    private void mysleep(int x){
        try{
            Thread.sleep(x);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }
}

