package com.github.c.lunbotudemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 轮播图：
 *
 * 实现的功能：
 * 1.自动、无限播放的轮播图,对应每个轮播图页面的点击事件（传递position）
 * 2.手指在操作轮播图时（以及该页面不再显示时），轮播图自动播放功能会停止
 * 3.手指停止操作轮播图时（以及该页面再次显示时），轮播图自动播放启动
 *
 * 用法注意事项：
 * 1.外部直接new即可,注意不是new LunBoTu而是new LunBoTu.Builder（具体用法参照demo）
 * 2.这个自定义View，不能在布局文件中直接写
 * 3.只能在代码里new，在布局文件里写一个随便的viewgroup（例如：LinearLayout）占位置即可，该viewgroup的高宽，即是轮播图的高宽
 * 4.记得最后要调用intoParent方法，把new出来的轮播图显示在其父布局中
 * 5.记得绑定activity的生命周期方法onStart-->startRoll(否则不会自动滚动),onPause-->stopRoll（否则离开此页面时，一直在滚动）
 * 6.一般情况直接new即可，默认的排版能够满足一般需求，也可以通过buidle的各种方法来调整轮播图各个组件的位置和外观
 * 7.所有的尺寸单位都是按照dp计算的，文字为sp
 * 8.具体用法参照demo
 * 9.内部加载网络图片使用了Picasso，记得引入，别忘了添加访问网络的权限
 * Created by a on 2016/4/28.
 */
public class LunBoTu extends LinearLayout {

    public OnLunBoTuClickListenter mOnLunBoTuClickListenter;

    public void setOnLunBoTuClickListenter(OnLunBoTuClickListenter mOnLunBoTuClickListenter) {
        this.mOnLunBoTuClickListenter = mOnLunBoTuClickListenter;
    }

    private Params p = new Params();
    private static final String TAG = "LunBoTu";
    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;
    private ArrayList<ImageView> mIndicatorList = new ArrayList<>();
    private RunnableTask runnableTask = new RunnableTask();
    /**
     * 轮播图的真实的位置
     */
    private int mCurrentPosition = 0;
    /**
     * 真正的轮播图的数量
     */
    private int TRUE_IMAGE_COUNT;
    /**
     * 欺骗ViewPager有2倍的真实数据数量（这里必须是真实数量的2倍及其以上，其实取2倍就最佳）
     */
    private int FAKE_IMAGE_COUNT;

    /**
     * 要显示的轮播图图片资源
     */
    private ArrayList<String> mImagesUrls = new ArrayList<>();
    /**
     * 要显示的轮播图图片资源的文字介绍
     */
    private ArrayList<String> mIntroductions = new ArrayList<>();

    private LunBoTu(Context context, ArrayList<String> mImagesUrls, ArrayList<String> mIntroductions, Params p) {
        super(context);
        this.p.indicatorBottomMargin = p.indicatorBottomMargin;
        this.p.indicatorLeftMargin = p.indicatorLeftMargin;
        this.p.indicatorTopMargin = p.indicatorTopMargin;
        this.p.indicatorRightMargin = p.indicatorRightMargin;
        this.p.indicatorUncheckedImgResource = p.indicatorUncheckedImgResource;
        this.p.indicatorCheckedImgResource = p.indicatorCheckedImgResource;
        this.p.eachIndicatorLeftMargin = p.eachIndicatorLeftMargin;
        this.p.eachIndicatorRightMargin = p.eachIndicatorRightMargin;
        this.p.eachIndicatorWidth = p.eachIndicatorWidth;
        this.p.eachIndicatorHeight = p.eachIndicatorHeight;
        this.p.indicatorGravity = p.indicatorGravity;
        this.p.imgScaleType = p.imgScaleType;
        this.p.introductionGravity = p.introductionGravity;
        this.p.introductionTextAppearance = p.introductionTextAppearance;
        this.p.introductionTextColor = p.introductionTextColor;
        this.p.introductionLeftMargin = p.introductionLeftMargin;
        this.p.introductionTopMargin = p.introductionTopMargin;
        this.p.introductionRightMargin = p.introductionRightMargin;
        this.p.introductionBottomMargin = p.introductionBottomMargin;
        this.p.introductTextSize = p.introductTextSize;
        this.p.autoPlaytimeInterval = p.autoPlaytimeInterval;

        this.mImagesUrls = mImagesUrls;
        this.mIntroductions = mIntroductions;
        TRUE_IMAGE_COUNT = mImagesUrls.size();
        FAKE_IMAGE_COUNT = TRUE_IMAGE_COUNT * 2;
        init(context);
    }

