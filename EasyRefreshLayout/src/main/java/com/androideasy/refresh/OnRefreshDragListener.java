package com.androideasy.refresh;

import android.view.View;

public interface OnRefreshDragListener {

    void onPrepare();

    void onDrag(int dy, int triggerOffset);

    void onRelease();

    void onComplete();

    void onReset();

    int getDragMaxOffset(View rootView,View target,int targetHeight);

    int getDragTriggerOffset(View rootView,View target,int targetHeight);

    int getRefreshOrLoadMoreHeight(View rootView,View target,int targetHeight);
}
