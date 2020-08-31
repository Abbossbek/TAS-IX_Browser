package com.ARCompany.Tas_ixBrowser.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;

public class SmartNestedScrollView extends NestedScrollView {

    private int slop = 0;
    private float yDistanse;
    private float xDistanse;
    private float lastX;
    private float lastY;

    public SmartNestedScrollView(@NonNull Context context) {
        super(context);
        init(context);
    }


    public SmartNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SmartNestedScrollView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                yDistanse = 0f;
                xDistanse = yDistanse;
                lastX = ev.getX();
                lastY = ev.getY();

                computeScroll();
                break;
            case MotionEvent.ACTION_MOVE:
                float curX=ev.getX();
                float curY=ev.getY();
                xDistanse+=Math.abs(curX-lastX);
                yDistanse+=Math.abs(curY-lastY);
                lastX=curX;
                lastY=curY;

                if(xDistanse>yDistanse){
                    return false;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private void init(Context context) {
        ViewConfiguration configuration = ViewConfiguration.get(context);
        slop = configuration.getScaledEdgeSlop();
    }
}
