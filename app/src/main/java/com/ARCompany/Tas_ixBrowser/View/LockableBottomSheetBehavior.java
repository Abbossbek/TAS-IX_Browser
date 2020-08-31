package com.ARCompany.Tas_ixBrowser.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class LockableBottomSheetBehavior extends BottomSheetBehavior {
    private boolean swipeEnabled = false;

    public LockableBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
        if (swipeEnabled) {
            return super.onInterceptTouchEvent(parent, child, event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(CoordinatorLayout parent, View child, MotionEvent event) {
        if (swipeEnabled) {
            return super.onTouchEvent(parent, child, event);
        } else {
            return false;
        }
    }

    public void setSwipeEnabled(boolean swipeEnabled) {
        this.swipeEnabled = swipeEnabled;
    }
}
