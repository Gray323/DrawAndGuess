package com.rxd.drawandguess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

/**
 * Created by Administrator on 2018/2/27.
 */

public class DrawView extends View{

    private Paint drawPaint;
    private Paint eraserPaint;
    private Paint backPaint;
    private Path mPath;
    private float mx, my;
    private Canvas mCanvas;
    private static final float TOUCH_TOLERANCE = 4;
    private Bitmap cacheBitmap;
    public enum State{
        PEN,
        ERASER
    }
    private State mCurrentState = State.PEN;
    private int mStrokeWidth;
    private int mEraserWidth;
    private int mPaintColor = Color.BLACK;
    private Stack<Path> paths;
    private Stack<Integer> states;
    private Stack<Integer> colors;
    private Stack<Float> widths;


    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){
        drawPaint = new Paint();
        drawPaint.setAntiAlias(true);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPaint.setColor(mPaintColor);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);

        backPaint = new Paint();
        backPaint.setColor(Color.WHITE);
        backPaint.setAntiAlias(true);


        eraserPaint = new Paint();
        eraserPaint.setAlpha(0);
        eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        //eraserPaint.setColor(Color.RED);
        eraserPaint.setAntiAlias(true);
        eraserPaint.setDither(true);
        eraserPaint.setStyle(Paint.Style.STROKE);
        eraserPaint.setStrokeJoin(Paint.Join.ROUND);

        mPath = new Path();
        paths = new Stack<>();
        states = new Stack<>();
        colors = new Stack<>();
        widths = new Stack<>();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cacheBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(cacheBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(cacheBitmap, 0, 0, backPaint);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                TouchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                TouchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                TouchUp();
                break;
        }
        invalidate();
        return true;
    }

    private void TouchUp() {
        mPath.lineTo(mx, my);
        drawPath();
        Path path = new Path();
        path.set(mPath);
        paths.push(path);
        if (mCurrentState == State.PEN){
            states.push(0);
            int color = drawPaint.getColor();
            colors.push(color);
            widths.push(drawPaint.getStrokeWidth());
        }else{
            states.push(1);
            int color = eraserPaint.getColor();
            colors.push(color);
            widths.push(eraserPaint.getStrokeWidth());
        }

    }

    private void TouchMove(float x, float y) {
        float dx = Math.abs(x - mx);
        float dy = Math.abs(y - my);
        if (dx >=  TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE){
            mPath.quadTo(mx, my, (x + mx) / 2, (y + my) / 2);
            mx = x;
            my = y;
            drawPath();
        }
    }

    private void TouchDown(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mx = x;
        my = y;
        drawPath();
    }

    public void setCurrentState(State state) {
       this.mCurrentState = state;
       invalidate();
    }

    public State getmCurrentState() {
        return mCurrentState;
    }

    private void drawPath(){
        switch (mCurrentState){
            case PEN:
                mCanvas.drawPath(mPath, drawPaint);
                break;
            case ERASER:
                mCanvas.drawPath(mPath, eraserPaint);
                break;
        }
    }

    public void setmStrokeWidth(int mStrokeWidth) {
        this.mStrokeWidth = mStrokeWidth;
        drawPaint.setStrokeWidth(mStrokeWidth);
    }

    public void setmPaintColor(int mPaintColor) {
        this.mPaintColor = mPaintColor;
        drawPaint.setColor(mPaintColor);
    }

    public void clearPath(){
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        mPath.reset();
        paths.clear();
        states.clear();
        colors.clear();
        widths.clear();
        invalidate();
    }

    public void back(){
        if (!paths.empty()){
            mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            mPath.reset();
            paths.pop();
            states.pop();
            colors.pop();
            widths.pop();

            Log.d("TAG", "size ==> " + paths.size());
            Paint paint1 = new Paint();
            Paint paint2 = new Paint();
            paint1.setAntiAlias(true);
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setStrokeCap(Paint.Cap.ROUND);
            paint1.setStrokeJoin(Paint.Join.ROUND);
            paint2.setAlpha(0);
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            paint2.setAntiAlias(true);
            paint2.setDither(true);
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setStrokeJoin(Paint.Join.ROUND);

            for (int i = 0; i < paths.size(); i++){
                Log.d("TAG", "empty --> " + paths.get(i).isEmpty());
                if (states.get(i) == 0){
                    paint1.setColor(colors.get(i));
                    paint1.setStrokeWidth(widths.get(i));
                    mCanvas.drawPath(paths.get(i), paint1);
                }else{
                    paint2.setColor(colors.get(i));
                    paint2.setStrokeWidth(widths.get(i));
                    mCanvas.drawPath(paths.get(i), paint2);
                }
            }
            invalidate();
        }
    }

    public void setmEraserWidth(int mEraserWidth) {
        this.mEraserWidth = mEraserWidth;
        eraserPaint.setStrokeWidth(mEraserWidth);
    }

}
