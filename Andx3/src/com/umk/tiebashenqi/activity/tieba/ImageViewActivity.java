package com.umk.tiebashenqi.activity.tieba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.lidroid.xutils.BitmapUtils;
import com.umk.andx3.util.xutil.BitmapHelp;
import com.umk.tiebashenqi.R;
import com.umk.tiebashenqi.view.TouchImageView;

/**
 *简单可以放大的图片View
 * */
public class ImageViewActivity extends Activity {

    private TouchImageView imageView;
    private BitmapUtils bitmapUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_image_view);

        imageView = (TouchImageView) findViewById(R.id.iv);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");

        bitmapUtils = BitmapHelp.getBitmapUtils(this);

        bitmapUtils.display(imageView,imageUrl);
    }


}