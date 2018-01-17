package com.example.student.diary_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 2017-12-14.
 */

public class DrawView extends View {
    private List<Path> pathList = new ArrayList<>();
    private List<Paint> paintList = new ArrayList<>();
    private Paint currentPaint;
    private Path currentPath;
    private int currentColor = Color.BLACK;
    private float currentFont = 10f;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        makePaintPath();
    }


    //    private boolean isFirst = true;
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (int x = 0; x < pathList.size(); x++) {
            canvas.drawPath(pathList.get(x), paintList.get(x));
        }
        canvas.drawPath(currentPath, currentPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                makePaintPath();
                currentPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                Log.d("hjw", "ddd");
                break;
            case MotionEvent.ACTION_UP:
                pathList.add(currentPath);
                paintList.add(currentPaint);
                makePaintPath();
                break;
        }

        invalidate();
        return true;
    }

    public void makePaintPath() {
        currentPaint = new Paint();
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(currentFont);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(currentColor);

        currentPath = new Path();
    }

    public void selColor(int color) {
        currentFont = 10f;
        currentColor = color;
    }

    public void eraser(int color){
        currentFont = 25f;
        currentColor = color;
    }

    public void printBack() {
        if (pathList.size() != 0) {
            pathList.remove(pathList.size() - 1);
            paintList.remove(paintList.size() - 1);
            invalidate();
        }
    }

    public void setClear() {
        pathList.clear();
        paintList.clear();
        invalidate();
    }
}