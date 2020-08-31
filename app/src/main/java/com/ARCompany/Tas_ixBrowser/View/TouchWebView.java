package com.ARCompany.Tas_ixBrowser.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;

public class TouchWebView extends WebView {

    private ScrollListener scrollListener;

    public TouchWebView(Context context) {
        super(context);
    }

    public TouchWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //Check is required to prevent crash
        if (MotionEventCompat.findPointerIndex(event, 0) == -1) {
            return super.onTouchEvent(event);
        }

        if (event.getPointerCount() >= 2) {
            requestDisallowInterceptTouchEvent(true);
        } else {
            requestDisallowInterceptTouchEvent(true);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        scrollListener.onScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public interface ScrollListener {
        public void onScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY);
    }
    public void setScrollListener( ScrollListener scrollListener){
        this.scrollListener=scrollListener;
    }
}
