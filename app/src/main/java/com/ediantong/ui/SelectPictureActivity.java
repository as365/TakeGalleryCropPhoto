package com.ediantong.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ediantong.R;
import com.ediantong.adapter.ImagePickAdapter;
import com.ediantong.bean.ImageBean;
import com.ediantong.library.AlbumImageUtils;

import java.util.ArrayList;
import java.util.List;

public class SelectPictureActivity extends AppCompatActivity {

    private Thread mWorkThread;
    private List<String> mList = new ArrayList<>();
    private RecyclerView recycler_view;
    private int mCowCount = 4;
    private ImagePickAdapter adapter = new ImagePickAdapter(R.layout.item_image_pick,mList,mCowCount);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_picture);
        initView();
    }

    private void initView() {
        recycler_view = findViewById(R.id.recycler_view);
        recycler_view.setLayoutManager(new GridLayoutManager(this,mCowCount));
        recycler_view.setAdapter(adapter);
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
                        mList.clear();
                        for (int i = 0; i < imagePathList.size(); i++) {
                            mList.add(imagePathList.get(i).path);
                        }
                        adapter.setNewData(mList);
                    }
                });
            }
        });
    }

}
