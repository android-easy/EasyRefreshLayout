package com.androideasy.library.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 这个是精简版本 ，自己测试时使用的 完整版在Git上
 * 类功能:通用的ViewHolder
 * 工作室：AndroidEasy
 * 作者：AndroidEasy
 * 邮箱：AndroidEasy@126.com   QQ：1400100300
 */
public class EasyViewHolder extends RecyclerView.ViewHolder {

    private final SparseArrayCompat<View> mViews = new SparseArrayCompat<>();
    private View mConvertView;

    public View getConvertView() {
        return mConvertView;
    }

    public EasyViewHolder(View itemView) {
        super(itemView);
        this.mConvertView = itemView;
//        ViewGroup.LayoutParams params =  mConvertView.getLayoutParams();
//        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
    }

    public <T extends View> T getViewById(int viewId) {
        //先从SparseArray里面去 跟从map 取是一个道理的
        View view = mViews.get(viewId);
        if (view == null) {
            // 如果没有就用 ConvertView 获取这个view，然后添加到SparseArray里面，下次就可以直接取了
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置TextView的值
     */
    public TextView setText(int viewId, String text) {
        TextView textView = getViewById(viewId);
        textView.setText(text);
        return textView;
    }

    /**
     * 设置TextView 文字颜色
     */
    public TextView setTextColor(int viewId, int textColor) {
        TextView textView = getViewById(viewId);
        textView.setTextColor(textColor);
        return textView;
    }

    /**
     * 隐藏View
     */
    public <T extends View> T setViewVisible(int viewId, boolean visible) {
        T view = getViewById(viewId);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return view;
    }

}
