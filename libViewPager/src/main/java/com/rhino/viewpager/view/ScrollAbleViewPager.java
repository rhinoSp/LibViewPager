package com.rhino.viewpager.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.rhino.viewpager.transformers.DefaultTransformer;
import com.rhino.viewpager.transformers.FlipVerticalTransformer;
import com.rhino.viewpager.transformers.VerticalPageTransformer;

/**
 * @author LuoLin
 * @since Create on 2016/10/31.
 **/
public class ScrollAbleViewPager extends ViewPager {

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    private Orientation orientation = Orientation.HORIZONTAL;
    private boolean scrollable = true;
    private PageTransformer pageTransformer;

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
        return super.onTouchEvent(checkSwapXY(ev));
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!scrollable) {
            return false;
        }
        boolean intercepted = super.onInterceptTouchEvent(checkSwapXY(ev));
        checkSwapXY(ev);
        return intercepted;
    }

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, @Nullable PageTransformer transformer) {
        this.pageTransformer = transformer;
        super.setPageTransformer(reverseDrawingOrder, transformer);
        if (transformer instanceof VerticalPageTransformer
                || transformer instanceof FlipVerticalTransformer) {
            this.orientation = Orientation.VERTICAL;
        } else {
            this.orientation = Orientation.HORIZONTAL;
        }
    }

    @Override
    public void setPageTransformer(boolean reverseDrawingOrder, @Nullable PageTransformer transformer, int pageLayerType) {
        this.pageTransformer = transformer;
        super.setPageTransformer(reverseDrawingOrder, transformer, pageLayerType);
        if (transformer instanceof VerticalPageTransformer
                || transformer instanceof FlipVerticalTransformer) {
            this.orientation = Orientation.VERTICAL;
        } else {
            this.orientation = Orientation.HORIZONTAL;
        }
    }

    public PageTransformer getPageTransformer() {
        return pageTransformer;
    }

    private MotionEvent checkSwapXY(MotionEvent ev) {
        if (orientation == Orientation.HORIZONTAL) {
            return ev;
        }
        float width = getWidth();
        float height = getHeight();
        float newX = (ev.getY() / height) * width;
        float newY = (ev.getX() / width) * height;
        ev.setLocation(newX, newY);
        return ev;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
        if (orientation == Orientation.VERTICAL) {
            setPageTransformer(true, new VerticalPageTransformer());
        } else {
            setPageTransformer(true, new DefaultTransformer());
        }
    }

}
