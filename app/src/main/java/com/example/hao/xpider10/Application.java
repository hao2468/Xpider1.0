package com.example.hao.xpider10;

import android.graphics.Bitmap;

/**
 * Created by HAO on 2018/4/8.
 */

public class Application extends android.app.Application{
    private ConnectThread conT = null;
    private float emotionX = 0;
    private float emotionY = 0;
    private Bitmap bitmap = null;
    public ConnectThread getconT(){
        return conT;
    }
    public void setConT(ConnectThread c){
        conT = c;
    }
    public void setEmotionX(float x){
        emotionX = x;
    }
    public void setEmotionY(float y){
        emotionY = y;
    }
    public float getEmotionX(){
        return emotionX;
    }
    public float getEmotionY(){
        return emotionY;
    }
    public void setbitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }
    public void clearbitmap(){
        bitmap.recycle();
        bitmap = null;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
