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
    private ArrayList<String> mIntroductions = new ArrayList<>();
    private LinearLayout ll;


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

        LunBoTu lunBoTu = new LunBoTu(this, mImagesUrls,mIntroductions);
        ll.addView(lunBoTu);
        lunBoTu.setmOnLunBoTuClickLisenter(new LunBoTu.OnLunBoTuClickLisenter() {
            @Override
            public void OnLunBoTuClick(int position) {
                Toast.makeText(MainActivity.this, "zzz click banner item :" + position, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
