package com.example.myapplication;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.RequiresApi;


/**
 * 阴影控件
 */

public class ShadowLayout extends View {

    private int mShadowColor;
    private float mShadowLimit;
    private float mCornerRadius;
    private float mDx;
    private float mDy;
    private boolean leftShow;
    private boolean rightShow;
    private boolean topShow;
    private boolean bottomShow;
    private Paint shadowPaint;

    private int leftPadding;
    private int topPadding;
    private int rightPadding;
    private int bottomPadding;
    //阴影布局子空间区域
    private final RectF rectf = new RectF();
    private boolean isShowShadow = true;
    private boolean isSym;

    //增加各个圆角的属性
    private float mCornerRadius_leftTop;
    private float mCornerRadius_rightTop;
    private float mCornerRadius_leftBottom;
    private float mCornerRadius_rightBottom;

    public ShadowLayout(Context context) {
        this(context, null);
    }

    public ShadowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public ShadowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }


    //增加selector样式
    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (getWidth() == 0) {
            addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    removeOnLayoutChangeListener(this);
                    setSelected(isSelected());
                }
            });
        }
    }


    //是否隐藏阴影
    public void setShadowHidden(boolean isShowShadow) {
        this.isShowShadow = !isShowShadow;
        if (getWidth() != 0 && getHeight() != 0) {
            setBackgroundCompat(getWidth(), getHeight());
        }
    }

    //设置x轴偏移量
    public void setShadowOffsetX(float mDx) {
        if (isShowShadow) {
            if (Math.abs(mDx) > mShadowLimit) {
                if (mDx > 0) {
                    this.mDx = mShadowLimit;
                } else {
                    this.mDx = -mShadowLimit;
                }
            } else {
                this.mDx = mDx;
            }
            setPadding();
        }
    }

    //动态设置y轴偏移量
    public void setShadowOffsetY(float mDy) {
        if (isShowShadow) {
            if (Math.abs(mDy) > mShadowLimit) {
                if (mDy > 0) {
                    this.mDy = mShadowLimit;
                } else {
                    this.mDy = -mShadowLimit;
                }
            } else {
                this.mDy = mDy;
            }
            setPadding();
        }
    }


    public float getCornerRadius() {
        return mCornerRadius;
    }

    //动态设置 圆角属性
    public void setCornerRadius(int mCornerRadius) {
        this.mCornerRadius = mCornerRadius;
        if (getWidth() != 0 && getHeight() != 0) {
            setBackgroundCompat(getWidth(), getHeight());
        }
    }

    public float getShadowLimit() {
        return mShadowLimit;
    }

    //动态设置阴影扩散区域
    public void setShadowLimit(int mShadowLimit) {
        if (isShowShadow) {
            int dip5 = dip2px(5f);
            this.mShadowLimit = Math.max(mShadowLimit, dip5);
            setPadding();
        }
    }

    //动态设置阴影颜色值
    public void setShadowColor(int mShadowColor) {
        this.mShadowColor = mShadowColor;
        if (getWidth() != 0 && getHeight() != 0) {
            setBackgroundCompat(getWidth(), getHeight());
        }
    }


    public void setSpecialCorner(int leftTop, int rightTop, int leftBottom, int rightBottom) {
        mCornerRadius_leftTop = leftTop;
        mCornerRadius_rightTop = rightTop;
        mCornerRadius_leftBottom = leftBottom;
        mCornerRadius_rightBottom = rightBottom;
        if (getWidth() != 0 && getHeight() != 0) {
            setBackgroundCompat(getWidth(), getHeight());
        }
    }


    //是否隐藏阴影的上边部分
    public void setShadowHiddenTop(boolean topShow) {
        this.topShow = !topShow;
        setPadding();
    }

    public void setShadowHiddenBottom(boolean bottomShow) {
        this.bottomShow = !bottomShow;
        setPadding();
    }


    public void setShadowHiddenRight(boolean rightShow) {
        this.rightShow = !rightShow;
        setPadding();
    }


    public void setShadowHiddenLeft(boolean leftShow) {
        this.leftShow = !leftShow;
        setPadding();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0 && h > 0) {
            setBackgroundCompat(w, h);
        }
    }

    private void initView(Context context, AttributeSet attrs) {
        initAttributes(attrs);
        shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setStyle(Paint.Style.FILL);
        setPadding();
    }

    public int dip2px(float dipValue) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public void setPadding() {
        if (isShowShadow && mShadowLimit > 0) {
            //控件区域是否对称，默认是对称。不对称的话，那么控件区域随着阴影区域走
            if (isSym) {
                int xPadding = (int) (mShadowLimit + Math.abs(mDx));
                int yPadding = (int) (mShadowLimit + Math.abs(mDy));

                if (leftShow) {
                    leftPadding = xPadding;
                } else {
                    leftPadding = 0;
                }

                if (topShow) {
                    topPadding = yPadding;
                } else {
                    topPadding = 0;
                }


                if (rightShow) {
                    rightPadding = xPadding;
                } else {
                    rightPadding = 0;
                }

                if (bottomShow) {
                    bottomPadding = yPadding;
                } else {
                    bottomPadding = 0;
                }
            } else {
                if (Math.abs(mDy) > mShadowLimit) {
                    if (mDy > 0) {
                        mDy = mShadowLimit;
                    } else {
                        mDy = 0 - mShadowLimit;
                    }
                }


                if (Math.abs(mDx) > mShadowLimit) {
                    if (mDx > 0) {
                        mDx = mShadowLimit;
                    } else {
                        mDx = 0 - mShadowLimit;
                    }
                }

                if (topShow) {
                    topPadding = (int) (mShadowLimit - mDy);
                } else {
                    topPadding = 0;
                }

                if (bottomShow) {
                    bottomPadding = (int) (mShadowLimit + mDy);
                } else {
                    bottomPadding = 0;
                }


                if (rightShow) {
                    rightPadding = (int) (mShadowLimit - mDx);
                } else {
                    rightPadding = 0;
                }

                if (leftShow) {
                    leftPadding = (int) (mShadowLimit + mDx);
                } else {
                    leftPadding = 0;
                }
            }
            setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @SuppressWarnings("deprecation")
    private void setBackgroundCompat(int w, int h) {
        if (isShowShadow) {
            //判断传入的颜色值是否有透明度
            isAddAlpha(mShadowColor);
            Bitmap bitmap = createShadowBitmap(w, h, mCornerRadius, mShadowLimit, mDx, mDy, mShadowColor, Color.TRANSPARENT);
            BitmapDrawable drawable = new BitmapDrawable(bitmap);
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                setBackgroundDrawable(drawable);
            } else {
                setBackground(drawable);
            }
        } else {
            this.setBackgroundColor(Color.parseColor("#00000000"));
        }

    }


    private void initAttributes(AttributeSet attrs) {
        TypedArray attr = getContext().obtainStyledAttributes(attrs, R.styleable.ShadowLayout);

        try {
            //默认是显示
            isShowShadow = !attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHidden, false);
            leftShow = !attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHiddenLeft, false);
            rightShow = !attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHiddenRight, false);
            bottomShow = !attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHiddenBottom, false);
            topShow = !attr.getBoolean(R.styleable.ShadowLayout_hl_shadowHiddenTop, false);
            mCornerRadius = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius, 0);
            mCornerRadius_leftTop = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius_leftTop, -1);
            mCornerRadius_leftBottom = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius_leftBottom, -1);
            mCornerRadius_rightTop = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius_rightTop, -1);
            mCornerRadius_rightBottom = attr.getDimension(R.styleable.ShadowLayout_hl_cornerRadius_rightBottom, -1);

            //默认扩散区域宽度
            mShadowLimit = attr.getDimension(R.styleable.ShadowLayout_hl_shadowLimit, 0);
            if (mShadowLimit == 0) {
                //如果阴影没有设置阴影扩散区域，那么默认隐藏阴影
                isShowShadow = false;
            } else {
                int dip5 = dip2px(5f);
                if (mShadowLimit < dip5) {
                    mShadowLimit = dip5;
                }
            }

            //x轴偏移量
            mDx = attr.getDimension(R.styleable.ShadowLayout_hl_shadowOffsetX, 0);
            //y轴偏移量
            mDy = attr.getDimension(R.styleable.ShadowLayout_hl_shadowOffsetY, 0);
            mShadowColor = attr.getColor(R.styleable.ShadowLayout_hl_shadowColor, Color.parseColor("#FFFFFF"));

            isSym = attr.getBoolean(R.styleable.ShadowLayout_hl_shadowSymmetry, true);
        } finally {
            attr.recycle();
        }
    }


    private Bitmap createShadowBitmap(int shadowWidth, int shadowHeight, float cornerRadius, float shadowRadius,
                                      float dx, float dy, int shadowColor, int fillColor) {
        //优化阴影bitmap大小,将尺寸缩小至原来的1/4。
        dx = dx / 4;
        dy = dy / 4;
        shadowWidth = shadowWidth / 4 == 0 ? 1 : shadowWidth / 4;
        shadowHeight = shadowHeight / 4 == 0 ? 1 : shadowHeight / 4;
        cornerRadius = cornerRadius / 4;
        shadowRadius = shadowRadius / 4;

        Bitmap output = Bitmap.createBitmap(shadowWidth, shadowHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        float rect_left;
        float rect_right;
        float rect_top;
        float rect_bottom;
        if (leftShow) {
            rect_left = shadowRadius;
        } else {
            float maxLeftTop = Math.max(cornerRadius, mCornerRadius_leftTop);
            float maxLeftBottom = Math.max(cornerRadius, mCornerRadius_leftBottom);
            rect_left = Math.max(maxLeftTop, maxLeftBottom);
        }

        if (topShow) {
            rect_top = shadowRadius;
        } else {
            float maxLeftTop = Math.max(cornerRadius, mCornerRadius_leftTop);
            float maxRightTop = Math.max(cornerRadius, mCornerRadius_rightTop);
            rect_top = Math.max(maxLeftTop, maxRightTop);
        }

        if (rightShow) {
            rect_right = shadowWidth - shadowRadius;
        } else {
            float maxRightTop = Math.max(cornerRadius, mCornerRadius_rightTop);
            float maxRightBottom = Math.max(cornerRadius, mCornerRadius_rightBottom);
            float maxRight = Math.max(maxRightTop, maxRightBottom);
            rect_right = shadowWidth - maxRight;
        }

        if (bottomShow) {
            rect_bottom = shadowHeight - shadowRadius;
        } else {
            float maxLeftBottom = Math.max(cornerRadius, mCornerRadius_leftBottom);
            float maxRightBottom = Math.max(cornerRadius, mCornerRadius_rightBottom);
            float maxBottom = Math.max(maxLeftBottom, maxRightBottom);
            rect_bottom = shadowHeight - maxBottom;
        }


        RectF shadowRect = new RectF(
                rect_left,
                rect_top,
                rect_right,
                rect_bottom);

        if (isSym) {
            if (dy > 0) {
                shadowRect.top += dy;
                shadowRect.bottom -= dy;
            } else if (dy < 0) {
                shadowRect.top += Math.abs(dy);
                shadowRect.bottom -= Math.abs(dy);
            }

            if (dx > 0) {
                shadowRect.left += dx;
                shadowRect.right -= dx;
            } else if (dx < 0) {

                shadowRect.left += Math.abs(dx);
                shadowRect.right -= Math.abs(dx);
            }
        } else {
            shadowRect.top -= dy;
            shadowRect.bottom -= dy;
            shadowRect.right -= dx;
            shadowRect.left -= dx;
        }


        shadowPaint.setColor(fillColor);
        shadowPaint.setShadowLayer(shadowRadius / 2, dx, dy, shadowColor);

        if (mCornerRadius_leftBottom == -1 && mCornerRadius_leftTop == -1 && mCornerRadius_rightTop == -1 && mCornerRadius_rightBottom == -1) {
            //如果没有设置整个属性，那么按原始去画
            canvas.drawRoundRect(shadowRect, cornerRadius, cornerRadius, shadowPaint);
        } else {
            //目前最佳的解决方案
            rectf.left = leftPadding;
            rectf.top = topPadding;
            rectf.right = getWidth() - rightPadding;
            rectf.bottom = getHeight() - bottomPadding;


            shadowPaint.setAntiAlias(true);
            int leftTop;
            if (mCornerRadius_leftTop == -1) {
                leftTop = (int) mCornerRadius / 4;
            } else {
                leftTop = (int) mCornerRadius_leftTop / 4;
            }
            int leftBottom;
            if (mCornerRadius_leftBottom == -1) {
                leftBottom = (int) mCornerRadius / 4;
            } else {
                leftBottom = (int) mCornerRadius_leftBottom / 4;
            }

            int rightTop;
            if (mCornerRadius_rightTop == -1) {
                rightTop = (int) mCornerRadius / 4;
            } else {
                rightTop = (int) mCornerRadius_rightTop / 4;
            }

            int rightBottom;
            if (mCornerRadius_rightBottom == -1) {
                rightBottom = (int) mCornerRadius / 4;
            } else {
                rightBottom = (int) mCornerRadius_rightBottom / 4;
            }

            float[] outerR = new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom};//左上，右上，右下，左下
            Path path = new Path();
            path.addRoundRect(shadowRect, outerR, Path.Direction.CW);
            canvas.drawPath(path, shadowPaint);
        }

        return output;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        int trueHeight = (int) (rectf.bottom - rectf.top);
        if (mCornerRadius_leftTop == -1 && mCornerRadius_leftBottom == -1 && mCornerRadius_rightTop == -1 && mCornerRadius_rightBottom == -1) {
            //说明没有设置过任何特殊角、且是半圆。
            Path path = new Path();
            if (mCornerRadius > trueHeight / 2f) {
                path.addRoundRect(rectf, trueHeight / 2f, trueHeight / 2f, Path.Direction.CW);
            } else {
                path.addRoundRect(rectf, mCornerRadius, mCornerRadius, Path.Direction.CW);
            }
            canvas.clipPath(path);
        } else {
            float[] outerR = getCornerValue(trueHeight);
            Path path = new Path();
            path.addRoundRect(leftPadding, topPadding, getWidth() - rightPadding, getHeight() - bottomPadding, outerR, Path.Direction.CW);
            canvas.clipPath(path);
        }

        super.dispatchDraw(canvas);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rectf.left = leftPadding;
        rectf.top = topPadding;
        rectf.right = getWidth() - rightPadding;
        rectf.bottom = getHeight() - bottomPadding;
        int trueHeight = (int) (rectf.bottom - rectf.top);
        //如果都为0说明，没有设置特定角，那么按正常绘制
        setSpaceCorner(canvas, trueHeight);
    }


    public float[] getCornerValue(int trueHeight) {
        int leftTop;
        int rightTop;
        int rightBottom;
        int leftBottom;
        if (mCornerRadius_leftTop == -1) {
            leftTop = (int) mCornerRadius;
        } else {
            leftTop = (int) mCornerRadius_leftTop;
        }

        if (leftTop > trueHeight / 2) {
            leftTop = trueHeight / 2;
        }

        if (mCornerRadius_rightTop == -1) {
            rightTop = (int) mCornerRadius;
        } else {
            rightTop = (int) mCornerRadius_rightTop;
        }

        if (rightTop > trueHeight / 2) {
            rightTop = trueHeight / 2;
        }

        if (mCornerRadius_rightBottom == -1) {
            rightBottom = (int) mCornerRadius;
        } else {
            rightBottom = (int) mCornerRadius_rightBottom;
        }

        if (rightBottom > trueHeight / 2) {
            rightBottom = trueHeight / 2;
        }


        if (mCornerRadius_leftBottom == -1) {
            leftBottom = (int) mCornerRadius;
        } else {
            leftBottom = (int) mCornerRadius_leftBottom;
        }

        if (leftBottom > trueHeight / 2) {
            leftBottom = trueHeight / 2;
        }

        return new float[]{leftTop, leftTop, rightTop, rightTop, rightBottom, rightBottom, leftBottom, leftBottom};
    }


    //这是自定义四个角的方法。
    private void setSpaceCorner(Canvas canvas, int trueHeight) {
        float[] outerR = getCornerValue(trueHeight);
        ShapeDrawable mDrawables = new ShapeDrawable(new RoundRectShape(outerR, null, null));
        mDrawables.setBounds(leftPadding, topPadding, getWidth() - rightPadding, getHeight() - bottomPadding);
        mDrawables.draw(canvas);
    }

    public void isAddAlpha(int color) {
        //获取单签颜色值的透明度，如果没有设置透明度，默认加上#2a
        if (Color.alpha(color) == 255) {
            String red = Integer.toHexString(Color.red(color));
            String green = Integer.toHexString(Color.green(color));
            String blue = Integer.toHexString(Color.blue(color));

            if (red.length() == 1) {
                red = "0" + red;
            }

            if (green.length() == 1) {
                green = "0" + green;
            }

            if (blue.length() == 1) {
                blue = "0" + blue;
            }
            String endColor = "#FC" + red + green + blue;
            mShadowColor = convertToColorInt(endColor);
        }
    }


    public static int convertToColorInt(String argb)
            throws IllegalArgumentException {

        if (!argb.startsWith("#")) {
            argb = "#" + argb;
        }

        return Color.parseColor(argb);
    }
}
