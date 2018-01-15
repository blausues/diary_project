package com.example.student.diary_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 2018-01-15.
 */

public class DrawView extends View{
    private List<Path> pathList = new ArrayList<>();
    private List<Paint> paintList = new ArrayList<>();
    private Paint currentPaint;
    private Path currentPath;

    public DrawView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        currentPaint = new Paint();
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(10f);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(Color.BLACK);

        currentPath = new Path();

        pathList.add(currentPath);
        paintList.add(currentPaint);
    }

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
                currentPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                currentPath.lineTo(x, y);
                break;
        }
        invalidate();
        return true;
    }

    public void selColor(int color) {
        currentPaint = new Paint();
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(10f);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setAntiAlias(true);
        currentPaint.setColor(color);

        currentPath = new Path();

        pathList.add(currentPath);
        paintList.add(currentPaint);
    }

    public void setClear(int deleteColor) {
        if (deleteColor != 0) {
            for (int x = 0; x < pathList.size(); x++) {
                if (paintList.get(x).getColor() == deleteColor) {
                    pathList.get(x).reset();
                }
            }
        } else {
            for (int x = 0; x < pathList.size(); x++) {
                pathList.get(x).reset();
            }
        }
        invalidate();
    }

    public void printBack() {
        currentPath.reset();
        invalidate();
    }
}
