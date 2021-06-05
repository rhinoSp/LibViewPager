package com.rhino.viewpager.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;

import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.rhino.viewpager.utils.ScrollerUtils;

import java.lang.reflect.Field;

/**
 * Auto Scroll View Pager.
 *
 * @author LuoLin
 * @since Create on 2016/11/21.
 **/
public class AutoScrollViewPager extends ScrollAbleViewPager {

    public static final int DEFAULT_INTERVAL = 1500;

    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int SLIDE_BORDER_MODE_NONE = 0;
    public static final int SLIDE_BORDER_MODE_CYCLE = 1;
    public static final int SLIDE_BORDER_MODE_TO_PARENT = 2;

    /**
     * auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     **/
    private long interval = DEFAULT_INTERVAL;
    /**
     * auto scroll direction, default is {@link #RIGHT}
     **/
    private int direction = RIGHT;
    /**
     * is cycle
     */
    private boolean isCycle = true;
    /**
     * whether stop auto scroll when touching, default is true
     **/
    private boolean stopScrollWhenTouch = true;
    /**
     * the slide border mode
     */
    private int slideBorderMode = SLIDE_BORDER_MODE_NONE;
    /**
     * the border animation
     */
    private boolean isBorderAnimation = true;
    /**
     * scroll factor for auto scroll animation, default is 1.0
     **/
    private double autoScrollFactor = 1.0;
    /**
     * scroll factor for swipe scroll animation, default is 1.0
     **/
    private double swipeScrollFactor = 1.0;

    private Handler handler;
    private boolean isAutoScroll = false;
    private boolean isStopByTouch = false;
    private boolean isCanTouch = true;
    private float touchX = 0f;
    private float touchY = 0f;
    private float downX = 0f;
    private float downY = 0f;
    private ScrollerUtils scrollerUtils = null;

    public static final int SCROLL_WHAT = 0;

    public AutoScrollViewPager(Context paramContext) {
        super(paramContext);
        init();
    }

    public AutoScrollViewPager(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    private void init() {
        handler = new MyHandler(this);
        setViewPagerScroller();
    }

    public void setTouchEnable(boolean isTouchEnable) {
        this.isCanTouch = isTouchEnable;
    }

    public boolean getTouchEnable() {
        return isCanTouch;
    }

    /**
     * start auto scroll, first scroll delay time is {@link #getInterval()}
     */
    public void startAutoScroll() {
        isAutoScroll = true;
        sendScrollMessage((long) (interval + scrollerUtils.getDuration() / autoScrollFactor * swipeScrollFactor));
    }

    /**
     * start auto scroll
     *
     * @param delayTimeInMills first scroll delay time
     */
    public void startAutoScroll(int delayTimeInMills) {
        isAutoScroll = true;
        sendScrollMessage(delayTimeInMills);
    }

    /**
     * stop auto scroll
     */
    public void stopAutoScroll() {
        isAutoScroll = false;
        handler.removeMessages(SCROLL_WHAT);
    }

    /**
     * is auto scroll
     */
    public boolean isAutoScroll() {
        return isAutoScroll;
    }

    /**
     * set the factor by which the duration of sliding animation will change while swiping
     */
    public void setSwipeScrollDurationFactor(double scrollFactor) {
        swipeScrollFactor = scrollFactor;
    }

    /**
     * set the factor by which the duration of sliding animation will change while auto scrolling
     */
    public void setAutoScrollDurationFactor(double scrollFactor) {
        autoScrollFactor = scrollFactor;
    }

    /**
     * send scroll message
     */
    private void sendScrollMessage(long delayTimeInMills) {
        /** remove messages before, keeps one message is running at most **/
        handler.removeMessages(SCROLL_WHAT);
        handler.sendEmptyMessageDelayed(SCROLL_WHAT, delayTimeInMills);
    }

    /**
     * set ViewPager scrollerUtils to change animation duration when sliding
     */
    private void setViewPagerScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);

