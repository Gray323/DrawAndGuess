package com.rxd.drawandguess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/27.
 */

public class ColorHorizontalScrollView extends View{

    private Paint mPaint;
    private int radius = 50;
    private List<Rect> rectList;
    private List<Region> regionList;
    private float startX, startY;
    private float dx, dy;
    private StrokeWidthChooseView strokeWidthChooseView;
    private int currentIndex = -1;
    private DrawView.State mCurrentState = DrawView.State.PEN;
    private onStateChnaged onStateChnagedListener;
    private int[] colors = new int[]{
            Color.parseColor("#fd039d"),
            Color.parseColor("#ff4d3f"),
            Color.parseColor("#fda602"),
            Color.parseColor("#fff001"),
            Color.parseColor("#000000"),
            Color.parseColor("#00b181"),
            Color.parseColor("#004bfe"),
            Color.parseColor("#2c6281"),
            Color.parseColor("#4e4c61"),
            Color.parseColor("#edd93f"),
            Color.parseColor("#666666"),
            Color.parseColor("#66b502"),
            Color.parseColor("#66fecb"),
            Color.parseColor("#03c1fe"),
            Color.parseColor("#966b59"),
            Color.parseColor("#fda7a4"),
            Color.parseColor("#f42728"),
            Color.parseColor("#2c6281"),
            Color.parseColor("#4e4c61"),
            Color.parseColor("#edd93f"),
            Color.parseColor("#666666"),
            Color.parseColor("#c9c9c9"),
            Color.parseColor("#8efbf6"),
            Color.parseColor("#78d1b8"),
            Color.parseColor("#bb18fd"),
            Color.parseColor("#ffffcc"),
            Color.parseColor("#fdcdb7"),
            Color.parseColor("#993300"),
    };

    public ColorHorizontalScrollView(Context context) {
        this(context, null);
    }

    public ColorHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.FILL);

        rectList = new ArrayList<>();
        regionList = new ArrayList<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Rect rect;
        for (int i = 0; i < colors.length; i++){
            int centerX = 20 * i + radius * 2 * i + radius;
            int centerY = h / 2;
            rect = new Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            rectList.add(rect);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect;
        Rect chooseRect;
        switch (mCurrentState){
            case ERASER:
                for (int i = 0; i < rectList.size(); i++){
                    mPaint.setColor(colors[i]);
                    rect = rectList.get(i);
                    canvas.drawRect(rect, mPaint);
                }
                break;
            case PEN:
                for (int i = 0; i < rectList.size(); i++){
                    mPaint.setColor(colors[i]);
                    rect = rectList.get(i);
                    if (currentIndex == i){
                        chooseRect = new Rect(rect.left, rect.bottom - 10, rect.right, rect.bottom);
                        rect = new Rect(rect.left, rect.top - 30, rect.right, rect.bottom - 30);
                        canvas.drawRect(chooseRect, mPaint);
                        canvas.drawRect(rect, mPaint);
                    }else{
                        canvas.drawRect(rect, mPaint);
                    }

                }
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("TAG", "ACTION_DOWN");
                startX = event.getX();
                startY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("TAG", "ACTION_MOVE");
                dx = event.getX() - startX;
                dy = event.getY() - startY;
                if (Math.abs(dx) -  Math.abs(dy) > ViewConfiguration.getTouchSlop()){
                    if (getScrollX() + (-dx) < 0 || getScrollX() + (-dx) > getWidth()){
                        return true;
                    }
                    this.scrollBy((int) -dx, 0);
                    startX = event.getX();
                    startY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                Log.d("TAG", "ACTION_UP");
                Log.d("TAG", "dx == " + dx);
                Log.d("TAG", "dy == " + dy);
                if (Math.abs(dx) <= 10 && Math.abs(dy) <= 10){
                    for (int i = 0; i < rectList.size(); i++){
                        if (rectList.get(i).contains((int) startX + getScrollX(), (int) startY + getScrollY())){
                            Log.d("TAG", "有包含的");
                            Rect rect;
                            rectList.clear();
                            for (int j = 0; j < colors.length; j++){
                                int centerX = 20 * j + radius * 2 * j + radius;
                                int centerY = getHeight() / 2;
                                rect = new Rect(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
                                rectList.add(rect);
                            }
                            currentIndex = i;
                            onStateChnagedListener.onPen();
                            strokeWidthChooseView.setmPaintColor(colors[i]);
                            invalidate();
                        }
                    }
                }
                startX = 0;
                startY = 0;
                dx = 0;
                dy = 0;
                break;
        }
        return true;
    }

    public void setStrokeWidthChooseView(StrokeWidthChooseView strokeWidthChooseView) {
        this.strokeWidthChooseView = strokeWidthChooseView;
    }

    public void setmCurrentState(DrawView.State mCurrentState) {
        this.mCurrentState = mCurrentState;
        invalidate();
    }

    public interface onStateChnaged{
        void onPen();
    }

    public void setOnStateChnagedListener(onStateChnaged onStateChnagedListener) {
        this.onStateChnagedListener = onStateChnagedListener;
    }
}
