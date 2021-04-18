package com.androideasy.library.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.recyclerview.widget.RecyclerView;


import java.util.List;

/**
 * 类功能:单Adapter
 * 工作室：AndroidEasy
 * 作者：AndroidEasy
 * 邮箱：AndroidEasy@126.com   QQ：1400100300
 */

public abstract class EasyRecyclerAdapter<T> extends RecyclerView.Adapter<EasyViewHolder> {

     /**
     * 保存上下文对象Context
     */
    protected Context mContext;
    /**
     * 对应的实体类列表
     */
    protected List<T> mList;
    /**
     * 布局加载器
     */
    protected LayoutInflater mLayoutInflater;
    /**
     * 点击事件
     */
    protected OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public EasyRecyclerAdapter(Context context, List<T> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public EasyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = mLayoutInflater.inflate(getLayoutId(), parent,false);
        return new EasyViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final EasyViewHolder holder, final int position) {
        bindViewHolder(holder, mList.get(position), position);
        if(onItemClickListener!=null){
            holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder,mList.get(position),position);
                }
            });

        }
    }

    //
    public abstract int getLayoutId();

    //
    public abstract void bindViewHolder(EasyViewHolder holder, T t, int position);


}
