package com.rhino.viewpager.demo;

import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.rhino.viewpager.adapter.SimplePageAdapter;
import com.rhino.viewpager.view.AutoScrollViewPager;
import com.rhino.viewpager.adapter.LoopPageAdapter;
import com.rhino.viewpager.base.BaseHolderData;
import com.rhino.viewpager.transformers.*;
import com.rhino.viewpager.view.CircleIndicator;
import com.rhino.viewpager.demo.databinding.ActivityMainBinding;
import com.rhino.viewpager.view.ScrollAbleViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding dataBinding;
    public List<ViewPagerItemBanner> itemList1 = new ArrayList<>();
    private ScrollAbleViewPager viewPager1;
    private SimplePageAdapter adapter1;

    private Button selectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        viewPager1 = findViewById(R.id.view_pager1);
//        viewPager1.setDirection(AutoScrollViewPager.LEFT);
//        viewPager1.setInterval(1000);
//        viewPager1.setAutoScrollDurationFactor(10);
        viewPager1.setOffscreenPageLimit(5);

        adapter1 = new SimplePageAdapter(viewPager1);

        itemList1.add(new ViewPagerItemBanner("1/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("2/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("3/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("4/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("5/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("6/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("7/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("8/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("9/10", "内容内容"));
        itemList1.add(new ViewPagerItemBanner("10/10", "内容内容"));

        adapter1.updateDataAndNotify(itemList1);
//        viewPager1.startAutoScroll();

        dataBinding.circleIndicator.setCount(itemList1.size());
        dataBinding.circleIndicator.setMode(CircleIndicator.Mode.INSIDE);
        viewPager1.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                dataBinding.circleIndicator.setPosition(adapter1.getRealPosition(i), v);
            }

            @Override
            public void onPageSelected(int i) {
                dataBinding.circleIndicator.setPosition(adapter1.getRealPosition(i));
                int currentItem = dataBinding.viewPager1.getCurrentItem();
                if (currentItem < 0 || currentItem >= itemList1.size() - 1) {
                    return;
                }
                if (dataBinding.bookPageWidget.getVisibility() == View.VISIBLE) {
                    loadBitmapFromView(dataBinding.bookPageWidget.getCurPage(), itemList1.get(currentItem).getRootView());
                    loadBitmapFromView(dataBinding.bookPageWidget.getNextPage(), itemList1.get(currentItem + 1).getRootView());
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        addPageTransformerChangeListener();


        dataBinding.bookPageWidget.setTouchListener(new BookPageWidget.TouchListener() {
            @Override
            public void center() {

            }

            @Override
            public Boolean prePage() {
                Log.d("xxx", "prePage");
                int currentItem = dataBinding.viewPager1.getCurrentItem();
                if (currentItem <= 0 || currentItem > itemList1.size()) {
                    return false;
                }
                loadBitmapFromView(dataBinding.bookPageWidget.getCurPage(), itemList1.get(currentItem).getRootView());
                loadBitmapFromView(dataBinding.bookPageWidget.getNextPage(), itemList1.get(currentItem - 1).getRootView());
                dataBinding.viewPager1.setCurrentItem(currentItem - 1, false);
                pre = true;
                return true;
            }

            @Override
            public Boolean nextPage() {
                Log.d("xxx", "nextPage");
                int currentItem = dataBinding.viewPager1.getCurrentItem();
                if (currentItem < 0 || currentItem >= itemList1.size() - 1) {
                    return false;
                }
                loadBitmapFromView(dataBinding.bookPageWidget.getCurPage(), itemList1.get(currentItem).getRootView());
                loadBitmapFromView(dataBinding.bookPageWidget.getNextPage(), itemList1.get(currentItem + 1).getRootView());
                dataBinding.viewPager1.setCurrentItem(currentItem + 1, false);

                pre = false;
                return true;
            }

            @Override
            public void cancel() {
                Log.d("xxx", "cancel");
                dataBinding.bookPageWidget.changePage();
                int currentItem = dataBinding.viewPager1.getCurrentItem();
                dataBinding.viewPager1.setCurrentItem(pre ? currentItem + 1 : currentItem - 1, false);
            }
        });
    }

    private boolean pre;

    private void loadBitmapFromView(Bitmap bitmap, View view) {
        if (bitmap == null || view == null) {
            Log.d("xxx", "bitmap = " + bitmap + ", view = " + view);
            return;
        }
        int w = view.getWidth();
        int h = view.getHeight();
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, w, h);
        view.draw(canvas);
    }

    /**
     * 滑动动画
     */
    private void addPageTransformerChangeListener() {
//        select(dataBinding.DefaultTransformer, new DefaultTransformer());
        dataBinding.SimulationTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, null);
            }
        });
        dataBinding.DefaultTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new DefaultTransformer());
            }
        });
        dataBinding.StackTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new StackTransformer());
            }
        });
        dataBinding.VerticalPageTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new VerticalPageTransformer());
            }
        });
        dataBinding.AccordionTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new AccordionTransformer());
            }
        });
        dataBinding.BackgroundToForegroundTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new BackgroundToForegroundTransformer());
            }
        });
        dataBinding.CubeInTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new CubeInTransformer());
            }
        });
        dataBinding.CubeOutTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new CubeOutTransformer());
            }
        });
        dataBinding.DepthPageTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new DepthPageTransformer());
            }
        });
        dataBinding.FlipHorizontalTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new FlipHorizontalTransformer());
            }
        });
        dataBinding.FlipVerticalTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new FlipVerticalTransformer());
            }
        });
        dataBinding.ForegroundToBackgroundTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new ForegroundToBackgroundTransformer());
            }
        });
        dataBinding.RotateDownTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new RotateDownTransformer());
            }
        });
        dataBinding.RotateUpTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new RotateUpTransformer());
            }
        });
        dataBinding.TabletTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new TabletTransformer());
            }
        });
        dataBinding.ZoomInTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new ZoomInTransformer());
            }
        });
        dataBinding.ZoomOutSlideTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new ZoomOutSlideTransformer());
            }
        });
        dataBinding.ZoomOutTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new ZoomOutTransformer());
            }
        });
    }

    private void select(Button button, @Nullable ViewPager.PageTransformer transformer) {
        if (selectButton != null) {
            selectButton.setSelected(false);
        }
        if (button == dataBinding.SimulationTransformer) {
            dataBinding.bookPageWidget.setVisibility(View.VISIBLE);
            int currentItem = dataBinding.viewPager1.getCurrentItem();
            if (currentItem < 0 || currentItem >= itemList1.size() - 1) {
                return;
            }
            if (dataBinding.bookPageWidget.getVisibility() == View.VISIBLE) {
                loadBitmapFromView(dataBinding.bookPageWidget.getCurPage(), itemList1.get(currentItem).getRootView());
                loadBitmapFromView(dataBinding.bookPageWidget.getNextPage(), itemList1.get(currentItem + 1).getRootView());
            }
        } else {
            dataBinding.bookPageWidget.setVisibility(View.INVISIBLE);
        }
        selectButton = button;
        selectButton.setSelected(true);
        viewPager1.setPageTransformer(true, transformer);
    }

}
