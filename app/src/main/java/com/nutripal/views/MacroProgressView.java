package com.nutripal.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class MacroProgressView extends View {

    private float progress = 0f;

    public MacroProgressView(Context context) {
        super(context);
    }

    public MacroProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setProgress(float value) {
        this.progress = value;
        invalidate(); // 会触发重新绘制
    }

    public float getProgress() {
        return progress;
    }

    // 如果需要自定义绘制，还可以重写 onDraw 方法
}
