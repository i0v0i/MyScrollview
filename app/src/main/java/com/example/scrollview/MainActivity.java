package com.example.scrollview;

import android.os.Build;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private TextView textView;
    private ScrollView scrollView;
    private SliderBarView sliderBarView;
    private int height;
    private int contentHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tv);
        scrollView = findViewById(R.id.sv);
        sliderBarView = findViewById(R.id.sb);
        StringBuilder tmp = new StringBuilder();
        for(int i=0; i < 100 ; i++){
            tmp.append("Android：深度探究线性布局LinearLayout中权重（layout_weight）属性");
        }
        textView.setText(tmp.toString());

        scrollView.post(() -> { // 这样才能获取到渲染后的高度
            height = scrollView.getHeight();
            contentHeight = scrollView.getChildAt(0).getHeight();
            sliderBarView.setScroll(contentHeight, height);
        });
        sliderBarView.setOnScrollChangeListener(scrollDistance -> scrollView.scrollTo(0, scrollDistance));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            scrollView.setOnScrollChangeListener((view, x, y, x2, y2) -> {
                float c = contentHeight;
                float h = height;
                float progress = (float) y * (h/c) / h;
                sliderBarView.setProgress(progress);
            });
        }
    }
}