            scrollerUtils = new ScrollerUtils(getContext(), new DecelerateInterpolator(3));//(Interpolator)interpolatorField.get(null));
            scrollerField.set(this, scrollerUtils);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * scroll only once
     */
    public void scrollOnce() {
        PagerAdapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        int totalCount;
        if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
            return;
        }
        int nextItem = (direction == LEFT) ? --currentItem : ++currentItem;
        if (nextItem < 0) {
            setCurrentItem(totalCount - 1);
        } else if (nextItem == totalCount) {
            setCurrentItem(0);
        } else {
            setCurrentItem(nextItem, true);
        }
        if (nextItem < 0) {
            if (this.isCycle) {
                this.setCurrentItem(totalCount - 1, isBorderAnimation);
            }
        } else if (nextItem == totalCount) {
            if (this.isCycle) {
                this.setCurrentItem(0, isBorderAnimation);
            }
        } else {
            this.setCurrentItem(nextItem, true);
        }
    }

    /**
     * <ul>
     * if stopScrollWhenTouch is true
     * <li>if event is down, stop auto scroll.</li>
     * <li>if event is up, start auto scroll again.</li>
     * </ul>
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (this.stopScrollWhenTouch) {
            if (action == 0 && this.isAutoScroll) {
                this.isStopByTouch = true;
                this.stopAutoScroll();
            } else if (ev.getAction() == 1 && this.isStopByTouch) {
                this.startAutoScroll();
            }
        }

        if (action == 0) {
            this.downX = ev.getX();
            this.downY = ev.getY();
        }

        if (action == 2 && !this.isCanTouch) {
            return true;
        } else {
            if (action == 1 && !this.isCanTouch) {
                this.touchX = ev.getX();
                this.touchY = ev.getY();
                if (Math.abs(this.touchX - this.downX) > 15.0F || Math.abs(this.touchY - this.downY) > 15.0F) {
                    return true;
                }
            }

            if (this.slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT || this.slideBorderMode == SLIDE_BORDER_MODE_CYCLE) {
                this.touchX = ev.getX();
                if (ev.getAction() == 0) {
                    this.downX = this.touchX;
                }
                int currentItem = this.getCurrentItem();
                PagerAdapter adapter = this.getAdapter();
                int pageCount = adapter == null ? 0 : adapter.getCount();
                if (!this.isCanTouch) {
                    this.getParent().requestDisallowInterceptTouchEvent(false);
                } else if (currentItem == 0 && this.downX <= this.touchX || currentItem == pageCount - 1 && this.downX >= this.touchX) {
                    if (this.slideBorderMode == SLIDE_BORDER_MODE_TO_PARENT) {
                        this.getParent().requestDisallowInterceptTouchEvent(false);
                    } else {
                        if (pageCount > 1) {
                            this.setCurrentItem(pageCount - currentItem - 1, isBorderAnimation);
                        }
                        this.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    return super.dispatchTouchEvent(ev);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
    }

    private static class MyHandler extends Handler {

        private AutoScrollViewPager pager;

        public MyHandler(AutoScrollViewPager autoScrollViewPager) {
            this.pager = autoScrollViewPager;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SCROLL_WHAT:
                    if (pager != null) {
                        if (pager.isAutoScroll) {
                            pager.scrollerUtils.setScrollDurationFactor(pager.autoScrollFactor);
                            pager.scrollOnce();
                            pager.scrollerUtils.setScrollDurationFactor(pager.swipeScrollFactor);
                            pager.sendScrollMessage(pager.interval + pager.scrollerUtils.getDuration());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * get auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     *
     * @return the interval
     */
    public long getInterval() {
        return interval;
    }

    /**
     * set auto scroll time in milliseconds, default is {@link #DEFAULT_INTERVAL}
     *
     * @param interval the interval to set
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * get auto scroll direction
     *
     * @return {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
     */
    public int getDirection() {
        return (direction == LEFT) ? LEFT : RIGHT;
    }

    /**
     * set auto scroll direction
     *
     * @param direction {@link #LEFT} or {@link #RIGHT}, default is {@link #RIGHT}
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    /**
     * is cycle
     */
    public boolean isCycle() {
        return this.isCycle;
    }

    /**
     * set cycle
     */
    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    /**
     * whether stop auto scroll when touching, default is true
     *
     * @return the stopScrollWhenTouch
     */
    public boolean isStopScrollWhenTouch() {
        return stopScrollWhenTouch;
    }

    /**
     * set whether stop auto scroll when touching, default is true
     */
    public void setStopScrollWhenTouch(boolean stopScrollWhenTouch) {
        this.stopScrollWhenTouch = stopScrollWhenTouch;
    }

    /**
     * get slide border mode
     */
    public int getSlideBorderMode() {
        return this.slideBorderMode;
    }

    /**
     * set slide border mode
     */
    public void setSlideBorderMode(int slideBorderMode) {
        this.slideBorderMode = slideBorderMode;
    }

    /**
     * is border animation
     */
    public boolean isBorderAnimation() {
        return this.isBorderAnimation;
    }

    /**
     * set the border animation
     */
    public void setBorderAnimation(boolean isBorderAnimation) {
        this.isBorderAnimation = isBorderAnimation;
    }


}
