package com.rhino.viewpager.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class ScrollAbleViewPager extends ViewPager {

    private boolean scrollable = true;

    public ScrollAbleViewPager(Context context) {
        super(context);
    }

    public ScrollAbleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!scrollable) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!scrollable) {
            return false;
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void setScrollable(boolean scrollable) {
        scrollable = scrollable;
    }

    public boolean isScrollable() {
        return scrollable;
    }
}
