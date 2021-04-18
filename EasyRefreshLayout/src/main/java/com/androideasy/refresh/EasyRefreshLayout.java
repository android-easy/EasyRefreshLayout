package com.androideasy.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.NestedScrollingChild;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat; 

public class EasyRefreshLayout extends ViewGroup implements NestedScrollingParent, NestedScrollingChild {

    /**
     * 判断是否在进行 嵌套滑动 对嵌套滑动进行适配
     */
    boolean mNestedScroll = false;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];

    /**
     * 整体需要三个View
     */
    private View mTargetView;
    private View mHeaderView;
    private View mFooterView;

    private int mRefreshOffset = 0;
    private int mLoadMoreOffset = 0;
    private int mHeaderHeight = 0;
    private int mFooterHeight = 0;
    private int mRefreshHeaderHeight = 0;
    private int mLoadMoreFooterHeight = 0;
    private boolean mHasHeaderView;
    private boolean mHasFooterView;
    boolean mRefreshEnabled = true;
    boolean mLoadMoreEnabled = true;
    int mHeaderOffset = 0;
    int mTargetOffset = 0;
    int mFooterOffset = 0;
    //当前状态
    private REFRESH_STATUS mStatus = REFRESH_STATUS.STATUS_DEFAULT;

    private final int mSwipingToRefreshToDefaultScrollingDuration = 200;
    private final int mReleaseToRefreshToRefreshingScrollingDuration = 200;
    private final int mRefreshCompleteDelayDuration = 300;
    private final int mRefreshCompleteToDefaultScrollingDuration = 500;
    private final int mDefaultToRefreshingScrollingDuration = 500;
    private final int mReleaseToLoadMoreToLoadingMoreScrollingDuration = 200;
    private final int mLoadMoreCompleteDelayDuration = 300;
    private final int mLoadMoreCompleteToDefaultScrollingDuration = 300;
    private final int mSwipingToLoadMoreToDefaultScrollingDuration = 200;
    private final int mDefaultToLoadingMoreScrollingDuration = 300;


    //滑动系数
    private static final float DRAG_RATIO = 1.6f ;

    //最大下拉
    private int mRefreshMaxDragOffset = 0;
    private int mLoadMoreMaxDragOffset = 0;
    private AutoScroller mAutoScroller;


    private int mTouchSlop;

    boolean mIsBeingDragged = false;
    /**
     * 计算Y坐标和移动距离dy 支持多点 和单点
     */
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;
    //储存上次的Y坐标
    private float mLastY;
    private float mLastX;
    //记录单次滚动x,y轴偏移量
    private float overDy;
    private float overDx;


    private OnLoadMoreListener mLoadMoreListener;
    private OnRefreshListener mRefreshListener;
    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        this.mLoadMoreListener = listener;
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        this.mRefreshListener = listener;
    }

    public EasyRefreshLayout(Context context) {
        this(context, null);
    }

    public EasyRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public EasyRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //处理自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EasyRefreshLayout);
        if (typedArray != null) {
            LayoutInflater  mLayoutInflater = LayoutInflater.from(context);
            mRefreshEnabled = typedArray.getBoolean(R.styleable.EasyRefreshLayout_refreshenabled, true);
            mLoadMoreEnabled = typedArray.getBoolean(R.styleable.EasyRefreshLayout_loadMoreEnabled, true);

            int headerResourceId = typedArray.getResourceId(R.styleable.EasyRefreshLayout_header, -1);
            if (headerResourceId > 0) {
                View headerView = mLayoutInflater.inflate(headerResourceId, null);
                setHeaderView(headerView);
            }

            int footerResourceId = typedArray.getResourceId(R.styleable.EasyRefreshLayout_footer, -1);
            if (footerResourceId > 0) {
                View footerView = mLayoutInflater.inflate(footerResourceId, null);
                setFooterView(footerView);
            }
            typedArray.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mAutoScroller = new AutoScroller();
        setWillNotDraw(false);
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        setNestedScrollingEnabled(true);
    }

    public void setStatus(REFRESH_STATUS status) {
        this.mStatus = status;
    }

    public boolean isRefreshEnabled() {
        return mRefreshEnabled;
    }

    public void setRefreshEnabled(boolean enable) {
        this.mRefreshEnabled = enable;
    }

    public boolean isLoadMoreEnabled() {
        return mLoadMoreEnabled;
    }

    public void setLoadMoreEnabled(boolean enable) {
        this.mLoadMoreEnabled = enable;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // header
        if (mHeaderView != null) {
            final View headerView = mHeaderView;
            measureChildWithMargins(headerView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = ((MarginLayoutParams) headerView.getLayoutParams());
            mHeaderHeight = headerView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            mRefreshOffset = ((OnRefreshDragListener) mHeaderView).getDragTriggerOffset(this, mHeaderView, mHeaderHeight);
            mRefreshMaxDragOffset = ((OnRefreshDragListener) mHeaderView).getDragMaxOffset(this, mHeaderView, mHeaderHeight);
            mRefreshHeaderHeight = ((OnRefreshDragListener) mHeaderView).getRefreshOrLoadMoreHeight(this, mHeaderView, mHeaderHeight);

            if (mRefreshHeaderHeight <= 0) {
                mRefreshHeaderHeight = mHeaderHeight;
            }
            if (mRefreshOffset <= 0) {
                mRefreshOffset = mRefreshHeaderHeight;
            }
            if (mRefreshMaxDragOffset < mHeaderHeight) {
                mRefreshMaxDragOffset = mHeaderHeight * 2;
            }
        }
        // target
        if (mTargetView != null) {
            final View targetView = mTargetView;
            measureChildWithMargins(targetView, widthMeasureSpec, 0, heightMeasureSpec, 0);
        }
        // footer
        if (mFooterView != null) {
            final View footerView = mFooterView;
            measureChildWithMargins(footerView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = ((MarginLayoutParams) footerView.getLayoutParams());
            mFooterHeight = footerView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            mLoadMoreOffset = ((OnRefreshDragListener) mFooterView).getDragTriggerOffset(this, mFooterView, mFooterHeight);
            mLoadMoreMaxDragOffset = ((OnRefreshDragListener) mFooterView).getDragMaxOffset(this, mFooterView, mFooterHeight);
            mLoadMoreFooterHeight = ((OnRefreshDragListener) mFooterView).getRefreshOrLoadMoreHeight(this, mFooterView, mFooterHeight);

            if (mLoadMoreFooterHeight <= 0) {
                mLoadMoreFooterHeight = mFooterHeight;
            }

            if (mLoadMoreOffset <= 0) {
                mLoadMoreOffset = mLoadMoreFooterHeight;
            }

            if (mLoadMoreMaxDragOffset < mFooterHeight) {
                mLoadMoreMaxDragOffset = mFooterHeight * 2;
            }
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
        mHasHeaderView = (mHeaderView != null);
        mHasFooterView = (mFooterView != null);
    }

    /**
     * layout children
     */
    private void layoutChildren() {
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        if (mTargetView == null) {
            return;
        }
        if (mHeaderView != null) {
            final View headerView = mHeaderView;
            MarginLayoutParams lp = (MarginLayoutParams) headerView.getLayoutParams();
            final int headerLeft = paddingLeft + lp.leftMargin;
            final int headerTop = paddingTop + lp.topMargin - mHeaderHeight + mHeaderOffset;
            final int headerRight = headerLeft + headerView.getMeasuredWidth();
            final int headerBottom = headerTop + headerView.getMeasuredHeight();

            headerView.layout(headerLeft, headerTop, headerRight, headerBottom);
        }
        if (mTargetView != null) {
            final View targetView = mTargetView;
            MarginLayoutParams lp = (MarginLayoutParams) targetView.getLayoutParams();
            final int targetLeft = paddingLeft + lp.leftMargin;
            final int targetTop;

            targetTop = paddingTop + lp.topMargin + mTargetOffset;

            final int targetRight = targetLeft + targetView.getMeasuredWidth();
            final int targetBottom = targetTop + targetView.getMeasuredHeight();
            targetView.layout(targetLeft, targetTop, targetRight, targetBottom);
        }
        if (mFooterView != null) {
            final View footerView = mFooterView;
            MarginLayoutParams lp = (MarginLayoutParams) footerView.getLayoutParams();
            final int footerLeft = paddingLeft + lp.leftMargin;
            final int footerBottom = height - paddingBottom - lp.bottomMargin + mFooterHeight + mFooterOffset;
            final int footerTop = footerBottom - footerView.getMeasuredHeight();
            final int footerRight = footerLeft + footerView.getMeasuredWidth();

            footerView.layout(footerLeft, footerTop, footerRight, footerBottom);
        }

        if (mHeaderView != null) {
            mHeaderView.bringToFront();
        }
        if (mFooterView != null) {
            mFooterView.bringToFront();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mTargetView == null) {
            ensureTarget();
        }
        if (mTargetView == null) {
            return;
        }
        if (mHeaderView != null) {
            mHeaderView.setVisibility(View.GONE);
        }

        if (mFooterView != null) {
            mFooterView.setVisibility(View.GONE);
        }
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        if (REFRESH_STATUS.isRefreshing(mStatus) || REFRESH_STATUS.isLoadingMore(mStatus) || mNestedScroll) {
            return false;
        }
        dealMulTouchEvent(ev);

        mAutoScroller.abortIfRunning();

        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:

                mIsBeingDragged = isMyControlScroll();
                if (!mIsBeingDragged) {
                    resetCurrentStatusLayout();
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged = false;
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    //结束
                    finishSpinner();
                }
                break;
        }
        return mIsBeingDragged;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        ensureTarget();
        if (REFRESH_STATUS.isRefreshing(mStatus) || REFRESH_STATUS.isLoadingMore(mStatus) || mNestedScroll) {
            return false;
        }
        dealMulTouchEvent(ev);
        mAutoScroller.abortIfRunning();


        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE: {

                mIsBeingDragged = isMyControlScroll();
                if (mIsBeingDragged) {
                    fingerScroll(-overDy);
                } else {
                    if (overDy != 0) {
                        resetCurrentStatusLayout();
//                    //把滚动事件交给内部控件处理
                        ev.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(ev);
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN: {

                break;
            }
            case MotionEventCompat.ACTION_POINTER_UP:
                break;

            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {
                    mIsBeingDragged = false;
                    //结束
                    finishSpinner();
                }
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }

        return mIsBeingDragged;
    }


    /**
     * 判断是否进行拖拽
     * @return
     */
    private boolean isMyControlScroll() {
        if (Math.abs(overDy) < Math.abs(overDx) || Math.abs(overDy) < mTouchSlop) {
            return mIsBeingDragged;
        }
        if ((-overDy > mTouchSlop && checkRefreshAble()) || (checkRefreshAble() && mTargetOffset - overDy > 0)) {
            return true;

        } else if ((-overDy < -mTouchSlop && checkLoadMoreAble()) || (checkLoadMoreAble() && mTargetOffset - overDy < 0)) {
            return true;
        }
        return false;
    }

    /**
     *  多点处理
     * @param ev
     */
    public void dealMulTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;

            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                mLastX = x;
                mLastY = y;
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                overDx = mLastX - x;
                overDy = mLastY - y;
                mLastY = y;
                mLastX = x;
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId != mActivePointerId) {
                    mLastX = MotionEventCompat.getX(ev, pointerIndex);
                    mLastY = MotionEventCompat.getY(ev, pointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastX = MotionEventCompat.getX(ev, newPointerIndex);
                    mLastY = MotionEventCompat.getY(ev, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
                }
                break;
            }
        }
    }


    public void setRefreshing(boolean refreshing) {
        if (!isRefreshEnabled() || mHeaderView == null) {
            return;
        }
        if (refreshing) {
            if (REFRESH_STATUS.isStatusDefault(mStatus)) {
                setStatus(REFRESH_STATUS.STATUS_RELEASE_TO_REFRESH);
                scrollDefaultToRefreshing();
            }
        } else {
            if (REFRESH_STATUS.isRefreshing(mStatus)) {
                mRefreshCallback.onComplete();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollRefreshingToDefault();
                    }
                }, mRefreshCompleteDelayDuration);
            }
        }
    }

    public void setLoadingMore(boolean loadingMore) {
        if (!isLoadMoreEnabled() || mFooterView == null) {
            return;
        }
        if (loadingMore) {
            if (REFRESH_STATUS.isStatusDefault(mStatus)) {
                setStatus(REFRESH_STATUS.STATUS_SWIPING_TO_LOAD_MORE);
                scrollDefaultToLoadingMore();
            }
        } else {
            if (REFRESH_STATUS.isLoadingMore(mStatus)) {
                mLoadMoreCallback.onComplete();
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollLoadingMoreToDefault();
                    }
                }, mLoadMoreCompleteDelayDuration);
            }
        }
    }

    public void setHeaderView(View view) {
        if (view instanceof OnRefreshDragListener) {
            if (mHeaderView != null && mHeaderView != view) {
                removeView(mHeaderView);
            }
            if (mHeaderView != view) {
                this.mHeaderView = view;
                addView(view);
                mRefreshCallback.onReset();
            }
        } else {
        }
    }

    public void setFooterView(View view) {
        if (view instanceof OnRefreshDragListener) {
            if (mFooterView != null && mFooterView != view) {
                removeView(mFooterView);
            }
            if (mFooterView != view) {
                this.mFooterView = view;
                addView(mFooterView);
                mLoadMoreCallback.onReset();
            }
        } else {
        }
    }


    private boolean checkLoadMoreAble() {
        return mLoadMoreEnabled && !canChildScrollDown() && mHasFooterView && mLoadMoreOffset > 0;
    }

    private boolean checkRefreshAble() {
        return mRefreshEnabled && !canChildScrollUp() && mHasHeaderView && mRefreshOffset > 0;
    }

    private void ensureTarget() {

        if (mTargetView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (!child.equals(mHeaderView) && !child.equals(mFooterView)) {
                    mTargetView = child;
                    break;
                }
            }
        }
    }

    protected boolean canChildScrollUp() {
        if(mTargetView!=null){
            mTargetView.canScrollVertically( -1);
        }
        return false;
    }

    protected boolean canChildScrollDown() {
        if(mTargetView!=null){
            mTargetView.canScrollVertically( 1);
        }
        return false;
    }


    // NestedScrollingParent  需要 重写的方法
    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return isEnabled() && !REFRESH_STATUS.isRefreshing(mStatus) && !REFRESH_STATUS.isLoadingMore(mStatus)
                && (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
        startNestedScroll(axes & ViewCompat.SCROLL_AXIS_VERTICAL);
        mNestedScroll = true;
    }


    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        //dy > 0  手指向上滑动
        if (dy > 0 && mTargetOffset > 0) {
            if (dy > mTargetOffset) {
                consumed[1] = mTargetOffset;
            } else {
                consumed[1] = dy;
            }
            fingerScroll(-consumed[1]);
        } else if (dy < 0 && mTargetOffset < 0) {
            //dy < 0  手指向下滑动
            if (dy < mTargetOffset) {
                consumed[1] = mTargetOffset;
            } else {
                consumed[1] = dy;
            }
            fingerScroll(-consumed[1]);
        }
        final int[] parentConsumed = mParentScrollConsumed;
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0];
            consumed[1] += parentConsumed[1];
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public void onStopNestedScroll(View target) {
        mNestedScrollingParentHelper.onStopNestedScroll(target);
        mNestedScroll = false;
        if (mTargetOffset != 0) {
            finishSpinner();
        }
        stopNestedScroll();
    }

    @Override
    public void onNestedScroll(final View target, final int dxConsumed, final int dyConsumed,
                               final int dxUnconsumed, final int dyUnconsumed) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow);

        final int dy = dyUnconsumed + mParentOffsetInWindow[1];

        if (dy < 0 && checkRefreshAble()) {
            fingerScroll(-dy);
        } else if (dy > 0 && checkLoadMoreAble()) {
            fingerScroll(-dy);
        }
    }


    // NestedScrollingChild  需要 重写的方法
    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed,
                                        int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {

        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX,
                                    float velocityY) {

        return dispatchNestedPreFling(velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY,
                                 boolean consumed) {
        boolean nestedFling = dispatchNestedFling(velocityX, velocityY, consumed);
        return nestedFling;
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }


    /**
     * 刷新帮助类
     */
    abstract class OnRefreshCallback implements OnRefreshDragListener, OnRefreshListener {
    }

    /**
     * 加载更多帮助类
     */
    abstract class OnLoadMoreCallback implements OnRefreshDragListener, OnLoadMoreListener {
    }

    OnRefreshCallback mRefreshCallback = new OnRefreshCallback() {
        @Override
        public void onPrepare() {
            if (mHeaderView != null && mHeaderView instanceof OnRefreshDragListener && REFRESH_STATUS.isStatusDefault(mStatus)) {
                mHeaderView.setVisibility(VISIBLE);
                ((OnRefreshDragListener) mHeaderView).onPrepare();
            }
        }

        @Override
        public void onDrag(int y, int offset) {
            if (mHeaderView != null && mHeaderView instanceof OnRefreshDragListener && REFRESH_STATUS.isRefreshStatus(mStatus)) {
                if (mHeaderView.getVisibility() != VISIBLE) {
                    mHeaderView.setVisibility(VISIBLE);
                }
                ((OnRefreshDragListener) mHeaderView).onDrag(y, offset);
            }
        }

        @Override
        public void onRelease() {
            if (mHeaderView != null && mHeaderView instanceof OnRefreshDragListener && REFRESH_STATUS.isReleaseToRefresh(mStatus)) {
                ((OnRefreshDragListener) mHeaderView).onRelease();
            }
        }

        @Override
        public void onRefresh() {
            if (mHeaderView != null && REFRESH_STATUS.isRefreshing(mStatus)) {
                if (mHeaderView instanceof OnRefreshListener) {
                    ((OnRefreshListener) mHeaderView).onRefresh();
                }
                if (mRefreshListener != null) {
                    mRefreshListener.onRefresh();
                }
            }
        }

        @Override
        public void onComplete() {
            if (mHeaderView != null && mHeaderView instanceof OnRefreshDragListener) {
                ((OnRefreshDragListener) mHeaderView).onComplete();
            }
        }

        @Override
        public void onReset() {
            if (mHeaderView != null && mHeaderView instanceof OnRefreshDragListener && REFRESH_STATUS.isStatusDefault(mStatus)) {
                ((OnRefreshDragListener) mHeaderView).onReset();
                mHeaderView.setVisibility(GONE);
            }
        }

        @Override
        public int getDragMaxOffset(View rootView, View target, int targetHeight) {
            if (mHeaderView != null && mHeaderView instanceof OnRefreshDragListener) {
                return ((OnRefreshDragListener) mHeaderView).getDragMaxOffset(rootView, target, targetHeight);
            }
            return 0;
        }

        @Override
        public int getDragTriggerOffset(View rootView, View target, int targetHeight) {
            if (mHeaderView != null && mHeaderView instanceof OnRefreshDragListener) {
                return ((OnRefreshDragListener) mHeaderView).getDragTriggerOffset(rootView, target, targetHeight);
            }
            return 0;
        }

        @Override
        public int getRefreshOrLoadMoreHeight(View rootView, View target, int targetHeight) {
            if (mHeaderView != null && mHeaderView instanceof OnRefreshDragListener) {
                return ((OnRefreshDragListener) mHeaderView).getRefreshOrLoadMoreHeight(rootView, target, targetHeight);
            }
            return 0;
        }

    };

    OnLoadMoreCallback mLoadMoreCallback = new OnLoadMoreCallback() {

        @Override
        public void onPrepare() {
            if (mFooterView != null && mFooterView instanceof OnRefreshDragListener && REFRESH_STATUS.isStatusDefault(mStatus)) {
                mFooterView.setVisibility(VISIBLE);
                ((OnRefreshDragListener) mFooterView).onPrepare();
            }
        }

        @Override
        public void onDrag(int y, int offset) {
            if (mFooterView != null && mFooterView instanceof OnRefreshDragListener && REFRESH_STATUS.isLoadMoreStatus(mStatus)) {
                if (mFooterView.getVisibility() != VISIBLE) {
                    mFooterView.setVisibility(VISIBLE);
                }
                ((OnRefreshDragListener) mFooterView).onDrag(y, offset);
            }
        }

        @Override
        public void onRelease() {
            if (mFooterView != null && mFooterView instanceof OnRefreshDragListener && REFRESH_STATUS.isReleaseToLoadMore(mStatus)) {
                ((OnRefreshDragListener) mFooterView).onRelease();
            }
        }

        @Override
        public void onLoadMore() {
            if (mFooterView != null && REFRESH_STATUS.isLoadingMore(mStatus)) {
                if (mFooterView instanceof OnLoadMoreListener) {
                    ((OnLoadMoreListener) mFooterView).onLoadMore();
                }
                if (mLoadMoreListener != null) {
                    mLoadMoreListener.onLoadMore();
                }
            }
        }

        @Override
        public void onComplete() {
            if (mFooterView != null && mFooterView instanceof OnRefreshDragListener) {
                ((OnRefreshDragListener) mFooterView).onComplete();
            }
        }

        @Override
        public void onReset() {
            if (mFooterView != null && mFooterView instanceof OnRefreshDragListener && REFRESH_STATUS.isStatusDefault(mStatus)) {
                ((OnRefreshDragListener) mFooterView).onReset();
                mFooterView.setVisibility(GONE);
            }
        }


        @Override
        public int getDragMaxOffset(View rootView, View target, int targetHeight) {
            if (mFooterView != null && mFooterView instanceof OnRefreshDragListener) {
                return ((OnRefreshDragListener) mFooterView).getDragMaxOffset(rootView, target, targetHeight);
            }
            return 0;
        }

        @Override
        public int getDragTriggerOffset(View rootView, View target, int targetHeight) {
            if (mFooterView != null && mFooterView instanceof OnRefreshDragListener) {
                return ((OnRefreshDragListener) mFooterView).getDragTriggerOffset(rootView, target, targetHeight);
            }
            return 0;
        }

        @Override
        public int getRefreshOrLoadMoreHeight(View rootView, View target, int targetHeight) {
            if (mFooterView != null && mFooterView instanceof OnRefreshDragListener) {
                return ((OnRefreshDragListener) mFooterView).getRefreshOrLoadMoreHeight(rootView, target, targetHeight);
            }
            return 0;
        }
    };



    /**
     * 更新滑动状态
     */
    private void updateScrollStatus() {
        if (mTargetOffset > mTouchSlop && checkRefreshAble()) {
            //下拉
            if (mTargetOffset >= mRefreshOffset) {
                setStatus(REFRESH_STATUS.STATUS_RELEASE_TO_REFRESH);
            } else {
                setStatus(REFRESH_STATUS.STATUS_SWIPING_TO_REFRESH);
            }
            mRefreshCallback.onPrepare();
        } else if (mTargetOffset < -mTouchSlop && checkLoadMoreAble()) {
            //上拉
            if (-mTargetOffset >= mLoadMoreOffset) {
                setStatus(REFRESH_STATUS.STATUS_RELEASE_TO_LOAD_MORE);
            } else {
                setStatus(REFRESH_STATUS.STATUS_SWIPING_TO_LOAD_MORE);
            }
            mLoadMoreCallback.onPrepare();
        } else if (Math.abs(mTargetOffset) < mTouchSlop) {
            setStatus(REFRESH_STATUS.STATUS_DEFAULT);
        }
    }

    /**
     * finger Scroll 滑动
     *
     * @param yDiff
     */
    private void fingerScroll(final float yDiff) {
        updateScrollStatus();

        float ratio = DRAG_RATIO;
        float yScrolled = yDiff / ratio;
        float tmpTargetOffset = yScrolled + mTargetOffset;
        if ((tmpTargetOffset > 0 && mTargetOffset < 0)
                || (tmpTargetOffset < 0 && mTargetOffset > 0)) {
            yScrolled = -mTargetOffset;
        }
        if (mRefreshMaxDragOffset >= mRefreshOffset && tmpTargetOffset > mRefreshMaxDragOffset) {
            yScrolled = mRefreshMaxDragOffset - mTargetOffset;
        } else if (mLoadMoreMaxDragOffset >= mLoadMoreOffset && -tmpTargetOffset > mLoadMoreMaxDragOffset) {
            yScrolled = -mLoadMoreMaxDragOffset - mTargetOffset;
        }
        if (REFRESH_STATUS.isRefreshStatus(mStatus)) {
            mRefreshCallback.onDrag(mTargetOffset, mRefreshOffset);
        } else if (REFRESH_STATUS.isLoadMoreStatus(mStatus)) {
            mLoadMoreCallback.onDrag(mTargetOffset, mLoadMoreOffset);
        }
        updateScroll(yScrolled);
    }


    private void finishSpinner() {
        updateScrollStatus();
        if (REFRESH_STATUS.isSwipingToRefresh(mStatus)) {
            scrollToDefault();
        } else if (REFRESH_STATUS.isSwipingToLoadMore(mStatus)) {
            scrollToDefault();
        } else if (REFRESH_STATUS.isReleaseToRefresh(mStatus)) {
            mRefreshCallback.onRelease();
            scrollReleaseToRefreshToRefreshing();
        } else if (REFRESH_STATUS.isReleaseToLoadMore(mStatus)) {
            mLoadMoreCallback.onRelease();
            scrollReleaseToLoadMoreToLoadingMore();
        } else {
            scrollToDefault();
        }
    }


    private void scrollDefaultToRefreshing() {
        mAutoScroller.autoScroll((int) (mRefreshOffset + 0.5f), mDefaultToRefreshingScrollingDuration);
    }

    private void scrollDefaultToLoadingMore() {
        mAutoScroller.autoScroll(-(int) (mLoadMoreOffset + 0.5f), mDefaultToLoadingMoreScrollingDuration);
    }

    private void scrollSwipingToRefreshToDefault() {
        mAutoScroller.autoScroll(-mHeaderOffset, mSwipingToRefreshToDefaultScrollingDuration);
    }

    private void scrollSwipingToLoadMoreToDefault() {
        mAutoScroller.autoScroll(-mFooterOffset, mSwipingToLoadMoreToDefaultScrollingDuration);
    }

    private void scrollReleaseToRefreshToRefreshing() {
        mAutoScroller.autoScroll(mRefreshHeaderHeight - mHeaderOffset, mReleaseToRefreshToRefreshingScrollingDuration);
    }

    private void scrollReleaseToLoadMoreToLoadingMore() {
        mAutoScroller.autoScroll(-mFooterOffset - mLoadMoreFooterHeight, mReleaseToLoadMoreToLoadingMoreScrollingDuration);
    }

    private void scrollRefreshingToDefault() {
        mAutoScroller.autoScroll(-mHeaderOffset, mRefreshCompleteToDefaultScrollingDuration);
    }

    private void scrollLoadingMoreToDefault() {
        mAutoScroller.autoScroll(-mFooterOffset, mLoadMoreCompleteToDefaultScrollingDuration);
    }

    private void scrollToDefault() {
        mAutoScroller.autoScroll(-mTargetOffset, mLoadMoreCompleteToDefaultScrollingDuration);
    }

    /**
     * 滑动帮助类
     */
    private class AutoScroller implements Runnable {

        private Scroller mScroller;

        private int mmLastY;

        private boolean mRunning = false;

        private boolean mAbort = false;

        public AutoScroller() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int currY = mScroller.getCurrY();
            int yDiff = currY - mmLastY;
            if (finish) {
                finish();
            } else {
                mmLastY = currY;
                EasyRefreshLayout.this.autoScroll(yDiff);
                post(this);
            }
        }

        private void finish() {
            mmLastY = 0;
            mRunning = false;
            removeCallbacks(this);
            // if abort by user, don't call
            if (!mAbort) {
                scrollToFinish();
            }
        }

        public void abortIfRunning() {
            if (mRunning) {
                if (!mScroller.isFinished()) {
                    mAbort = true;
                    mScroller.forceFinished(true);
                }
                finish();
                mAbort = false;
            }
        }

        private void autoScroll(int yScrolled, int duration) {
            removeCallbacks(this);
            mmLastY = 0;
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, yScrolled, duration);
            post(this);
            mRunning = true;
        }
    }
    /**
     * 完成滑动充值CallBack  之后充值View 避免出现混乱
     */
    private void scrollToFinish() {

        switch (mStatus){
            case STATUS_LOADING_MORE:
                setStatus(REFRESH_STATUS.STATUS_DEFAULT);
                mLoadMoreCallback.onReset();
                break;
            case STATUS_RELEASE_TO_LOAD_MORE:
                setStatus(REFRESH_STATUS.STATUS_LOADING_MORE);
                mLoadMoreCallback.onLoadMore();
                break;
            case STATUS_RELEASE_TO_REFRESH:
                setStatus(REFRESH_STATUS.STATUS_REFRESHING);
                mRefreshCallback.onRefresh();
                break;
            case STATUS_REFRESHING:
                setStatus(REFRESH_STATUS.STATUS_DEFAULT);
            case STATUS_DEFAULT:
                mRefreshCallback.onReset();
                break;
            default:
                setStatus(REFRESH_STATUS.STATUS_DEFAULT);
                mLoadMoreCallback.onReset();
                mRefreshCallback.onReset();
                break;
        }
        resetCurrentStatusLayout();
    }

    private void autoScroll(final float yScroll) {
        if (REFRESH_STATUS.isRefreshStatus(mStatus)) {
            mRefreshCallback.onDrag(mTargetOffset, mRefreshOffset);
        } else if (REFRESH_STATUS.isLoadMoreStatus(mStatus)) {
            mLoadMoreCallback.onDrag(mTargetOffset, mLoadMoreOffset);
        }
        updateScroll(yScroll);
    }

    private void updateScroll(final float yScroll) {
        if (yScroll == 0) {
            return;
        }
        mTargetOffset += yScroll;

        if (REFRESH_STATUS.isRefreshStatus(mStatus)) {
            mHeaderOffset = mTargetOffset;
            mFooterOffset = 0;
        } else if (REFRESH_STATUS.isLoadMoreStatus(mStatus)) {
            mFooterOffset = mTargetOffset;
            mHeaderOffset = 0;
        }
        layoutChildren();
        invalidate();
    }


    /**
     *  取消或手指离开后，重新设置当前状态
     */
    private void resetCurrentStatusLayout() {
        mFooterOffset = 0;
        mHeaderOffset = 0;
        mTargetOffset = 0;
        if (REFRESH_STATUS.isRefreshing(mStatus)) {
            mTargetOffset = (int) (mRefreshOffset + 0.5f);
            mHeaderOffset = mTargetOffset;
        }   else if (REFRESH_STATUS.isLoadingMore(mStatus)) {
            mTargetOffset = -(int) (mLoadMoreOffset + 0.5f);
            mFooterOffset = mTargetOffset;
        }
        layoutChildren();
        invalidate();
    }

}
