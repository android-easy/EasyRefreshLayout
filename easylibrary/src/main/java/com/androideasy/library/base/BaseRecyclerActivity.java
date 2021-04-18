package com.androideasy.library.base;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androideasy.library.R;
import com.androideasy.library.http.OnNetResultListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 缩减版本 ，测试使用版本 ，这里暂时删除下拉刷新功能
 * 只提供最基础的列表页面 方便操作
 *
 * @param <T>
 */
public abstract class BaseRecyclerActivity<T> extends BaseActivity implements   OnNetResultListener {

    //这四行 就是声明  因为 list 的 所有的 都一样 所以统一到 base里面
    protected List<T> mList = new ArrayList<T>();
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;

    protected int page = 1;

    protected int getLayoutId() {
         return R.layout.base_recycler;
    }

    @Override
    protected void initView() {
        mRecyclerView =   findViewById(R.id.base_recycler);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mAdapter = getAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return  new LinearLayoutManager(this);
    }

    protected abstract RecyclerView.Adapter getAdapter();


//    public abstract void getDataApi();

    @Override
    public void onSuccess (String response) {
        handlerResponse(response);
     }

    @Override
    public void onFailure (String errMsg) {
        Log.i(TAG ,"========onFailure===============" +errMsg);
      //  ToastUtils.showToast(mContext, "网络错误，请检测网络稍后再试");
    }


    public abstract void handlerResponse(String response);

}
