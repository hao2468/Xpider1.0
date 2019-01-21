package com.example.hao.xpider10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Checkview extends AppCompatActivity {
    private ViewPager vp;
    Bitmap[] views;
    private File file;
    private FileInputStream fis;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        vp = (ViewPager)findViewById(R.id.vp);
        file = new File(Environment.getExternalStorageDirectory().getPath() +"/XpiderVideo/Photo");
        try {
            if (file.exists()) {
                if (file.list().length == 0) {
                    Toast.makeText(getApplicationContext(), "没有图片可显示", Toast.LENGTH_LONG).show();
                } else {
                    File[] files = file.listFiles();
                    views = new Bitmap[file.list().length];
                    for (int x = 0; x < file.list().length; x++) {
                        fis = new FileInputStream(files[x]);
                        views[x] = BitmapFactory.decodeStream(fis);
                    }
                }
            }
        }catch (FileNotFoundException e){
            Toast.makeText(getApplicationContext(), "文件夹不存在", Toast.LENGTH_LONG).show();
        }
        PagerAdapter pa = new PagerAdapter() {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                iv = new ImageView(getApplicationContext());
                iv.setImageBitmap(views[position]);
                container.addView(iv);
                return iv;
            }
            @Override
            public int getCount() {
                return views.length;
            }

            @Override
            public boolean isViewFromObject(android.view.View view, Object object) {
                return view == object;
            }
            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {

            }
        };
        vp.setAdapter(pa);
    }
    protected void onDestroy(){
        super.onDestroy();
        for (int x = 0;x < views.length;x++){
            views[x].recycle();
        }
        System.gc();
    }
}
