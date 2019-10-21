package com.ediantong.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ediantong.R;
import com.ediantong.utils.ScreenUtils;

import java.io.File;
import java.util.List;

public class ImagePickAdapter extends BaseQuickAdapter<String, ImagePickAdapter.ImageViewHolder> {

    //列数
    private int cowCount;
    public ImagePickAdapter(int layoutResId, @Nullable List<String> data,int cowCount) {
        super(layoutResId, data);
        this.cowCount = cowCount;
    }

    public static class  ImageViewHolder  extends BaseViewHolder{
        public ImageViewHolder(View view) {
            super(view);
        }
    }

    @Override
    protected void convert( ImageViewHolder helper, String path) {
        ImageView imageView = helper.getView(R.id.imageView);
        int width = ScreenUtils.getScreenWidth(mContext) / cowCount;
        Glide.with(mContext).load(new File(path)).into(imageView);
    }

}
