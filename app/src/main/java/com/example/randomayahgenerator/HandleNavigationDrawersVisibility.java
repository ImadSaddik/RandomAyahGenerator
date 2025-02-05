package com.example.randomayahgenerator;

import android.widget.ImageView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HandleNavigationDrawersVisibility {
    private ImageView rightNavigationDrawerIcon;
    private DrawerLayout drawerLayout;

    public HandleNavigationDrawersVisibility(
            ImageView rightNavigationDrawerIcon,
            DrawerLayout drawerLayout
    ) {
        this.rightNavigationDrawerIcon = rightNavigationDrawerIcon;
        this.drawerLayout = drawerLayout;
    }

    public void setNavigationDrawerListeners() {
        this.rightNavigationDrawerIcon.setOnClickListener(v -> {
            this.drawerLayout.openDrawer(GravityCompat.END);
        });
    }
}
