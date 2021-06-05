package com.rhino.viewpager.demo;

import android.view.View;

import androidx.annotation.Nullable;

import com.rhino.viewpager.base.BaseViewPagerHolderData;
import com.rhino.viewpager.demo.databinding.VpagerItemBannerBinding;


public class ViewPagerItemBanner extends BaseViewPagerHolderData<VpagerItemBannerBinding> {

    private VpagerItemBannerBinding dataBinding;
    private String title = "1";
    private String content = "内容内容";

    public ViewPagerItemBanner(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.vpager_item_banner;
    }

    @Override
    public void bindView(VpagerItemBannerBinding dataBinding, int position) {
        this.dataBinding = dataBinding;
        dataBinding.tvTitle.setText(title);
        dataBinding.tvContent.setText(content);
    }

    @Nullable
    public View getRootView() {
        if (dataBinding != null) {
            return dataBinding.getRoot();
        }
        return null;
    }

}
