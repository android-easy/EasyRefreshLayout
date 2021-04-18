package com.androideasy.refresh.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androideasy.refresh.R;


public class TextRefreshFooterView extends EasyFooter {

    private TextView tvLoadMore;
    private ImageView ivSuccess;
    private ProgressBar progressBar;


    public TextRefreshFooterView(Context context) {
        this(context, null);
    }

    public TextRefreshFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextRefreshFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tvLoadMore = findViewById(R.id.tvLoadMore);
        ivSuccess =  findViewById(R.id.ivSuccess);
        progressBar = findViewById(R.id.progressbar);
    }

    @Override
    public void onPrepare() {
        ivSuccess.setVisibility(INVISIBLE);
    }

    @Override
    public void onDrag(int y, int offset) {

        if(y < 0){
            ivSuccess.setVisibility(INVISIBLE);
            progressBar.setVisibility(INVISIBLE);
            int deY = Math.abs(y);
            if(deY >= offset){
                tvLoadMore.setText("松开开始加载");
            }else {
                tvLoadMore.setText("加载更多");
            }
        }
    }

    @Override
    public void onLoadMore() {
        tvLoadMore.setText("正在加载>>>");
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void onRelease() {

    }

    @Override
    public void onComplete() {
        progressBar.setVisibility(INVISIBLE);
        ivSuccess.setVisibility(VISIBLE);
    }

    @Override
    public void onReset() {
        ivSuccess.setVisibility(INVISIBLE);
    }
}
