package com.example.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

public class PathAnimView extends View {
    private static final int INVALIDATE_TIMES = 100; //总共执行100次

    private Paint mPaint;
    private Bitmap mBitmap;
    private int mOffsetX, mOffsetY; // 图片的中间位置

    private Path mAnimPath;
    private PathMeasure mPathMeasure;
    private float mPathLength;

    private double mStep;            //distance each step
    private float mDistance;        //distance moved

    private float[] mPos;
    private float[] mTan;

    private Matrix mMatrix;

    public PathAnimView(Context context) {
        this(context, null);
    }

    public PathAnimView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PathAnimView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPathView();
    }

    public void start() {
        mDistance = 0;
        invalidate();
    }

    public void initPathView() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(10);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setAntiAlias(true);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.tuowei_235_00000);

        mOffsetX = mBitmap.getWidth() / 2;
        mOffsetY = mBitmap.getHeight() / 2;

        mAnimPath = new Path();
        mAnimPath.addArc(200, 200, 400, 400, -225, 225);
        mAnimPath.arcTo(400, 200, 600, 400, -180, 225, false);
        mAnimPath.lineTo(400, 542);
        mAnimPath.close();

        mPathMeasure = new PathMeasure(mAnimPath, false);
        mPathLength = mPathMeasure.getLength();

        mStep = mPathLength / INVALIDATE_TIMES;
        mDistance = mPathLength;
        mPos = new float[2];
        mTan = new float[2];

        mMatrix = new Matrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mAnimPath == null || mPaint == null) {
            return;
        }
        canvas.drawPath(mAnimPath, mPaint);

        if (mDistance < mPathLength) {
            mPathMeasure.getPosTan(mDistance, mPos, mTan);
            mMatrix.reset();
            mMatrix.postTranslate(-mOffsetX, -mOffsetY);
            float degrees = (float) (Math.atan2(mTan[1], mTan[0]) * 180.0 / Math.PI);
            mMatrix.postRotate(degrees);
            mMatrix.postTranslate(mPos[0], mPos[1]);
            canvas.drawBitmap(mBitmap, mMatrix, null);
            mDistance += mStep;
            invalidate();
        } else {
            canvas.drawBitmap(mBitmap, mMatrix, null);
        }
    }
}