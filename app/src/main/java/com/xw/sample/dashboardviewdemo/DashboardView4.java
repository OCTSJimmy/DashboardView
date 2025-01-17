package com.xw.sample.dashboardviewdemo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * DashboardView style 4，仿汽车速度仪表盘
 * Created by woxingxiao on 2016-12-19.
 */
public class DashboardView4 extends View {

    private int mRadius; // 扇形半径
    private int mStartAngle = 175; // 起始角度
    private int mSweepAngle = 190; // 绘制角度
    private int mMin = 0; // 最小值
    private int mMax = 100; // 最大值
    private int mSection = 6; // 值域（mMax-mMin）等分份数
    private int mPortion = 6; // 一个mSection等分份数
    private String mHeaderText = "km/h"; // 表头
    private int mVelocity = mMin; // 实时速度
    private int mStrokeWidth; // 画笔宽度
    private int mLength1; // 长刻度的相对圆弧的长度
    private int mLength2; // 刻度读数顶部的相对圆弧的长度
    private int mPLRadius; // 指针长半径

    private int mPadding;
    private float mCenterX, mCenterY; // 圆心坐标
    private Paint mPaint;
    private RectF mRectFArc;
    private RectF mRectFInnerArc;
    private RectF mPathRectFInnerArc;
    private Rect mRectText;
    private String[] mTexts;
    private int[] mColors;
    private Path mPath;

    public DashboardView4(Context context) {
        this(context, null);
    }

    public DashboardView4(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DashboardView4(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mStrokeWidth = dp2px(1);
        mLength1 = dp2px(2) + mStrokeWidth;
        mLength2 = mLength1 + dp2px(2);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mRectFArc = new RectF();
        mRectFInnerArc = new RectF();
        mPathRectFInnerArc = new RectF();
        mRectText = new Rect();

        mTexts = new String[mSection + 1]; // 需要显示mSection + 1个刻度读数
        for (int i = 0; i < mTexts.length; i++) {
            int n = (mMax - mMin) / mSection;
            mTexts[i] = String.valueOf(mMin + i * n);
        }

        mColors = new int[]{ContextCompat.getColor(getContext(), R.color.color_green),
                ContextCompat.getColor(getContext(), R.color.color_yellow),
                ContextCompat.getColor(getContext(), R.color.color_red)};
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mPadding = Math.max(
                Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom())
        );
        setPadding(mPadding, mPadding, mPadding, mPadding);

        int width = resolveSize(getLayoutParams().width, widthMeasureSpec);
        mRadius = (width - mPadding * 2 - mStrokeWidth * 2) / 2;

        // 由起始角度确定的高度
        float[] point1 = getCoordinatePoint(mRadius, mStartAngle);
        // 由结束角度确定的高度
        float[] point2 = getCoordinatePoint(mRadius, mStartAngle + mSweepAngle);
        int height = (int) Math.max(point1[1] + mStrokeWidth * 2 + dp2px(5),
                point2[1] + mStrokeWidth * 2 + dp2px(5));
        setMeasuredDimension(width, height + getPaddingTop() + getPaddingBottom());

        mCenterX = mCenterY = getMeasuredWidth() / 2f;
        mRectFArc.set(
                getPaddingLeft() + mStrokeWidth,
                getPaddingTop() + mStrokeWidth,
                getMeasuredWidth() - getPaddingRight() - mStrokeWidth,
                getMeasuredWidth() - getPaddingBottom() - mStrokeWidth
        );

        mPaint.setTextSize(sp2px(8));
        mPaint.getTextBounds("0", 0, "0".length(), mRectText);
        mRectFInnerArc.set(
                getPaddingLeft() + mLength2 + dp2px(5),
                getPaddingTop() + mLength2 + dp2px(5),
                getMeasuredWidth() - getPaddingRight() - mLength2 - dp2px(5),
                getMeasuredWidth() - getPaddingBottom() - mLength2 - dp2px(5)
        );
        mPathRectFInnerArc.set(
                getPaddingLeft() + mLength2 + dp2px(5),
                getPaddingTop() + mLength2 + dp2px(5),
                getMeasuredWidth() - getPaddingRight() - mLength2 - dp2px(5),
                getMeasuredWidth() - getPaddingBottom() - mLength2 - dp2px(5)
        );

        mPLRadius = (int) (mRadius * 0.7);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(ContextCompat.getColor(getContext(), R.color.color_light));
        /**
         * 画圆弧
         */
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);

//        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_light));
        mPaint.setColor(Color.parseColor("#666666"));
//        canvas.drawArc(mRectFArc, mStartAngle, mSweepAngle, false, mPaint);

