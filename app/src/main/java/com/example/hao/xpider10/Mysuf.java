package com.example.hao.xpider10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Mysuf extends SurfaceView implements SurfaceHolder.Callback,Runnable{
    private SurfaceHolder holder;
    private boolean flag = true;
    private Canvas canvas;
    private RectF rectF;
    private Bitmap bitmap;
    public Mysuf(Context context){
        super(context);
        init();
    }
    public Mysuf(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }
    private void init(){
        holder = getHolder();
        holder.addCallback(this);
        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        this.setKeepScreenOn(true);

    }
    @Override
    public void surfaceCreated(SurfaceHolder holder){
        canvas = holder.lockCanvas();
        canvas.drawColor(Color.BLACK);
        holder.unlockCanvasAndPost(canvas);
        new Thread(this).start();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder,int format,int width,int height){
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        flag = false;
    }

    private void drawview(Bitmap bitmap){
        if (holder != null){
            canvas = holder.lockCanvas();
        }
        if (holder != null && canvas != null){
            rectF = new RectF(Mysuf.this.getLeft(),Mysuf.this.getTop(),Mysuf.this.getWidth(),Mysuf.this.getHeight());
            canvas.drawBitmap(bitmap,null,rectF,null);
        }
        holder.unlockCanvasAndPost(canvas);
    }
    @Override
    public void run(){
        while (flag) {
           /* synchronized (lock) {
                try {
                    lock.wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }*/
           if (Play.ok)
           {
                bitmap = Play.getbitmap();
                if (bitmap != null) {
                    drawview(bitmap);
                   /* try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/
                    if (Play.getbitmaplistsize() > 1) {
                        bitmap.recycle();
                        bitmap = null;
                        Play.clearbitmap();
                    }
                }
            }
        }
    }
}