    private void init(Context context) {
        initView(context);
    }


    private void initView(Context context) {
//      开始用代码写布局item，首先新建一个FrameLayout
        FrameLayout frameLayout = new FrameLayout(context);
        FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams
                (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        frameLayout.setLayoutParams(frameLayoutParams);
        frameLayout.setBackgroundColor(Color.RED);
        addView(frameLayout);
//      添加viewpager
        viewPager = new ViewPager(context);
        FrameLayout.LayoutParams viewPagerLayoutParams = new FrameLayout.LayoutParams
                (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(viewPagerLayoutParams);
        frameLayout.addView(viewPager);

//      指示的小白点所在的LinearLayout
        LinearLayout ll_indicator = new LinearLayout(context);
        FrameLayout.LayoutParams indicatorLayoutParams =
                new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        indicatorLayoutParams.gravity = p.indicatorGravity;//ll_indicator在framelayout中的位置★
        indicatorLayoutParams.setMargins(
                dip2px(context, p.indicatorLeftMargin),
                dip2px(context, p.indicatorTopMargin),
                dip2px(context, p.indicatorRightMargin),
                dip2px(context, p.indicatorBottomMargin)
        );
        ll_indicator.setLayoutParams(indicatorLayoutParams);
        frameLayout.addView(ll_indicator);

//      在LinearLayout内部根据图片显示的数量，添加相应数量的小白点
        for (int i = 0; i < mImagesUrls.size(); i++) {

            ImageView indicator = new ImageView(context);
            indicator.setBackgroundResource(p.indicatorUncheckedImgResource);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    dip2px(context, p.eachIndicatorWidth),
                    dip2px(context, p.eachIndicatorHeight));
            layoutParams.setMargins(
                    dip2px(context, p.eachIndicatorLeftMargin),
                    0,
                    dip2px(context, p.eachIndicatorRightMargin),
                    0);
            indicator.setLayoutParams(layoutParams);
            ll_indicator.addView(indicator);
            mIndicatorList.add(indicator);

        }


        myPagerAdapter = new MyPagerAdapter(context, mImagesUrls, mIntroductions);
        viewPager.setAdapter(myPagerAdapter);
        viewPager.setOnPageChangeListener(myPagerAdapter);
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN
                        || action == MotionEvent.ACTION_MOVE) {
                    // 移除所有的任务，即：手指在操作viewpager时，停止轮播图的滚动
                    stopScroll();

                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    //手指离开屏幕时，回复自动播放的
                    startRoll();
                }
                return false;//这里返回false，还会执行viewpager自带的滑动事件，否则返回true，不会执行执行viewpager自带的滑动事件
            }
        });
    }

    public void stopScroll() {
        handler.removeCallbacksAndMessages(null);
    }

    private void setIndicator(int position) {
        position %= TRUE_IMAGE_COUNT;
        for (ImageView indicator : mIndicatorList) {
            indicator.setBackgroundResource(p.indicatorUncheckedImgResource);
        }
        mIndicatorList.get(position).setBackgroundResource(p.indicatorCheckedImgResource);
    }

//    =======================================自动播放代码=====================================================

    /**
     * 让轮播图viewpager滚动起来
     */
    public void startRoll() {
        //1.发送一个3秒的延时任务
        handler.postDelayed(runnableTask, p.autoPlaytimeInterval);
    }

    class RunnableTask implements Runnable {
        @Override
        public void run() {
            // 2.变化轮播图当前要显示的页面位置，递增1，为了不使这个数字递增超过轮播图图片欺骗数据的个数，取余数
            mCurrentPosition = (mCurrentPosition + 1) % FAKE_IMAGE_COUNT;
            // 3.发送消息给主线程的handler去改变UI
            handler.obtainMessage().sendToTarget();
        }
    }

    private Handler handler = new Handler() {
        // 4.接收并处理run方法发来的消息
        public void handleMessage(android.os.Message msg) {
            // 5.viewpager设置新的当前页
            if (mCurrentPosition == FAKE_IMAGE_COUNT - 1) {
//                如果当前页为欺骗页的最后一页，将其置换真实的最后一页，使得能够顺利继续向后滑动
                viewPager.setCurrentItem(TRUE_IMAGE_COUNT - 1, false);
            } else {
//                viewPager设置显示将当前页
                viewPager.setCurrentItem(mCurrentPosition);
            }

            // 6.继续执行startRoll方法，成为一个循环
            startRoll();
        }
    };
