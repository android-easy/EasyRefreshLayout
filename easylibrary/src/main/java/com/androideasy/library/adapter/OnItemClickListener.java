package com.androideasy.library.adapter;


import androidx.recyclerview.widget.RecyclerView;

/**
 * 类功能:点击事件
 * 工作室：AndroidEasy
 * 作者：AndroidEasy
 * 邮箱：AndroidEasy@126.com   QQ：1400100300
 */

public interface OnItemClickListener<T> {
    void onItemClick(RecyclerView.ViewHolder holder, T t, int position);
}
