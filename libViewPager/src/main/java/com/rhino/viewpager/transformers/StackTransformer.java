package com.rhino.viewpager.transformers;

import android.view.View;

public class StackTransformer extends BaseTransformer {

    @Override
    protected void onTransform(View view, float position) {
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            view.setAlpha(1);
            view.setTranslationX(0);
        } else if (position <= 1) { // (0,1]
            view.setAlpha(1);
            view.setTranslationX(view.getWidth() * -position);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }

}
