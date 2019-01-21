package com.example.hao.xpider10;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Autodialog extends Dialog {
    private View dialogview;
    Context context;
    public Autodialog(Context context){
        super(context);
        this.context = context;
    }
    public Autodialog(Context context,int theme){
        super(context,theme);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        dialogview = inflater.inflate(R.layout.activity_auto,null);
    }
    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        this.setContentView(dialogview);
        this.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
    }
    @Override
    public View findViewById(int id){
        return super.findViewById(id);
    }
    public View getDialogview(){
        return dialogview;
    }
}
