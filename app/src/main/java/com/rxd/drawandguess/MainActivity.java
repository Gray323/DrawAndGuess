package com.rxd.drawandguess;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements ColorHorizontalScrollView.onStateChnaged {

    private DrawView drawView;
    private ColorHorizontalScrollView colorHorizontalScrollView;
    private StrokeWidthChooseView strokeWidthChooseView;
    private ImageButton ibBack;
    private ImageButton ibEraser;
    private ImageButton ibClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();
    }

    private void initView(){
        drawView = findViewById(R.id.draw_view);
        colorHorizontalScrollView = findViewById(R.id.colorHorizontalScrollView);
        strokeWidthChooseView = findViewById(R.id.strokeWidthChooseView);
        ibBack = findViewById(R.id.ib_back);
        ibEraser = findViewById(R.id.ib_eraser);
        ibClear = findViewById(R.id.ib_clear);

        strokeWidthChooseView.setDrawView(drawView);
        colorHorizontalScrollView.setStrokeWidthChooseView(strokeWidthChooseView);
        colorHorizontalScrollView.setOnStateChnagedListener(this);
    }

    private void initListener(){
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.back();
            }
        });

        ibEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawView.getmCurrentState() == DrawView.State.ERASER){
                    becomePen();
                }else if (drawView.getmCurrentState() == DrawView.State.PEN){
                    becomeEraser();
                }
            }
        });

        ibClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("提示")
                        .setMessage("确定清空画布嘛")
                        .setNegativeButton("不行", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("好的", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                drawView.clearPath();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public void onPen() {
        becomePen();
    }

    private void becomePen(){
        drawView.setCurrentState(DrawView.State.PEN);
        strokeWidthChooseView.setmCurrentState(DrawView.State.PEN);
        colorHorizontalScrollView.setmCurrentState(DrawView.State.PEN);
        ibEraser.setImageResource(R.mipmap.rubbish);
    }

    private void becomeEraser(){
        drawView.setCurrentState(DrawView.State.ERASER);
        strokeWidthChooseView.setmCurrentState(DrawView.State.ERASER);
        colorHorizontalScrollView.setmCurrentState(DrawView.State.ERASER);
        ibEraser.setImageResource(R.mipmap.pen);
    }

}
