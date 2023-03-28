package com.example.scrollview;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private TextView textView;
    private TextView textView2;
    private ScrollView scrollView;
    private SliderBarView sliderBarView;
    private int height;
    private int contentHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv);
        textView2 = findViewById(R.id.tv2);
        scrollView = findViewById(R.id.sv);
        sliderBarView = findViewById(R.id.sb);
        StringBuilder tmp = new StringBuilder();
        for(int i=0; i < 100 ; i++){
            tmp.append("Android：深度探究线性布局LinearLayout中权重（layout_weight）属性");
        }
        textView.setText(tmp.toString());
        textView2.setText("------------\n==================\n--------------");

        // 设置滚动条不可见
        sliderBarView.setVisibility(View.INVISIBLE);

        // 异步获取scrollview高度
        scrollView.post(() -> { // 这样才能获取到渲染后的高度
            height = scrollView.getHeight();
            contentHeight = scrollView.getChildAt(0).getHeight();
            Log.e(TAG, "height " + height);
            Log.e(TAG, "contentHeight " + contentHeight);
            // 设置滚动条高度和scrollview内容最大高度
            sliderBarView.setScroll(contentHeight-height, height);
            // 设置滑块的高度，这里自适应scrollview内容最大高度的十分之一
            sliderBarView.setThumbHeight((contentHeight-height)/10);
        });

        // 监听触摸事件，设置滚动条可见
        scrollView.setOnTouchListener((view, motionEvent) -> {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    sliderBarView.setVisibility(View.VISIBLE);
                    return false;
                case MotionEvent.ACTION_UP:
                    sliderBarView.setHide();
                    return true;
                default:
                    return false;
            }
        });

        // 监听滚动条拖拉事件，拖动scrollview
        sliderBarView.setOnScrollChangeListener(scrollDistance -> scrollView.scrollTo(0, scrollDistance));

        // 监听scrollview 滚动变换事件，拖动滑块
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener((view, x, y, x2, y2) -> {
                float c = contentHeight - height;
                float h = height;
                float progress = (float) y * (h/c) / h;
                sliderBarView.setProgress(progress);
            });
        }
    }
}