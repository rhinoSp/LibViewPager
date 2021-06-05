package com.rhino.viewpager.base;

import android.view.View;

import androidx.databinding.DataBindingUtil;

/**
 * @author LuoLin
 * @since Create on 2016/11/21.
 **/
public class BaseViewPagerHolder extends BaseHolder<BaseViewPagerHolderData> {

    public BaseViewPagerHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindView(BaseViewPagerHolderData data, int position) {
        data.bindView(DataBindingUtil.bind(itemView), position);
    }

}
