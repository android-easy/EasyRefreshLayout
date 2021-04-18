package com.androideasy.refresh.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;

import com.androideasy.library.adapter.EasyRecyclerAdapter;
import com.androideasy.library.adapter.EasyViewHolder;
import com.androideasy.library.base.BaseRecyclerActivity;
import com.androideasy.refresh.EasyRefreshLayout;
import com.androideasy.refresh.OnLoadMoreListener;
import com.androideasy.refresh.OnRefreshListener;

public class MainActivity extends BaseRecyclerActivity<String> implements OnLoadMoreListener, OnRefreshListener {

    protected EasyRefreshLayout refreshView;
    private Handler mHandler = new Handler();
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        super.initView();
        refreshView =  findViewById(R.id.layout_refresh);
        refreshView.setOnRefreshListener(this);
        refreshView.setOnLoadMoreListener(this);
    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        for (int i = 0; i < 20; i++) {
            mList.add("默认数据" + (i+1));
        }
        mAdapter = new EasyRecyclerAdapter<String>(mContext, mList) {
            @Override
            public int getLayoutId() {
                return R.layout.item;
            }

            @Override
            public void bindViewHolder(EasyViewHolder holder, String s, int position) {
                holder.setText(R.id.text_info ,s);
            }
        };
        return mAdapter;
    }


    @Override
    public void handlerResponse(String response) {

    }

    @Override
    public void onLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mList.add("loadmore");
                            mAdapter.notifyDataSetChanged();
                            refreshView.setLoadingMore(false);

                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onRefresh() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mList.add(0,"refresh");
                            mAdapter.notifyDataSetChanged();
                            refreshView.setRefreshing(false);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}