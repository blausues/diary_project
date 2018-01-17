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

    private int undoCheck = 1;

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
                Log.d("hjw","ddd");
                paintList.add(currentPaint);
                pathList.add(currentPath);
                break;
//            case MotionEvent.ACTION_UP:
//
//                pathList.add(currentPath);
//                paintList.add(currentPaint);
//                currentPath = new Path();
//                break;
        }

        invalidate();
        return true;
    }

    public void makePaintPath() {
        currentPaint = new Paint();
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(10f);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(currentColor);

        currentPath = new Path();
    }
    public void selColor(int color){
        currentColor = color;
        currentPaint.setColor(color);
    }
    public void setClear() {
        for (int x = 0; x < pathList.size(); x++) {
            pathList.get(x).reset();
            paintList.get(x).reset();
        }
        invalidate();
    }

    public void printBack() {
        pathList.get(pathList.size()-1).reset();
        paintList.get(paintList.size()-1).reset();

        invalidate();
    }
}