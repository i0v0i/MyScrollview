package com.example.scrollview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

public class SliderBarView extends View {
    private String TAG = "SliderBarView";
    private int mWidth;
    private int mHeight;
    private int contentHeight;
    private int mThumbSize;
    private int mThumbColor;
    private int mTrackColor;
    private Paint mTrackPaint;
    private Paint mThumbPaint;
    private float mProgress;

    private OnScrollChangeListener onScrollChangeListener;

    public SliderBarView(Context context) {
        super(context);
    }

    public SliderBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init( context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs){
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SliderBarView);
        mThumbColor = typedArray.getColor(R.styleable.SliderBarView_thumb_color, Color.BLUE);
        mTrackColor = typedArray.getColor(R.styleable.SliderBarView_track_color, Color.GRAY);
        mThumbSize = typedArray.getDimensionPixelSize(R.styleable.SliderBarView_thumb_size, 30);
        typedArray.recycle();

        mTrackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTrackPaint.setColor(mTrackColor);
        mTrackPaint.setStyle(Paint.Style.FILL);

        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint.setColor(mThumbColor);
        mThumbPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = getDefaultSize(mThumbSize, widthMeasureSpec);
        mHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(0,0,mWidth, mHeight, mTrackPaint );
        canvas.drawCircle(mWidth / 2f, getThumbY(), mThumbSize/2f, mThumbPaint);
    }
    private float getThumbY(){
        float maxThumbY = mHeight - mThumbSize/2f;
        return Math.min(maxThumbY, Math.max(mThumbSize/2f, mProgress * maxThumbY));
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:
                mProgress = event.getY() / (mHeight-mThumbSize);
                postInvalidate();
                // 计算滑动距离
                int scrollDistance =(int) (mProgress*mHeight) *(contentHeight / mHeight);
                if(onScrollChangeListener != null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        onScrollChangeListener.onScrollChange(scrollDistance);
                    }
                }

            case MotionEvent.ACTION_UP:
                performClick();
                return true;
            default:
                return super.onTouchEvent(event);
        }
    }

    public interface OnScrollChangeListener {
        void onScrollChange(int scrollDistance);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener listener){
        this.onScrollChangeListener = listener;
    }

    public void setProgress(float progress){
        mProgress = progress;
        postInvalidate();
    }

    public void setScroll(int contentHeight, int mHeight){
        this.contentHeight = contentHeight;
        this.mHeight = mHeight;
        postInvalidate();
    }
}
