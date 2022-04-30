package com.rhino.viewpager.utils;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

/**
 * @author LuoLin
 * @since Create on 2016/11/21.
 **/
public class ScrollerUtils extends android.widget.Scroller {

    private double scrollFactor = 1;

    public ScrollerUtils(Context context) {
        super(context);
    }

    public ScrollerUtils(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, (int) (duration * scrollFactor));
    }

    /**
     * get real scroll duration
     */
    public int getRealScrollDuration() {
        return (int) (getDuration() / scrollFactor);
    }

    /**
     * Set the factor by which the duration will change
     */
    public void setScrollDurationFactor(double scrollFactor) {
        this.scrollFactor = scrollFactor;
    }

}
