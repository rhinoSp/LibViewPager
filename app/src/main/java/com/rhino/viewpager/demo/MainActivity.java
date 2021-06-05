package com.rhino.viewpager.demo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.rhino.viewpager.adapter.LoopPageAdapter;
import com.rhino.viewpager.adapter.SimplePageAdapter;
import com.rhino.viewpager.demo.databinding.ActivityMainBinding;
import com.rhino.viewpager.transformers.AccordionTransformer;
import com.rhino.viewpager.transformers.BackgroundToForegroundTransformer;
import com.rhino.viewpager.transformers.BookPageTransformer;
import com.rhino.viewpager.transformers.BookPageWidget;
import com.rhino.viewpager.transformers.CubeInTransformer;
import com.rhino.viewpager.transformers.CubeOutTransformer;
import com.rhino.viewpager.transformers.DefaultTransformer;
import com.rhino.viewpager.transformers.DepthPageTransformer;
import com.rhino.viewpager.transformers.FlipHorizontalTransformer;
import com.rhino.viewpager.transformers.FlipVerticalTransformer;
import com.rhino.viewpager.transformers.ForegroundToBackgroundTransformer;
import com.rhino.viewpager.transformers.RotateDownTransformer;
import com.rhino.viewpager.transformers.RotateUpTransformer;
import com.rhino.viewpager.transformers.StackTransformer;
import com.rhino.viewpager.transformers.TabletTransformer;
import com.rhino.viewpager.transformers.VerticalPageTransformer;
import com.rhino.viewpager.transformers.ZoomInTransformer;
import com.rhino.viewpager.transformers.ZoomOutSlideTransformer;
import com.rhino.viewpager.transformers.ZoomOutTransformer;
import com.rhino.viewpager.view.CircleIndicator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding dataBinding;
    private List<ViewPagerItemBanner> itemList1 = new ArrayList<>();
    private SimplePageAdapter adapter1;

    private Button selectButton;
    private boolean isNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        new LoopPageAdapter(null, null);
        dataBinding.viewPager1.setOffscreenPageLimit(5);
        adapter1 = new SimplePageAdapter(dataBinding.viewPager1);

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

        dataBinding.circleIndicator.setCount(itemList1.size());
        dataBinding.circleIndicator.setMode(CircleIndicator.Mode.INSIDE);
        dataBinding.viewPager1.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                dataBinding.circleIndicator.setPosition(adapter1.getRealPosition(i), v);
            }

            @Override
            public void onPageSelected(int i) {
                dataBinding.circleIndicator.setPosition(adapter1.getRealPosition(i));
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
                isNext = false;
                int currentItem = dataBinding.viewPager1.getCurrentItem();
                Log.d("xxx", "prePage, currentItem = " + currentItem);
                if (currentItem == 0) {
                    dataBinding.bookPageWidget.loadCurPageView(itemList1.get(currentItem).getRootView());
                    return false;
                } else if (currentItem > 0) {
                    dataBinding.bookPageWidget.loadNextPageView(itemList1.get(currentItem - 1).getRootView());
                    dataBinding.bookPageWidget.loadCurPageView(itemList1.get(currentItem).getRootView());
                    dataBinding.viewPager1.setCurrentItem(currentItem - 1);
                }
                return currentItem > 0;
            }

            @Override
            public Boolean nextPage() {
                isNext = true;
                int currentItem = dataBinding.viewPager1.getCurrentItem();
                Log.d("xxx", "nextPage, currentItem = " + currentItem);
                if (currentItem == itemList1.size() - 1) {
                    dataBinding.bookPageWidget.loadCurPageView(itemList1.get(currentItem).getRootView());
                    return false;
                } else if (currentItem < itemList1.size() - 1) {
                    dataBinding.bookPageWidget.loadNextPageView(itemList1.get(currentItem + 1).getRootView());
                    dataBinding.bookPageWidget.loadCurPageView(itemList1.get(currentItem).getRootView());
                    dataBinding.viewPager1.setCurrentItem(currentItem + 1);
                }
                return currentItem < itemList1.size() - 1;
            }

            @Override
            public void cancel() {
                int currentItem = dataBinding.viewPager1.getCurrentItem();
                Log.d("xxx", "cancel, currentItem = " + currentItem);
                if (isNext) {
                    dataBinding.viewPager1.setCurrentItem(currentItem - 1);
                } else {
                    dataBinding.viewPager1.setCurrentItem(currentItem + 1);
                }
            }
        });
    }

    /**
     * 滑动动画
     */
    private void addPageTransformerChangeListener() {
        select(dataBinding.StackTransformer, new StackTransformer());
        dataBinding.StackTransformer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                select((Button) v, new StackTransformer());
            }
        });
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
        selectButton = button;
        selectButton.setSelected(true);
        if (button == dataBinding.SimulationTransformer) {
//            dataBinding.viewPager1.setScrollable(false);
            int currentItem = dataBinding.viewPager1.getCurrentItem();
            Log.d("xxx", "change, currentItem = " + currentItem);
            dataBinding.bookPageWidget.loadNextPageView(itemList1.get(currentItem).getRootView());
            dataBinding.bookPageWidget.setVisibility(View.VISIBLE);
            dataBinding.viewPager1.setPageTransformer(true, new BookPageTransformer());
        } else {
//            dataBinding.viewPager1.setScrollable(true);
            int currentItem = dataBinding.viewPager1.getCurrentItem();
            Log.d("xxx", "change, currentItem = " + currentItem);

            if (dataBinding.viewPager1.getPageTransformer() instanceof BookPageTransformer) {
                if (currentItem == 0 || currentItem < itemList1.size() - 1) {
                    dataBinding.viewPager1.setCurrentItem(currentItem + 1, false);
                    dataBinding.viewPager1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dataBinding.viewPager1.setCurrentItem(currentItem, false);
                            dataBinding.bookPageWidget.setVisibility(View.INVISIBLE);
                        }
                    }, 50);
                } else {
                    dataBinding.viewPager1.setCurrentItem(currentItem - 1, false);
                    dataBinding.viewPager1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dataBinding.viewPager1.setCurrentItem(currentItem, false);
                            dataBinding.bookPageWidget.setVisibility(View.INVISIBLE);
                        }
                    }, 50);
                }
            }
            dataBinding.viewPager1.setPageTransformer(true, transformer);
        }
    }

}
