package com.example.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomView extends View {
    private Paint paint = new Paint();
    private Path path;
    private int color = Color.GREEN;
    private final int LEFT_TOP = 0x1;
    private final int LEFT_BOTTOM = 0x2;
    private final int RIGHT_TOP = 0x4;
    private final int RIGHT_BOTTOM = 0x8;
    private boolean drawLeftTop;
    private boolean drawLeftBottom;
    private boolean drawRightTop;
    private boolean drawRightBottom;
    private float radius;

    public CustomView(Context context) {
        super(context);
        initDraw();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        int position = LEFT_TOP | LEFT_BOTTOM | RIGHT_TOP | RIGHT_BOTTOM;
        radius = 20;
        drawLeftTop = (position & LEFT_TOP) == LEFT_TOP;
        drawLeftBottom = (position & LEFT_BOTTOM) == LEFT_BOTTOM;
        drawRightTop = (position & RIGHT_TOP) == RIGHT_TOP;
        drawRightBottom = (position & RIGHT_BOTTOM) == RIGHT_BOTTOM;
        initDraw();
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDraw();
    }

    private void initDraw() {
        path = new Path();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        RectF connerRect = new RectF();
        connerRect.set(10, 10, 310, 310);
        path.rewind();//这里很重要，如果不写这一行，则每次重绘view后先前绘制的还会存在
//        path.lineTo(100, 0);
//        path.lineTo(100, 100);
//        path.lineTo(0, 100);
        path.moveTo(10, 10);
        path.arcTo(connerRect, 180, 270);
//        path.arcTo(connerRect, 0, 90);
        path.lineTo(310, 10);
//        path.arcTo(connerRect, 270, 90);
        path.lineTo(310, 310);
//        path.arcTo(connerRect, 180, 90);
        path.lineTo(10, 310);
////        path.lineTo(100, 0);
////        path.moveTo(100, 0);
//        path.arcTo(connerRect, 270, 90);
//
//        path.arcTo(connerRect, 90, 90);
        path.close();
        canvas.drawPath(path, paint);
        super.onDraw(canvas);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void refreshView() {
        invalidate();
    }
}