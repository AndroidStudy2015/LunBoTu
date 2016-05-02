package com.github.c.lunbotudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    /**
     * 要显示的轮播图图片资源
     */
    private ArrayList<String> mImagesUrls = new ArrayList<>();
    /**
     * 要显示的轮播图图片文字介绍
     */
    private ArrayList<String> mIntroductions = new ArrayList<>();
    private LinearLayout ll;
    private LunBoTu lunBoTu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll = (LinearLayout) findViewById(R.id.ll);

        mImagesUrls.add("http://p0.so.qhimg.com/t01c7651db33b1ee0ff.jpg");
        mImagesUrls.add("http://p2.so.qhimg.com/t0128f83da29d80fab7.jpg");
        mImagesUrls.add("http://p4.so.qhimg.com/t012a2c693e86e4f124.jpg");
        mImagesUrls.add("http://p4.so.qhimg.com/t017f5ce99a947731f4.jpg");
        mImagesUrls.add("http://p1.so.qhimg.com/t019fb9638ddd249731.jpg");

        mIntroductions.add("这是一辆好车");
        mIntroductions.add("是私服外挂");
        mIntroductions.add("我各位供热");
        mIntroductions.add("台人员和人工");
        mIntroductions.add("浩特各有刚刚好");
        mIntroductions.add("和他如何和人如果我");

        LunBoTu.Builder builder = new LunBoTu.Builder(this, mImagesUrls, mIntroductions);
//        放开注释，是根据需求自定义轮播图各组件外观的效果
//        一般情况只需要
//        builder..create().showIn(ll)即可
        lunBoTu = builder
                /*.setIndicatorBottomMargin(5)
                .setIndicatorRightMargin(10)
                .setIndicatorCheckedImgResource(R.mipmap.indicator_checked)
                .setIndicatorUncheckedImgResource(R.mipmap.indicator_unchecked)
                .setEachIndicatorLeftMargin(0)
                .setEachIndicatorRightMargin(1)
                .setEachIndicatorWidth(10)
                .setEachIndicatorHeight(10)
                .setIndicatorGravity(Gravity.BOTTOM|Gravity.RIGHT)
                .setImgScaleType(ImageView.ScaleType.CENTER_CROP)
                .setIntroductionGravity(Gravity.BOTTOM|Gravity.LEFT)
                .setIntroductionTextColor("#ffff0000")
                .setIntroductionTextAppearance(android.R.style.TextAppearance_DeviceDefault_Large_Inverse)
                .setIntroductionLeftMargin(5)
                .setIntroductTextSize(17)
                .setAutoPlaytimeInterval(3000)*/
                .create()
                .showIn(ll);


        lunBoTu.setOnLunBoTuClickListenter(new LunBoTu.OnLunBoTuClickListenter() {
            @Override
            public void OnLunBoTuClick(int position) {
                Toast.makeText(MainActivity.this, "zzz click banner item :" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 记得绑定生命周期
     */
    @Override
    protected void onStart() {
        super.onStart();
        lunBoTu.startRoll();
    }

    /**
     * 记得绑定生命周期
     */
    @Override
    protected void onPause() {
        super.onPause();
        lunBoTu.stopScroll();
    }
}
