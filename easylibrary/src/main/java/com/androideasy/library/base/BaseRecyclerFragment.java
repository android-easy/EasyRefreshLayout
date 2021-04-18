package com.androideasy.library.base;

import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androideasy.library.R;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseRecyclerFragment<T> extends BaseFragment {


    //这四行 就是声明  因为 list 的 所有的 都一样 所以统一到 base里面
    protected List<T> mList = new ArrayList<T>();
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    @Override
    protected int getLayoutId() {
        return     R.layout.base_recycler;
    }

    @Override
    protected void sendRequest() {

    }

    @Override
    protected void initView(View rootView) {
        mRecyclerView =   rootView.findViewById(R.id.base_recycler);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return  new LinearLayoutManager(mContext);
    }
    protected abstract RecyclerView.Adapter getAdapter();
}
