package com.example.randomayahgenerator;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

class HandleSwipeAndDrawers extends GestureDetector.SimpleOnGestureListener {
    private final DrawerLayout drawerLayout;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private boolean isDrawerFullyClosed = true;

    public HandleSwipeAndDrawers(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                isDrawerFullyClosed = slideOffset == 0;
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                isDrawerFullyClosed = false;
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                isDrawerFullyClosed = true;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffX = e2.getX() - e1.getX();
        float diffY = e2.getY() - e1.getY();

        if (Math.abs(diffX) > Math.abs(diffY)) {
            boolean isSwipeThresholdReached = Math.abs(diffX) > SWIPE_THRESHOLD;
            boolean isSwipeVelocityThresholdReached = Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD;

            if (isSwipeThresholdReached && isSwipeVelocityThresholdReached && isDrawerFullyClosed) {
                if (diffX <= 0) {
                    openRightDrawer();
                }
            }
            return true;
        }

        return false;
    }

    private void openRightDrawer() {
        drawerLayout.openDrawer(GravityCompat.END);
    }
}
