package com.rhino.viewpager.transformers;

import android.util.Log;
import android.view.View;

public class VerticalPageTransformer extends BaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        Log.d("xxx", "position = " + position);
        if (position < -1) { // [-Infinity,-1)
            // 当前页的上一页
            view.setAlpha(0);
            view.setTranslationX(view.getWidth() * -position);
        } else if (position <= 1) { // [-1,1]
            view.setAlpha(1);

            // 抵消默认幻灯片过渡
            view.setTranslationX(view.getWidth() * -position);

            //设置从上滑动到Y位置
            float yPosition = position * view.getHeight();
            view.setTranslationY(yPosition);
        } else { // (1,+Infinity]
            // 当前页的下一页
            view.setAlpha(0);
            view.setTranslationX(view.getWidth() * -position);
        }
    }
}