//    ====================================自动播放代码===========================================================


    private class MyPagerAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

        private Picasso picasso;
        private ArrayList<String> mImagesUrls;
        private ArrayList<String> mIntroductions;
        private Context context;

        public MyPagerAdapter(Context context, ArrayList<String> mImagesUrls, ArrayList<String> mIntroductions) {
            picasso = Picasso.with(context);
            this.mImagesUrls = mImagesUrls;
            this.mIntroductions = mIntroductions;
            this.context = context;
        }

        @Override
        public int getCount() {
            return FAKE_IMAGE_COUNT;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            这里的position指欺骗数据里的position，不能直接用来设置真实的图片数据（会角标越界的），要除以真实的数据长度，取余数
            position %= TRUE_IMAGE_COUNT;
//            View view = mInflater.inflate(R.layout.item, container, false);
//          开始用代码写布局item，首先新建一个FrameLayout
            FrameLayout frameLayout = new FrameLayout(context);
//            这里framelayout的宽高就是轮播图所显示的宽高，
//            这里设置的宽高都是match_parent，所以你只需指定该轮播图所在的父布局宽高就可以了
            FrameLayout.LayoutParams frameLayoutParams = new FrameLayout.LayoutParams
                    (FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            frameLayout.setLayoutParams(frameLayoutParams);
            frameLayout.setBackgroundColor(Color.parseColor("#b0b0b0"));

//            添加轮播的图片imageView
            ImageView imageView = new ImageView(context);
            FrameLayout.LayoutParams imageViewLayoutParams = new FrameLayout.LayoutParams
                    (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(imageViewLayoutParams);

            imageView.setScaleType(p.imgScaleType);
            frameLayout.addView(imageView);

//            添加图片说明textView
            TextView introduction = new TextView(context);
            FrameLayout.LayoutParams introductionLayoutParams = new FrameLayout.LayoutParams
                    (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            introductionLayoutParams.gravity = p.introductionGravity;
            introductionLayoutParams.setMargins(
                    dip2px(context, p.introductionLeftMargin),
                    dip2px(context, p.introductionTopMargin),
                    dip2px(context, p.introductionRightMargin),
                    dip2px(context, p.introductionBottomMargin)
            );
            introduction.setLayoutParams(introductionLayoutParams);
            introduction.setTextAppearance(context, p.introductionTextAppearance);
            introduction.setTextColor(Color.parseColor(p.introductionTextColor));
            introduction.setTextSize(TypedValue.COMPLEX_UNIT_SP, p.introductTextSize);
            frameLayout.addView(introduction);

            picasso.load(mImagesUrls.get(position)).into(imageView);


            if (mIntroductions != null) {
                introduction.setText(mIntroductions.get(position));
            }

            final int pos = position;
            frameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnLunBoTuClickListenter != null) {
                        mOnLunBoTuClickListenter.OnLunBoTuClick(pos);
                    }
                }
            });
            container.addView(frameLayout);
            return frameLayout;
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
                position = TRUE_IMAGE_COUNT;
                viewPager.setCurrentItem(position, false);
            } else if (position == FAKE_IMAGE_COUNT - 1) {
//                同理，如果要滑动到欺骗数据最后一页，也要做类似的处理
                position = TRUE_IMAGE_COUNT - 1;
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

    /**
     * 将这个轮播图添加到父类容器中显示
     *
     * @param parent
     * @return
     */
    public LunBoTu showIn(ViewGroup parent) {
        parent.addView(this);
        return this;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public interface OnLunBoTuClickListenter {
        void OnLunBoTuClick(int position);
    }

    /**
     * 参数类，用来存放轮播图的所有对外部暴露的可以改变的属性
     */
    public static class Params {
        /**
         * 轮播图小白点指示器的位置（在底部还是顶部，左侧、右侧还是中间）
         */
        int indicatorGravity = Gravity.BOTTOM | Gravity.RIGHT;
        /**
         * 轮播图小白点指示器的LeftMargin值
         */
        int indicatorLeftMargin = 0;
        /**
         * 轮播图小白点指示器的TopMargin值
         */
        int indicatorTopMargin = 0;
        /**
         * 轮播图小白点指示器的RightMargin值
         */
        int indicatorRightMargin = 5;
        /**
         * 轮播图小白点指示器的BottomMargin值
         */
        int indicatorBottomMargin = 5;


        /**
         * 轮播图的未选中指示点的图片资源
         */
        int indicatorUncheckedImgResource = R.mipmap.indicator_unchecked;
        /**
         * 轮播图的已选中指示点的图片资源
         */
        int indicatorCheckedImgResource = R.mipmap.indicator_checked;

        /**
         * 轮播图每个小白点指示器的LeftMargin值
         */
        int eachIndicatorLeftMargin = 0;

        /**
         * 轮播图每个小白点指示器的RightMargin值
         */
        int eachIndicatorRightMargin = 0;

        /**
         * 轮播图每个小白点宽度
         */
        int eachIndicatorWidth = 12;
        /**
         * 轮播图每个小白点高度
         */
        int eachIndicatorHeight = 12;
        /**
         * 轮播图显示的图片的缩放模式
         */
        ImageView.ScaleType imgScaleType = ImageView.ScaleType.CENTER_CROP;

        /**
         * 文本介绍在轮播图中的位置
         */
        int introductionGravity = Gravity.BOTTOM | Gravity.LEFT;

        /**
         * 文本介绍文字的Appearance
         */
        int introductionTextAppearance = android.R.style.TextAppearance_DeviceDefault_Medium;

        /**
         * 文本介绍的字体颜色
         */
        String introductionTextColor = "#ffffffff";


        /**
         * 文本介绍的LeftMargin值
         */
        int introductionLeftMargin = 5;
        /**
         * 文本介绍的TopMargin值
         */
        int introductionTopMargin = 0;
        /**
         * 文本介绍的RightMargin值
         */
        int introductionRightMargin = 5;
        /**
         * 文本介绍的BottomMargin值
         */
        int introductionBottomMargin = 1;

        /**
         * 文本介绍的字体大小，单位已经转换为sp
         */
        float introductTextSize = 17;

        /**
         * 轮播图自动播放的时间间隔（毫秒值）
         */
        int autoPlaytimeInterval = 3000;
    }

    public static class Builder {
        private Context context;
        private ArrayList<String> mImagesUrls;
        private ArrayList<String> mIntroductions;
        private LunBoTu.Params p = new LunBoTu.Params();
        private LunBoTu lunBoTu;

        /**
         * 构造函数，文字介绍可传入null
         * @param context
         * @param mImagesUrls
         * @param mIntroductions 当没有文字介绍时，传入null
         */
        public Builder(Context context, ArrayList<String> mImagesUrls, ArrayList<String> mIntroductions) {
            this.context = context;
            this.mImagesUrls = mImagesUrls;
            this.mIntroductions = mIntroductions;
        }

        /**
         * 构建轮播图的指示点的LeftMargin值(这一组小白点在整个父布局中的margin)
         */
        public Builder setIndicatorLeftMargin(int indicatorLeftMargin) {
            p.indicatorLeftMargin = indicatorLeftMargin;
            return this;
        }

        /**
         * 构建轮播图的指示点的bottomMargin值(这一组小白点在整个父布局中的margin)
         */
        public Builder setIndicatorTopMargin(int indicatorTopMargin) {
            p.indicatorTopMargin = indicatorTopMargin;
            return this;
        }

        /**
         * 构建轮播图的指示点的bottomMargin值(这一组小白点在整个父布局中的margin)
         */
        public Builder setIndicatorRightMargin(int indicatorRightMargin) {
            p.indicatorRightMargin = indicatorRightMargin;
            return this;
        }

        /**
         * 构建轮播图的指示点的bottomMargin值(这一组小白点在整个父布局中的margin)
         */
        public Builder setIndicatorBottomMargin(int indicatorBottomMargin) {
            p.indicatorBottomMargin = indicatorBottomMargin;
            return this;
        }


        /**
         * 构建轮播图的未选中指示点的图片资源
         */
        public Builder setIndicatorUncheckedImgResource(int indicatorUncheckedImgResource) {
            p.indicatorUncheckedImgResource = indicatorUncheckedImgResource;
            return this;
        }

        /**
         * 构建轮播图的已选中指示点的图片资源
         */
        public Builder setIndicatorCheckedImgResource(int indicatorCheckedImgResource) {
            p.indicatorCheckedImgResource = indicatorCheckedImgResource;
            return this;
        }

        /**
         * 构建轮播图的每个指示点的LeftMargin值(小白点之间的左间距)
         */
        public Builder setEachIndicatorLeftMargin(int eachIndicatorLeftMargin) {
            p.eachIndicatorLeftMargin = eachIndicatorLeftMargin;
            return this;
        }

        /**
         * 构建轮播图的每个指示点的LeftMargin值(小白点之间的右间距)
         */
        public Builder setEachIndicatorRightMargin(int eachIndicatorRightMargin) {
            p.eachIndicatorRightMargin = eachIndicatorRightMargin;
            return this;
        }

        /**
         * 构建轮播图的每个小白点的宽度
         */
        public Builder setEachIndicatorWidth(int eachIndicatorWidth) {
            p.eachIndicatorWidth = eachIndicatorWidth;
            return this;
        }

        /**
         * 构建轮播图的每个小白点的高度
         */
        public Builder setEachIndicatorHeight(int eachIndicatorHeight) {
            p.eachIndicatorHeight = eachIndicatorHeight;
            return this;
        }

        /**
         * 轮播图小白点指示器的位置（在底部还是顶部，左侧、右侧还是中间）
         */
        public Builder setIndicatorGravity(int indicatorGravity) {
            p.indicatorGravity = indicatorGravity;
            return this;
        }

        /**
         * 轮播图显示的图片的缩放模式
         */
        public Builder setImgScaleType(ImageView.ScaleType imgScaleType) {
            p.imgScaleType = imgScaleType;
            return this;
        }

        /**
         * 文本介绍在轮播图中的位置
         */
        public Builder setIntroductionGravity(int introductionGravity) {
            p.introductionGravity = introductionGravity;
            return this;
        }

        /**
         * 文本介绍的字体颜色
         */
        public Builder setIntroductionTextColor(String introductionTextColor) {
            p.introductionTextColor = introductionTextColor;
            return this;
        }

        /**
         * 文本介绍文字的Appearance
         */
        public Builder setIntroductionTextAppearance(int introductionTextAppearance) {
            p.introductionTextAppearance = introductionTextAppearance;
            return this;
        }

        /**
         * 文本介绍的LeftMargin值
         */
        public Builder setIntroductionLeftMargin(int introductionLeftMargin) {
            p.introductionLeftMargin = introductionLeftMargin;
            return this;
        }

        /**
         * 文本介绍的TopMargin值
         */
        public Builder setIntroductionTopMargin(int introductionTopMargin) {
            p.introductionTopMargin = introductionTopMargin;
            return this;
        }

        /**
         * 文本介绍的RightMargin值
         */
        public Builder setIntroductionRightMargin(int introductionRightMargin) {
            p.introductionRightMargin = introductionRightMargin;
            return this;
        }

        /**
         * 文本介绍的BottomMargin值
         */
        public Builder setIntroductionBottomMargin(int introductionBottomMargin) {
            p.introductionBottomMargin = introductionBottomMargin;
            return this;
        }

        /**
         * 文文本介绍的字体大小，单位已经转换为sp
         */
        public Builder setIntroductTextSize(int introductTextSize) {
            p.introductTextSize = introductTextSize;
            return this;
        }

        /**
         * 文轮播图自动播放的时间间隔（毫秒值）
         */
        public Builder setAutoPlaytimeInterval(int autoPlaytimeInterval) {
            p.autoPlaytimeInterval = autoPlaytimeInterval;
            return this;
        }


        /**
         * 获取构建好的完整的产品——轮播图
         */

        public LunBoTu create() {
            lunBoTu = new LunBoTu(context, mImagesUrls, mIntroductions, p);
            return lunBoTu;
        }


    }

}



