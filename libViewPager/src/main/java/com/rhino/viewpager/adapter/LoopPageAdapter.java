package com.rhino.viewpager.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.rhino.viewpager.base.BaseHolderData;
import com.rhino.viewpager.base.BaseHolderFactory;
import com.rhino.viewpager.utils.RecyclingUtils;

/**
 * @author LuoLin
 * @since Create on 2019/11/7.
 **/
public class LoopPageAdapter extends SimplePageAdapter {

    public LoopPageAdapter() {
    }

    public LoopPageAdapter(@Nullable ViewPager viewPager) {
        super(viewPager);
    }

    public LoopPageAdapter(@Nullable ViewPager viewPager, RecyclingUtils recyclingUtils) {
        super(viewPager, recyclingUtils);
    }

    public LoopPageAdapter(@Nullable ViewPager viewPager, RecyclingUtils recyclingUtils, BaseHolderFactory holderFactory) {
        super(viewPager, recyclingUtils, holderFactory);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    public int getRealCount() {
        return dataList.size();
    }

    public int getRealPosition(int position) {
        if (dataList != null) {
            return position % dataList.size();
        }
        return 0;
    }

    @Override
    public BaseHolderData getData(int position) {
        return dataList.get(getRealPosition(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_UNCHANGED;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (viewPager != null && viewPager.getCurrentItem() <= 0) {
            resetStartPosition();
        }
    }

    @Override
    public void setViewPager(@Nullable ViewPager viewPager) {
        super.setViewPager(viewPager);
        if (viewPager != null) {
            viewPager.addOnAdapterChangeListener(new ViewPager.OnAdapterChangeListener() {
                @Override
                public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter pagerAdapter, @Nullable PagerAdapter pagerAdapter1) {
                    resetStartPosition();
                }
            });
        }
    }

    public void resetStartPosition() {
        if (viewPager == null) {
            throw new RuntimeException("ViewPager is null, init by setViewPager(ViewPager).");
        }
        int realCount = getRealCount();
        if (realCount <= 0) {
            return;
        }
        int m = (Integer.MAX_VALUE / 2) % getRealCount();
        int currentPosition = Integer.MAX_VALUE / 2 - m;
        viewPager.setCurrentItem(currentPosition);
    }

}
