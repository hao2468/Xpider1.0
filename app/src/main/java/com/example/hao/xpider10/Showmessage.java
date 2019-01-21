package com.example.hao.xpider10;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Showmessage extends AppCompatActivity {

    Button button;
    ImageView photo;
    Bitmap bitmap;
    Application app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photofrompet);
        app = (Application)getApplication();
        bitmap = app.getBitmap();
        photo = (ImageView)findViewById(R.id.showphoto);
        if (bitmap != null){
            photo.setImageBitmap(bitmap);
        }
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Showmessage.this,Play.class);
                startActivity(intent);
            }
        });
    }
}
