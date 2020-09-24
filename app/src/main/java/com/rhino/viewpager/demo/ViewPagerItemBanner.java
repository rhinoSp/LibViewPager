package com.rhino.viewpager.demo;

import com.rhino.viewpager.base.BaseViewPagerHolderData;
import com.rhino.viewpager.demo.databinding.VpagerItemBannerBinding;


public class ViewPagerItemBanner extends BaseViewPagerHolderData<VpagerItemBannerBinding> {

    private String content = "内容内容";

    public ViewPagerItemBanner() {
    }

    @Override
    public int getLayoutRes() {
        return R.layout.vpager_item_banner;
    }

    @Override
    public void bindView(VpagerItemBannerBinding dataBinding, int position) {
        dataBinding.tvContent.setText(content);
    }

}
