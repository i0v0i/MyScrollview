package com.example.scrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

public class SliderBarView extends View {
    private String TAG = "SliderBarView";
    private int mWidth;
    private int mHeight;
    private int contentHeight;
    private int mThumbHeight;
    private float mProgress;
    private boolean touchFlag;
    private Drawable mThumbDrawable;

    private OnScrollChangeListener onScrollChangeListener;

    public SliderBarView(Context context) {
        super(context);
    }

    public SliderBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        mThumbDrawable = AppCompatResources.getDrawable(context, R.drawable.thumb);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getDefaultSize(mThumbHeight, widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mThumbDrawable.setBounds(0, (int) (getThumbY() - mThumbHeight / 2f), mWidth, (int) (getThumbY() + mThumbHeight / 2f));
        mThumbDrawable.draw(canvas);
    }

    private float getThumbY() {
        float maxThumbY = mHeight - mThumbHeight;
        if (mProgress > 1f) {
            mProgress = 1f;
        } else if (mProgress < 0f) {
            mProgress = 0f;
        }
        return mThumbHeight / 2f + mProgress * maxThumbY;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchFlag = true;
                return true;
            case MotionEvent.ACTION_MOVE:
                touchFlag = true;
                mProgress = (event.getY() - mThumbHeight / 2f) / (mHeight - mThumbHeight / 2f);
                invalidate();
                // 计算滑动距离
                int scrollDistance = (int) (mProgress * (contentHeight - mHeight));
                if (onScrollChangeListener != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        onScrollChangeListener.onScrollChange(scrollDistance);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
                touchFlag = false;
                postDelayed(() -> setVisibility(INVISIBLE), 3000);
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public interface OnScrollChangeListener {
        void onScrollChange(int scrollDistance);
    }
    public void setOnScrollChangeListener(OnScrollChangeListener listener) {
        this.onScrollChangeListener = listener;
    }

    public void setViewScrollY(float scrollY) {
        mProgress = scrollY / (float) (contentHeight - mHeight);
        invalidate();
    }
    public void setScroll(int contentHeight, int mHeight) {
        this.contentHeight = contentHeight;
        this.mHeight = mHeight;
        // 自适应计算滑动高度
        int h = contentHeight - mHeight;
        if (h < 0) {
            mThumbHeight = mHeight;
        } else if (h < mHeight) {
            mThumbHeight = mHeight - h;
        } else {
            mThumbHeight = (int) (mHeight * ((float) mHeight / (float) h));
        }
        invalidate();
    }

    public void setHide() {
        if (!touchFlag) {
            postDelayed(() -> setVisibility(INVISIBLE), 3000);
        }
    }
}