        /**
         * 画长刻度
         * 画好起始角度的一条刻度后通过canvas绕着原点旋转来画剩下的长刻度
         */
        double cos = Math.cos(Math.toRadians(mStartAngle - 180));
        double sin = Math.sin(Math.toRadians(mStartAngle - 180));
        float x0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - cos));
        float y0 = (float) (mPadding + mStrokeWidth + mRadius * (1 - sin));

        float x1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * cos);
        float y1 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - mLength1) * sin);

        canvas.save();
        canvas.drawLine(x0, y0, x1, y1, mPaint);
        float angle = mSweepAngle * 1f / mSection;
        for (int i = 0; i < mSection; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            canvas.drawLine(x0, y0, x1, y1, mPaint);
        }
        canvas.restore();

        /**
         * 画短刻度
         * 同样采用canvas的旋转原理
         */
        canvas.save();
        mPaint.setColor(Color.parseColor("#79c4e6"));
        mPaint.setStrokeWidth(mStrokeWidth / 2f);
        float x2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - 2 * mLength1 / 2f) * cos);
        float y2 = (float) (mPadding + mStrokeWidth + mRadius - (mRadius - 2 * mLength1 / 2f) * sin);
//        canvas.drawLine(x0, y0, x2, y2, mPaint);
        angle = mSweepAngle * 1f / (mSection * mPortion);
        for (int i = 1; i < mSection * mPortion; i++) {
            canvas.rotate(angle, mCenterX, mCenterY);
            if (i % mPortion == 0) { // 避免与长刻度画重合
                continue;
            }
            canvas.drawLine(x0, y0, x2, y2, mPaint);
        }
        canvas.restore();

        /**
         * 画长刻度读数
         */
        mPaint.setTextSize(sp2px(8));
        mPaint.setStyle(Paint.Style.FILL);
        float α;
        float[] p;
        angle = mSweepAngle * 1f / mSection;
        for (int i = 0; i <= mSection; i++) {
            α = mStartAngle + angle * i;
            p = getCoordinatePoint(mRadius - mLength2, α);
            if (α % 360 > 135 && α % 360 < 225) {
                mPaint.setTextAlign(Paint.Align.LEFT);
            } else if ((α % 360 >= 0 && α % 360 < 45) || (α % 360 > 315 && α % 360 <= 360)) {
                mPaint.setTextAlign(Paint.Align.RIGHT);
            } else {
                mPaint.setTextAlign(Paint.Align.CENTER);
            }

/*            if (i == 0) {
                mPaint.getTextBounds("正常", 0, "正常".length(), mRectText);
                int txtH = mRectText.height();
                canvas.drawText("正常", p[0] + mLength2 + dp2px(10), p[1] + txtH / 2, mPaint);
            } else if (i == 1) {
                mPaint.getTextBounds("较高", 0, "较高".length(), mRectText);
                int txtH = mRectText.height();
                canvas.drawText("较高", p[0] + mLength2 + dp2px(10), p[1] + txtH / 2, mPaint);
            } else if (i == 2) {
                mPaint.getTextBounds("极高", 0, "极高".length(), mRectText);
                int txtH = mRectText.height();
                canvas.drawText("极高", p[0] + mLength2 + dp2px(5), p[1] + txtH / 2 + mLength2 + dp2px(5), mPaint);
            }*/
        }

        /**
         * 画内圈圆弧
         */

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dp2px(8));

