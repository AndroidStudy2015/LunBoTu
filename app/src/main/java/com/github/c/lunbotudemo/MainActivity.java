package com.github.c.lunbotudemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    /**
     * 要显示的轮播图图片资源
     */
    private ArrayList<String> mImagesUrls = new ArrayList<>();
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
        mIntroductions.add("浩特很过分");
        mIntroductions.add("和他如何和人如果我");



        LunBoTu.Builder builder = new LunBoTu.Builder(this, mImagesUrls, mIntroductions);
        lunBoTu = builder
                .setIndicatorBottomMargin(5)
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
                .setIntroductionTextColor("#d032aaaa")
                .setIntroductionTextAppearance(android.R.style.TextAppearance_DeviceDefault_Large_Inverse)
                .setIntroductionTopMargin(10)
                .setIntroductTextSize(17)
                .create();
        ll.addView(this.lunBoTu);


        this.lunBoTu.setmOnLunBoTuClickLisenter(new LunBoTu.OnLunBoTuClickLisenter() {
            @Override
            public void OnLunBoTuClick(int position) {
                Toast.makeText(MainActivity.this, "zzz click banner item :" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        lunBoTu.startRoll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        lunBoTu.stopScroll();
    }
}
