package com.androideasy.refresh.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.androideasy.refresh.OnLoadMoreListener;
import com.androideasy.refresh.OnRefreshDragListener;

public class EasyFooter extends FrameLayout implements OnLoadMoreListener, OnRefreshDragListener {

    public EasyFooter(Context context) {
        super(context);
    }

    public EasyFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EasyFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onPrepare() {

    }

    @Override
    public void onDrag(int dy,int offset) {

    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public int getDragMaxOffset(View rootView,View target,int targetHeigth) {
        return 0;
    }

    @Override
    public int getDragTriggerOffset(View rootView,View target,int targetHeigth) {
        return 0;
    }

    @Override
    public int getRefreshOrLoadMoreHeight(View rootView, View target, int targetHeight) {
        return 0;
    }
    @Override
    public void onLoadMore() {

    }
}