//        mPaint.setShader(generateSweepGradient());
//        canvas.drawArc(mRectFInnerArc, 180 - mStartAngle, (-mSweepAngle), false, mPaint);

        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setAntiAlias(true);

        mPaint.setColor(Color.parseColor("#ef3a35"));
        canvas.drawArc(mRectFInnerArc, 180 - mStartAngle, (-mSweepAngle / 3.0f), false, mPaint);
        mPath.reset();
        mPath.addArc(mRectFInnerArc, -mSweepAngle / 3.f / 2, 60);

        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_light));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(8));
        mPaint.getTextBounds("极高", 0, "极高".length(), mRectText);
        canvas.drawTextOnPath("极高", mPath, 0, mRectText.height() / 2 - 1, mPaint);


        mPaint.setColor(Color.parseColor("#efba11"));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextAlign(Paint.Align.LEFT);

        canvas.drawArc(mRectFInnerArc, 180 - mStartAngle - (mSweepAngle / 3.0f), (-mSweepAngle / 3.0f), false, mPaint);
        mPath.reset();
        mPath.addArc(mRectFInnerArc, -mSweepAngle / 2f - 2, 60);

        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_light));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(8));
        mPaint.getTextBounds("较高", 0, "较高".length(), mRectText);
        canvas.drawTextOnPath("较高", mPath, 0, mRectText.height() / 2f - 1, mPaint);

        mPaint.setColor(Color.parseColor("#1ed6d6"));
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setTextAlign(Paint.Align.LEFT);

        canvas.drawArc(mRectFInnerArc, 180 - mStartAngle - (mSweepAngle / 3.0f * 2), (-mSweepAngle / 3.0f), false, mPaint);
        mPath.reset();

        mPath.addArc(mRectFInnerArc, (float) (mStartAngle + mSweepAngle / 6f - 90 * (mRectText.width()) / (Math.PI * mRadius)), 45);

        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_light));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(sp2px(8));
        mPaint.getTextBounds("正常", 0, "正常".length(), mRectText);
        canvas.drawTextOnPath("正常", mPath, 0, mRectText.height() / 2 - 1, mPaint);

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(dp2px(16));

/*
        canvas.drawText("较高", mSweepAngle * 1f / 3f - mRectText.width() / 2, mLength1 + mRectText.height() + dp2px(10), mPaint);

        canvas.drawText("极高", mSweepAngle * 1f / 3f - mRectText.width() / 2, mLength1 + mRectText.height() + dp2px(10), mPaint);
*/


        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(null);
        mPaint.setColor(Color.parseColor("#1296db"));


        /**
         * 画指针
         */


        float θ = mStartAngle + mSweepAngle * (mVelocity - mMin) / (mMax - mMin); // 指针与水平线夹角
        float[] p1 = getCoordinatePoint(mPLRadius, θ);
        int r = mRadius / 8;

//        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_dark_light));

        //圆心
        canvas.drawCircle(mCenterX, mCenterY, r, mPaint);
        mPaint.setStrokeWidth(r / 3);
//        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.color_light));

        canvas.drawLine(p1[0], p1[1], mCenterX, mCenterY, mPaint);
