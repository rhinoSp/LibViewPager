package com.rhino.viewpager.demo;

import com.rhino.viewpager.base.BaseViewPagerHolderData;
import com.rhino.viewpager.demo.databinding.VpagerItemBannerBinding;


public class ViewPagerItemBanner extends BaseViewPagerHolderData<VpagerItemBannerBinding> {

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
        dataBinding.tvTitle.setText(title);
        dataBinding.tvContent.setText(content);
    }

}
