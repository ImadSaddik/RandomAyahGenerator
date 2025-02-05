package com.example.randomayahgenerator;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HandleRightNavigationDrawerActions {
    private Activity activity;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private OnDatabaseActionsListener onDatabaseActionsListener;

    public HandleRightNavigationDrawerActions(DrawerLayout drawerLayout, NavigationView navigationView, Activity activity, OnDatabaseActionsListener onDatabaseActionsListener) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;
        this.onDatabaseActionsListener = onDatabaseActionsListener;
    }

    public void setViewDataClickListener() {

    }

    public void setLoadDataClickListener() {
        View leftHeaderView = this.navigationView.getHeaderView(0);
        LinearLayout loadDataButton = leftHeaderView.findViewById(R.id.loadDataAction);
        loadDataButton.setOnClickListener(v -> {
            DatabaseUtils.triggerLoadDatabase(this.activity);
        });
    }

    public void setSaveDataClickListener() {
        View leftHeaderView = this.navigationView.getHeaderView(0);
        LinearLayout saveDataButton = leftHeaderView.findViewById(R.id.saveDataAction);
        saveDataButton.setOnClickListener(v -> {
            DatabaseUtils.triggerSaveDatabase(this.activity);
        });
    }

    public void setDeleteDataClickListener() {
        View leftHeaderView = this.navigationView.getHeaderView(0);
        LinearLayout deleteDataButton = leftHeaderView.findViewById(R.id.deleteDataAction);
        deleteDataButton.setOnClickListener(v -> {
            BookmarkedAyahDatabaseHelper bookmarkedAyahDatabaseHelper = new BookmarkedAyahDatabaseHelper(this.activity);
            bookmarkedAyahDatabaseHelper.deleteAllRowsFromTable();
            if (onDatabaseActionsListener != null) {
                onDatabaseActionsListener.onRowsDeleted();
                this.drawerLayout.closeDrawer(this.navigationView);
            }
        });
    }

    public void setViewDashboardClickListener() {
        View leftHeaderView = this.navigationView.getHeaderView(0);
        LinearLayout viewDashboardButton = leftHeaderView.findViewById(R.id.viewDataAction);

        viewDashboardButton.setOnClickListener(v -> {
            Intent intent = new Intent(this.activity, ViewDataActivity.class);
            this.activity.startActivity(intent);
        });
    }
}
