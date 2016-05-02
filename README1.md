# LunBoTu

 * 轮播图：
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
 
      
                   ll = (LinearLayout) findViewById(R.id.ll);

                   LunBoTu.Builder builder = new LunBoTu.Builder(this, mImagesUrls, mIntroductions);
                   // 放开注释，是根据需求自定义轮播图各组件外观的效果
                   // 一般情况只需要
                   //  builder..create().showIn(ll)即可
                   lunBoTu = builder
         //                .setIndicatorBottomMargin(5)
         //                .setIndicatorRightMargin(10)
         //                .setIndicatorCheckedImgResource(R.mipmap.indicator_checked)
         //                .setIndicatorUncheckedImgResource(R.mipmap.indicator_unchecked)
         //                .setEachIndicatorLeftMargin(0)
         //                .setEachIndicatorRightMargin(1)
         //                .setEachIndicatorWidth(10)
         //                .setEachIndicatorHeight(10)
         //                .setIndicatorGravity(Gravity.BOTTOM|Gravity.RIGHT)
         //                .setImgScaleType(ImageView.ScaleType.CENTER_CROP)
         //                .setIntroductionGravity(Gravity.BOTTOM|Gravity.LEFT)
         //                .setIntroductionTextColor("#ffff0000")
         //                .setIntroductionTextAppearance(android.R.style.TextAppearance_DeviceDefault_Large_Inverse)
         //                .setIntroductionLeftMargin(5)
         //                .setIntroductTextSize(17)
         //                .setAutoPlaytimeInterval(3000)
                           .create()
                           .showIn(ll);
           
           
                   lunBoTu.setOnLunBoTuClickListenter(new LunBoTu.OnLunBoTuClickListenter() {
                       @Override
                       public void OnLunBoTuClick(int position) {
                           Toast.makeText(MainActivity.this, "zzz click banner item :" + position, Toast.LENGTH_SHORT).show();
                       }
                   });
               
           
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
