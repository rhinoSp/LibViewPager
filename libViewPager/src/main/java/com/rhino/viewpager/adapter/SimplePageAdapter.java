package com.rhino.viewpager.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.rhino.viewpager.factory.SimpleHolderFactory;
import com.rhino.viewpager.base.BaseHolder;
import com.rhino.viewpager.base.BaseHolderData;
import com.rhino.viewpager.base.BaseHolderFactory;
import com.rhino.viewpager.utils.RecyclingUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by LuoLin on 2017/06/06
 */
public class SimplePageAdapter extends RecyclingPagerAdapter {

    public List<BaseHolderData> dataList = new ArrayList<>();
    public BaseHolderFactory holderFactory;

    public SimplePageAdapter() {
        this(null, new RecyclingUtils(), SimpleHolderFactory.create());
    }

    public SimplePageAdapter(@Nullable ViewPager viewPager) {
        this(viewPager, new RecyclingUtils(), SimpleHolderFactory.create());
    }

    public SimplePageAdapter(@Nullable ViewPager viewPager, RecyclingUtils recyclingUtils) {
        super(viewPager, recyclingUtils);
    }

    public SimplePageAdapter(@Nullable ViewPager viewPager, RecyclingUtils recyclingUtils, BaseHolderFactory holderFactory) {
        super(viewPager, recyclingUtils);
        this.holderFactory = holderFactory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public View getView(int position, View view, ViewGroup container) {
        BaseHolder holder;
        BaseHolderData data = getData(position);
        if (view == null) {
            holder = holderFactory.buildHolder(container, data.getLayoutRes(), data.getHolderClassName());
            view = holder.itemView;
            view.setTag(data.getLayoutRes(), holder);
        } else {
            Object tag = view.getTag(data.getLayoutRes());
            if (null == tag) {
                holder = holderFactory.buildHolder(container, data.getLayoutRes(), data.getHolderClassName());
                view = holder.itemView;
                view.setTag(data.getLayoutRes(), holder);
            } else {
                holder = (BaseHolder) tag;
            }
        }
        holder.bindView(data, position);
        return holder.itemView;
    }

    @Override
    public int getCount() {
        return null != dataList ? dataList.size() : 0;
    }

    public int getRealCount() {
        return dataList.size();
    }

    public int getRealPosition(int position) {
        if (dataList != null) {
            return position % dataList.size();
        }
        return position;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    /**
     * Get the data.
     *
     * @param position the position
     * @return BaseHolderData
     */
    public BaseHolderData getData(int position) {
        return dataList.get(position);
    }

    /**
     * Get the data list.
     *
     * @return list
     */
    public List<? extends BaseHolderData> getDataList() {
        return dataList;
    }

    /**
     * Update the data.
     *
     * @param dataList list
     */
    public void updateData(@NonNull List<? extends BaseHolderData> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
    }

    /**
     * Update the data and Notify data changed.
     *
     * @param dataList list
     */
    public void updateDataAndNotify(@NonNull List<? extends BaseHolderData> dataList) {
        updateData(dataList);
        notifyDataSetChanged();
    }

}
