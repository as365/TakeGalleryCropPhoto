package com.ediantong.ui;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.ediantong.R;
import com.ediantong.bean.ImageBean;
import com.ediantong.library.AlbumImageUtils;

import java.util.List;

public class SelectPictureActivity extends AppCompatActivity {

    private Thread mWorkThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadAlbums();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(mWorkThread!=null){
             mWorkThread.interrupt();
        }
    }

    private void loadAlbums() {
        AlbumImageUtils.getImageList(this, new AlbumImageUtils.OnAlbumListener() {
            @Override
            public void returnImageListOnBackThread(final List<ImageBean> imagePathList,Thread workThread) {
                mWorkThread = workThread;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("TAG", "returnImageList: "+imagePathList.size());
                        Log.i("TAG", "returnImageList: "+Thread.currentThread().getName());
                    }
                });
            }
        });
    }

}
