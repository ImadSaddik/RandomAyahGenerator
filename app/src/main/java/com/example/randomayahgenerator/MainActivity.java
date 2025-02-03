package com.example.randomayahgenerator;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView rightNavigationView;
    private ImageView rightNavigationDrawerIcon;
    private GestureDetectorCompat gestureDetector;
    private HandleSwipeAndDrawers handleSwipeAndDrawers;
    private HandleNavigationDrawersVisibility handleNavigationDrawersVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        instantiateViews();
        instantiateObjects();

        handleNavigationDrawersVisibility.setNavigationDrawerListeners();
    }

    private void instantiateViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        rightNavigationView = findViewById(R.id.rightNavigationDrawer);
        rightNavigationDrawerIcon = findViewById(R.id.rightNavigationDrawerIcon);
    }

    private void instantiateObjects() {
        handleNavigationDrawersVisibility = new HandleNavigationDrawersVisibility(
            rightNavigationDrawerIcon,
            rightNavigationView,
            drawerLayout
        );
        handleSwipeAndDrawers = new HandleSwipeAndDrawers(drawerLayout);
        gestureDetector = new GestureDetectorCompat(
                this,
                handleSwipeAndDrawers
        );
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }
}