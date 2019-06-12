package com.concise.bottombar;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.chaychan.library.BottomBarItem;
import com.chaychan.library.R.styleable;

import java.util.ArrayList;
import java.util.List;

public class BottomBarLayout<T extends ViewPager> extends LinearLayout implements OnPageChangeListener {
    private static final String STATE_INSTANCE = "instance_state";
    private static final String STATE_ITEM = "state_item";
    private T mViewPager;
    private int mChildCount;
    private List<BottomBarItem> mItemViews;
    private int mCurrentItem;
    private boolean mSmoothScroll;
    private OnItemSelectedListener onItemSelectedListener;
    private OnDirectionListener onDirectionListener;

    public BottomBarLayout(Context context) {
        this(context, (AttributeSet)null);
    }

    public BottomBarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomBarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mItemViews = new ArrayList();
        TypedArray ta = context.obtainStyledAttributes(attrs, styleable.BottomBarLayout);
        this.mSmoothScroll = ta.getBoolean(styleable.BottomBarLayout_smoothScroll, false);
        ta.recycle();
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        this.init();
    }

    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
    }

    public void setViewPager(T viewPager) {
        this.mViewPager = viewPager;
        this.init();
    }

    private void init() {
        this.mItemViews.clear();
        this.mChildCount = this.getChildCount();
        if (this.mChildCount != 0) {
            if (this.mViewPager != null && this.mViewPager.getAdapter().getCount() != this.mChildCount) {
                throw new IllegalArgumentException("LinearLayout的子View数量必须和ViewPager条目数量一致");
            } else {
                for(int i = 0; i < this.mChildCount; ++i) {
                    if (!(this.getChildAt(i) instanceof BottomBarItem)) {
                        throw new IllegalArgumentException("BottomBarLayout的子View必须是BottomBarItem");
                    }

                    BottomBarItem bottomBarItem = (BottomBarItem)this.getChildAt(i);
                    this.mItemViews.add(bottomBarItem);
                    bottomBarItem.setOnClickListener(new MyOnClickListener(i));
                }

                if (this.mCurrentItem < this.mItemViews.size()) {
                    ((BottomBarItem)this.mItemViews.get(this.mCurrentItem)).setStatus(true);
                }

                if (this.mViewPager != null) {
                    this.mViewPager.setOnPageChangeListener(this);
                }
            }
        }
    }

    public void addItem(BottomBarItem item) {
        LayoutParams layoutParams = new LayoutParams(-1, -2);
        layoutParams.weight = 1.0F;
        item.setLayoutParams(layoutParams);
        this.addView(item);
        this.init();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < this.mItemViews.size()) {
            BottomBarItem item = (BottomBarItem)this.mItemViews.get(position);
            if (this.mItemViews.contains(item)) {
                this.resetState();
                this.removeViewAt(position);
                this.init();
            }
        }

    }

    private boolean isLeft=true;
    private int lastValue=-1;
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (onDirectionListener!=null){
            if (positionOffset!=0){
                if (lastValue>=positionOffsetPixels){
                    onDirectionListener.onRight(position);
                    isLeft=false;
                }else if (lastValue<positionOffsetPixels){
                    onDirectionListener.onLeft(position);
                    isLeft=true;
                }
            }
            lastValue=positionOffsetPixels;
        }
    }

    public void onPageSelected(int position) {
        this.resetState();
        ((BottomBarItem)this.mItemViews.get(position)).setStatus(true);
        if (this.onItemSelectedListener != null) {
            this.onItemSelectedListener.onItemSelected(this.getBottomItem(position), this.mCurrentItem, position);
        }

        this.mCurrentItem = position;
    }

    public void onPageScrollStateChanged(int state) {

    }

    private void updateTabState(int position) {
        this.resetState();
        this.mCurrentItem = position;
        ((BottomBarItem)this.mItemViews.get(this.mCurrentItem)).setStatus(true);
    }

    private void resetState() {
        if (this.mCurrentItem < this.mItemViews.size()) {
            ((BottomBarItem)this.mItemViews.get(this.mCurrentItem)).setStatus(false);
        }

    }

    public void setCurrentItem(int currentItem) {
        if (this.mViewPager != null) {
            this.mViewPager.setCurrentItem(currentItem, this.mSmoothScroll);
        } else {
            if (this.onItemSelectedListener != null) {
                this.onItemSelectedListener.onItemSelected(this.getBottomItem(currentItem), this.mCurrentItem, currentItem);
            }

            this.updateTabState(currentItem);
        }

    }

    public void setUnread(int position, int unreadNum) {
        ((BottomBarItem)this.mItemViews.get(position)).setUnreadNum(unreadNum);
    }

    public void setMsg(int position, String msg) {
        ((BottomBarItem)this.mItemViews.get(position)).setMsg(msg);
    }

    public void hideMsg(int position) {
        ((BottomBarItem)this.mItemViews.get(position)).hideMsg();
    }

    public void showNotify(int position) {
        ((BottomBarItem)this.mItemViews.get(position)).showNotify();
    }

    public void hideNotify(int position) {
        ((BottomBarItem)this.mItemViews.get(position)).hideNotify();
    }

    public int getCurrentItem() {
        return this.mCurrentItem;
    }

    public void setSmoothScroll(boolean smoothScroll) {
        this.mSmoothScroll = smoothScroll;
    }

    public BottomBarItem getBottomItem(int position) {
        return (BottomBarItem)this.mItemViews.get(position);
    }

    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instance_state", super.onSaveInstanceState());
        bundle.putInt("state_item", this.mCurrentItem);
        return bundle;
    }

    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle)state;
            this.mCurrentItem = bundle.getInt("state_item");
            this.resetState();
            ((BottomBarItem)this.mItemViews.get(this.mCurrentItem)).setStatus(true);
            super.onRestoreInstanceState(bundle.getParcelable("instance_state"));
        } else {
            super.onRestoreInstanceState(state);
        }

    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    public void setOnDierectionListener(OnDirectionListener onDierectionListener){
        this.onDirectionListener=onDierectionListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(BottomBarItem var1, int var2, int var3);
    }

    public interface OnDirectionListener{
        void onLeft(int position);
        void onRight(int position);
    }

    private class MyOnClickListener implements OnClickListener {
        private int currentIndex;

        public MyOnClickListener(int i) {
            this.currentIndex = i;
        }

        public void onClick(View v) {
            if (BottomBarLayout.this.mViewPager != null) {
                if (this.currentIndex == BottomBarLayout.this.mCurrentItem) {
                    if (BottomBarLayout.this.onItemSelectedListener != null) {
                        BottomBarLayout.this.onItemSelectedListener.onItemSelected(BottomBarLayout.this.getBottomItem(this.currentIndex), BottomBarLayout.this.mCurrentItem, this.currentIndex);
                    }
                } else {
                    BottomBarLayout.this.mViewPager.setCurrentItem(this.currentIndex, BottomBarLayout.this.mSmoothScroll);
                }
            } else {
                if (BottomBarLayout.this.onItemSelectedListener != null) {
                    BottomBarLayout.this.onItemSelectedListener.onItemSelected(BottomBarLayout.this.getBottomItem(this.currentIndex), BottomBarLayout.this.mCurrentItem, this.currentIndex);
                }

                BottomBarLayout.this.updateTabState(this.currentIndex);
            }
        }
    }
}
