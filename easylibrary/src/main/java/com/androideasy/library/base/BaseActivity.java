package com.androideasy.library.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.io.Serializable;

import butterknife.ButterKnife;

/**
 * 类功能:封装好的BaseActivity
 * 公司：IsCoding工作室
 * 作者：IsCoding
 * 邮箱：iscoding@126.com   QQ：1400100300
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getName();
    protected Activity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置状态栏文字颜色及图标为深色
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
     //   getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mContext = this;
        initDataBeforeLoadLayout();
        //设置布局 绑定布局
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initDataBeforeInitView();
        initView();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
      //  ButterKnife.
    }

    /**
     * 初始化View视图前加载数据
     * 方便视图初始化时候使用
     * 加载布局前加载已经传入的数据 根据数据加载布局 情况
     */
    protected void initDataBeforeInitView() {}

    /**
     * 初始化Layout布局前加载数据
     * 方便用户根据数据去加载不同的布局
     */
    protected void initDataBeforeLoadLayout() {}

    /**
     * 加载Layout
     */
    protected abstract int getLayoutId();

    /**
     * 初始化视图
     */
    protected abstract void initView();

    /**
     * 业务逻辑
     * 一般为发送网络请求
     */
    public void loadData() {  }

    public void openActivity(Class<?> cls) {
        Intent i = new Intent(this, cls);
        startActivity(i);
    }

    protected void openActivity(Class<?> cls, String key, String value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    protected void openActivity(Class<?> cls, String key, Serializable value) {
        Intent intent = new Intent(this, cls);
        intent.putExtra(key, value);
        startActivity(intent);
    }

    protected void openActivity(Class<?> cls, Bundle extras) {
        Intent intent = new Intent(this, cls);
        intent.putExtras(extras);
        startActivity(intent);
    }

    /**
     * 切换fragment
     *
     * @param fragment
     */
    public void addFragment(Fragment fragment, int res_id) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //不存在 fragment--添加
        if (fm.getFragments() == null || fm.getFragments().size() == 0) {
            transaction.add(res_id, fragment);
            transaction.commit();
            return;
        }
    }

    /**
     * 切换fragment
     *
     * @param fragment
     */
    public void addFragmentList(Fragment fragment, int res_id, Fragment fragment2, int res_id2) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //不存在 fragment--添加
        transaction.add(res_id, fragment);
        transaction.add(res_id2, fragment2);
        transaction.commit();
    }
}
