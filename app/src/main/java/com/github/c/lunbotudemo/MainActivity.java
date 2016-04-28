package com.github.c.lunbotudemo;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";

    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;
    private ImageView[] mIndicators;
    private RunnableTask runnableTask = new RunnableTask();
    /**
     * 轮播图的正式位置
     */
    private int mCurrentPosition = 0;
    /**
     * 真正的轮播图的数量
     */
    private final int DEFAULT_BANNER_SIZE = 5;
    /**
     * 欺骗ViewPager有2倍的真实数据数量（这里必须是真实数量的2倍及其以上，其实取2倍就最佳）
     */
    private final int FAKE_BANNER_SIZE = DEFAULT_BANNER_SIZE * 2;

    /**
     * 要显示的轮播图图片资源
     */
    private int[] mImagesSrc = {
            R.mipmap.img1,
            R.mipmap.img2,
            R.mipmap.img3,
            R.mipmap.img4,
            R.mipmap.img5
    };

//    ===========================================================================================================

    /**
     * 让轮播图viewpager滚动起来
     */
    public void startRoll() {
        //1.发送一个3秒的延时任务
        handler.postDelayed(runnableTask, 5000);
    }

    class RunnableTask implements Runnable {
        @Override
        public void run() {
            // 2.变化轮播图当前要显示的页面位置，递增1，为了不使这个数字递增超过轮播图图片欺骗数据的个数，取余数
            mCurrentPosition = (mCurrentPosition + 1) % FAKE_BANNER_SIZE;
            // 3.发送消息给主线程的handler去改变UI
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {
        // 4.接收并处理run方法发来的消息
        public void handleMessage(android.os.Message msg) {
            // 5.viewpager设置新的当前页
            if (mCurrentPosition == FAKE_BANNER_SIZE - 1) {
//                如果当前页为欺骗页的最后一页，将其置换真实的最后一页，使得能够顺利继续向后滑动
                viewPager.setCurrentItem(DEFAULT_BANNER_SIZE - 1, false);
            } else {
//                viewPager设置显示将当前页
                viewPager.setCurrentItem(mCurrentPosition);
            }

            // 6.继续执行startRoll方法，成为一个循环
            startRoll();
        }
    };

    //    ===========================================================================================================


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        写在这里是因为，当页面再次显示时会每次调用此方法，让轮播图滚起来（不能写在onCreate里，因为onCreate只是执行一次）
        startRoll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 移除所有的任务，即：view失去焦点时，停止轮播图的滚动
        handler.removeCallbacksAndMessages(null);
    }

    private void initView() {
//        指示的小白点
        mIndicators = new ImageView[]{
                (ImageView) findViewById(R.id.indicator1),
                (ImageView) findViewById(R.id.indicator2),
                (ImageView) findViewById(R.id.indicator3),
                (ImageView) findViewById(R.id.indicator4),
                (ImageView) findViewById(R.id.indicator5)
        };
        viewPager = (ViewPager) findViewById(R.id.banner);
        myPagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setOnPageChangeListener(myPagerAdapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN
                        || action == MotionEvent.ACTION_MOVE) {
                    // 移除所有的任务，即：手指在操作viewpager时，停止轮播图的滚动
                    handler.removeCallbacksAndMessages(null);

                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    //手指离开屏幕时，回复自动播放的
                    startRoll();
                }
                return false;//这里返回false，还会执行viewpager自带的滑动事件，否则返回true，不会执行执行viewpager自带的滑动事件
            }
        });
    }

    private void setIndicator(int position) {
        position %= DEFAULT_BANNER_SIZE;
        for (ImageView indicator : mIndicators) {
            indicator.setImageResource(R.mipmap.indicator_unchecked);
        }
        mIndicators[position].setImageResource(R.mipmap.indicator_checked);
    }


    private class MyPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private LayoutInflater mInflater;

        public MyPagerAdapter(Context context) {
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return FAKE_BANNER_SIZE;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            这里的position指欺骗数据里的position，不能直接用来设置真实的图片数据（会角标越界的），要除以真实的数据长度，取余数
            position %= DEFAULT_BANNER_SIZE;
            View view = mInflater.inflate(R.layout.item, container, false);
            ImageView imageView = (ImageView) view.findViewById(R.id.image);
            imageView.setImageResource(mImagesSrc[position]);
            final int pos = position;
/*            view.setClickable(false);//这里做了一个测试，那么当点击viewpager时，
              viewpager会让点击事件传递给子view执行，如果设置view为可点击的，子view就会消费这个点击事件，从而不运行前面viewpager定义的onTouch事件
            如果设为不可点击，viewpager自己会再次处理这个触摸事件，导致运行前面viewpager定义的onTouch事件（即一直点击图片，轮播图不会自动滚动）*/
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "click banner item :" + pos, Toast.LENGTH_SHORT).show();
                }
            });
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            int position = viewPager.getCurrentItem();
            Log.d(TAG, "finish update before, position=" + position);
            if (position == 0) {
//              如果滑动到了第0页，为了能让图片顺利往左边继续滑动，从而划出最后一页（真实数据），
//              需要把第0页图片，偷换为第DEFAULT_BANNER_SIZE页，因为对DEFAULT_BANNER_SIZE取余数的缘故，
//              第DEFAULT_BANNER_SIZE页和第0页的页面是同一个数据，还要注意一定要使用瞬间滑到第DEFAULT_BANNER_SIZE页
//               而不要smooth平滑地滑动到第DEFAULT_BANNER_SIZE页，即false： viewPager.setCurrentItem(position, false);
                position = DEFAULT_BANNER_SIZE;
                viewPager.setCurrentItem(position, false);
            } else if (position == FAKE_BANNER_SIZE - 1) {
//                同理，如果要滑动到欺骗数据最后一页，也要做类似的处理
                position = DEFAULT_BANNER_SIZE - 1;
                viewPager.setCurrentItem(position, false);
            }
            Log.d(TAG, "finish update after, position=" + position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentPosition = position;
            setIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

}
