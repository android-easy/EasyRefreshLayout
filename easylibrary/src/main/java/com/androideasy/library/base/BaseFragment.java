package com.androideasy.library.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {

    protected Fragment mFragment;
    protected Context mContext;
    protected Activity mActivity;

    protected View rootView;// 缓存Fragment view

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragment = this;
        mContext = getContext();
        mActivity = getActivity();

        initArguments(savedInstanceState);
    }

    protected   void initArguments(Bundle savedInstanceState){};

    /**
     * 很多时候onCreateView会被调用，比如页面切换，如果已经初始化过的页面会重新初始化
     * 此处代码是为了实现程序复用添加的，避免重复调用onCreateView每次都要重新初始化控件
     * 此处代码直接封装好，子类直接使用initView就可以
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            // 初始化整体布局pull_list
            rootView = inflater.inflate(getLayoutId(), container, false);
            //加载 Fragment 不全屏问题
            if (rootView instanceof RelativeLayout) {
                 RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                rootView.setLayoutParams(lp);
            } else if (rootView instanceof LinearLayout) {
                 LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                rootView.setLayoutParams(lp);
            }

        } else {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null) {
                parent.removeView(rootView);
            }
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView(view);
        sendRequest();
    }

    protected abstract int getLayoutId();

    protected void initData() {
    }

    protected abstract void sendRequest();

    protected void initView(View rootView) {
    }


    /**
     * 打开新的activity
     */
    //Base类一般会封装一些常用方法，方便子类继承和调用，共性的东西可以添加在这个位置
    public void openActivity(Class<?> pClass) {
        Intent intent = new Intent(mActivity, pClass);
        startActivity(intent);
    }

    // Base类一般会封装一些常用方法，方便子类继承和调用，共性的东西可以添加在这个位置
    public void openActivity(Class<?> pClass, String name, String value) {
        Intent intent = new Intent(mActivity, pClass);
        intent.putExtra(name, value);
        startActivity(intent);
    }

}
