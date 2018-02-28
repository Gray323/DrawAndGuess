package com.rxd.drawandguess;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/27.
 */

public class StrokeWidthChooseView extends View {

    private Paint mPaint;
    private Paint mChoosePaint;
    private Paint mWhitePaint;
    private int mWidth;
    private int mHeight;
    private int[] widthArray = new int[]{10, 20, 30, 40};
    private List<Rect> rectList;
    private List<Region> regionList;
    private int mPaintColor = Color.parseColor("#000000");
    private int mCurrentIndex = 0;
    private int downX, downY;
    private DrawView drawView;
    private DrawView.State mCurrentState = DrawView.State.PEN;

    public StrokeWidthChooseView(Context context) {
        this(context, null);
    }

    public StrokeWidthChooseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mChoosePaint = new Paint();
        mChoosePaint.setAntiAlias(true);
        mChoosePaint.setStyle(Paint.Style.FILL);


        mWhitePaint = new Paint();
        mWhitePaint.setAntiAlias(true);
        mWhitePaint.setStyle(Paint.Style.FILL);
        mWhitePaint.setColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        rectList = new ArrayList<>();
        regionList = new ArrayList<>();
        int perWidth = mWidth / widthArray.length;
        Rect rect;
        Region region;
        for (int i = 0; i < widthArray.length; i++){
            rect = new Rect(perWidth * i + perWidth / 2 - widthArray[i],
                                mHeight / 2 - widthArray[i],
                                perWidth * i + perWidth / 2 + widthArray[i],
                                mHeight / 2 + widthArray[i]);
            region = new Region(rect);
            rectList.add(rect);
            regionList.add(region);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mPaintColor);

        for (int i = 0; i < rectList.size(); i++){
            Rect rect = rectList.get(i);
            float radius = widthArray[i];
            switch (mCurrentState){
                case PEN:
                    mChoosePaint.setColor(Color.WHITE);
                    if (mCurrentIndex == i){
                        canvas.drawCircle(rect.centerX(), rect.centerY(), radius + 10, mChoosePaint);
                    }
                    canvas.drawCircle(rect.centerX(), rect.centerY(), radius, mPaint);
                    break;
                case ERASER:
                    mChoosePaint.setColor(Color.BLACK);
                    if (mCurrentIndex == i){
                        canvas.drawCircle(rect.centerX(), rect.centerY(), radius + 10, mChoosePaint);
                    }
                    canvas.drawCircle(rect.centerX(), rect.centerY(), radius, mWhitePaint);
                    break;
            }

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = x;
                downY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                for (int i = 0; i < regionList.size(); i++){
                    Region region = regionList.get(i);
                    if (region.contains(x, y) && region.contains(downX, downY)){
                        mCurrentIndex = i;
                        switch (mCurrentState){
                            case PEN:
                                drawView.setmStrokeWidth(widthArray[i]);
                                break;
                            case ERASER:
                                drawView.setmEraserWidth(widthArray[i]);
                                break;
                        }

                    }
                }
                invalidate();
                break;
        }
        return true;
    }

    public void setWidthArray(int[] widthArray) {
        this.widthArray = widthArray;
    }

    public void setmPaintColor(int mPaintColor) {
        this.mPaintColor = mPaintColor;
        drawView.setmPaintColor(mPaintColor);
        invalidate();
    }

    public void setDrawView(DrawView drawView) {
        this.drawView = drawView;
        drawView.setmStrokeWidth(widthArray[0]);
        drawView.setmEraserWidth(widthArray[0]);
    }

    public void setmCurrentState(DrawView.State mCurrentState) {
        this.mCurrentState = mCurrentState;
        invalidate();
        if (mCurrentState == DrawView.State.ERASER){
            drawView.setmEraserWidth(widthArray[mCurrentIndex]);
        }else{
            drawView.setmStrokeWidth(widthArray[mCurrentIndex]);
        }
    }
}