//        canvas.drawLine(mCenterX, mCenterY, p2[0], p2[1], mPaint);

        /**
         * 画实时度数值
         */
        /*
        mPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mPaint.setStrokeWidth(dp2px(2));
        int xOffset = dp2px(22);
        if (mVelocity >= 100) {
            drawDigitalTube(canvas, mVelocity / 100, -xOffset);
            drawDigitalTube(canvas, (mVelocity - 100) / 10, 0);
            drawDigitalTube(canvas, mVelocity % 100 % 10, xOffset);
        } else if (mVelocity >= 10) {
            drawDigitalTube(canvas, -1, -xOffset);
            drawDigitalTube(canvas, mVelocity / 10, 0);
            drawDigitalTube(canvas, mVelocity % 10, xOffset);
        } else {
            drawDigitalTube(canvas, -1, -xOffset);
            drawDigitalTube(canvas, -1, 0);
            drawDigitalTube(canvas, mVelocity, xOffset);
        }*/
    }

    /**
     * 数码管样式
     */
    //      1
    //      ——
    //   2 |  | 3
    //      —— 4
    //   5 |  | 6
    //      ——
    //       7
    private void drawDigitalTube(Canvas canvas, int num, int xOffset) {
        float x = mCenterX + xOffset;
        float y = mCenterY + dp2px(40);
        int lx = dp2px(5);
        int ly = dp2px(10);
        int gap = dp2px(2);

        // 1
        mPaint.setAlpha(num == -1 || num == 1 || num == 4 ? 25 : 255);
        canvas.drawLine(x - lx, y, x + lx, y, mPaint);
        // 2
        mPaint.setAlpha(num == -1 || num == 1 || num == 2 || num == 3 || num == 7 ? 25 : 255);
        canvas.drawLine(x - lx - gap, y + gap, x - lx - gap, y + gap + ly, mPaint);
        // 3
        mPaint.setAlpha(num == -1 || num == 5 || num == 6 ? 25 : 255);
        canvas.drawLine(x + lx + gap, y + gap, x + lx + gap, y + gap + ly, mPaint);
        // 4
        mPaint.setAlpha(num == -1 || num == 0 || num == 1 || num == 7 ? 25 : 255);
        canvas.drawLine(x - lx, y + gap * 2 + ly, x + lx, y + gap * 2 + ly, mPaint);
        // 5
        mPaint.setAlpha(num == -1 || num == 1 || num == 3 || num == 4 || num == 5 || num == 7
                || num == 9 ? 25 : 255);
        canvas.drawLine(x - lx - gap, y + gap * 3 + ly,
                x - lx - gap, y + gap * 3 + ly * 2, mPaint);
        // 6
        mPaint.setAlpha(num == -1 || num == 2 ? 25 : 255);
        canvas.drawLine(x + lx + gap, y + gap * 3 + ly,
                x + lx + gap, y + gap * 3 + ly * 2, mPaint);
        // 7
        mPaint.setAlpha(num == -1 || num == 1 || num == 4 || num == 7 ? 25 : 255);
        canvas.drawLine(x - lx, y + gap * 4 + ly * 2, x + lx, y + gap * 4 + ly * 2, mPaint);
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    public float[] getCoordinatePoint(int radius, float angle) {
        float[] point = new float[2];

        double arcAngle = Math.toRadians(angle); //将角度转换为弧度
        if (angle < 90) {
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 90) {
            point[0] = mCenterX;
            point[1] = mCenterY + radius;
        } else if (angle > 90 && angle < 180) {
            arcAngle = Math.PI * (180 - angle) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY + Math.sin(arcAngle) * radius);
        } else if (angle == 180) {
            point[0] = mCenterX - radius;
            point[1] = mCenterY;
        } else if (angle > 180 && angle < 270) {
            arcAngle = Math.PI * (angle - 180) / 180.0;
            point[0] = (float) (mCenterX - Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        } else if (angle == 270) {
            point[0] = mCenterX;
            point[1] = mCenterY - radius;
        } else {
            arcAngle = Math.PI * (360 - angle) / 180.0;
            point[0] = (float) (mCenterX + Math.cos(arcAngle) * radius);
            point[1] = (float) (mCenterY - Math.sin(arcAngle) * radius);
        }

        return point;
    }

    private SweepGradient generateSweepGradient() {
        SweepGradient sweepGradient = new SweepGradient(mCenterX, mCenterY,
                mColors,
                new float[]{0, 0.1f, 0.25f}
        );

        Matrix matrix = new Matrix();
        matrix.setRotate(mStartAngle - 3, mCenterX, mCenterY);
        sweepGradient.setLocalMatrix(matrix);

        return sweepGradient;
    }

    public int getVelocity() {
        return mVelocity;
    }

    public void setVelocity(int velocity) {
        if (mVelocity == velocity || velocity < mMin || velocity > mMax) {
            return;
        }

        mVelocity = velocity;
        postInvalidate();
    }
}
