package com.example.randomayahgenerator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnAyahAddedListener {
    private DrawerLayout drawerLayout;
    private NavigationView rightNavigationView;
    private ImageView rightNavigationDrawerIcon;
    private GestureDetectorCompat gestureDetector;
    private LinearLayout generatedAyahsContainer;
    private AddAyahModalHandler addAyahModalHandler;
    private TextView noAyahFoundText, generationTypeHintText;
    private BookmarkedAyahDatabaseHelper bookmarkedAyahDatabaseHelper;
    private HandleNavigationDrawersVisibility handleNavigationDrawersVisibility;
    private Button addAyahButton, randomGenerationButton, manualGenerationButton, repeatGenerationButton;

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
        setClickListeners();

        handleNavigationDrawersVisibility.setNavigationDrawerListeners();
        showButtonsBasedOnTheRowCount();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onAyahAdded() {
        showButtonsBasedOnTheRowCount();
    }

    private void instantiateViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        rightNavigationView = findViewById(R.id.rightNavigationDrawer);
        rightNavigationDrawerIcon = findViewById(R.id.rightNavigationDrawerIcon);

        noAyahFoundText = findViewById(R.id.noAyahFoundText);
        generationTypeHintText = findViewById(R.id.generationTypeHintText);

        addAyahButton = findViewById(R.id.addAyahButton);
        randomGenerationButton = findViewById(R.id.randomGenerationButton);
        manualGenerationButton = findViewById(R.id.manualGenerationButton);
        repeatGenerationButton = findViewById(R.id.repeatGenerationButton);

        generatedAyahsContainer = findViewById(R.id.generatedAyahsContainer);
    }

    private void instantiateObjects() {
        handleNavigationDrawersVisibility = new HandleNavigationDrawersVisibility(
            rightNavigationDrawerIcon,
            rightNavigationView,
            drawerLayout
        );
        HandleSwipeAndDrawers handleSwipeAndDrawers = new HandleSwipeAndDrawers(drawerLayout);
        gestureDetector = new GestureDetectorCompat(
                this,
                handleSwipeAndDrawers
        );
        bookmarkedAyahDatabaseHelper = new BookmarkedAyahDatabaseHelper(this);
        addAyahModalHandler = new AddAyahModalHandler(this, this);
    }

    public void showButtonsBasedOnTheRowCount() {
        if (bookmarkedAyahDatabaseHelper.isTableContainingAtLeast2Records()) {
            noAyahFoundText.setVisibility(View.GONE);
            generationTypeHintText.setVisibility(View.VISIBLE);

            addAyahButton.setVisibility(View.GONE);
            randomGenerationButton.setVisibility(View.VISIBLE);
            manualGenerationButton.setVisibility(View.VISIBLE);
        } else {
            noAyahFoundText.setVisibility(View.VISIBLE);
            generationTypeHintText.setVisibility(View.GONE);

            addAyahButton.setVisibility(View.VISIBLE);
            randomGenerationButton.setVisibility(View.GONE);
            manualGenerationButton.setVisibility(View.GONE);
        }
    }

    private void setClickListeners() {
        addAyahButton.setOnClickListener(v -> addAyahModalHandler.showModal());
        repeatGenerationButton.setOnClickListener(v -> {
            generationTypeHintText.setVisibility(View.VISIBLE);
            randomGenerationButton.setVisibility(View.VISIBLE);
            manualGenerationButton.setVisibility(View.VISIBLE);
            repeatGenerationButton.setVisibility(View.GONE);

            generatedAyahsContainer.removeAllViews();
        });
        randomGenerationButton.setOnClickListener(v -> {
            generationTypeHintText.setVisibility(View.GONE);
            randomGenerationButton.setVisibility(View.GONE);
            manualGenerationButton.setVisibility(View.GONE);
            repeatGenerationButton.setVisibility(View.VISIBLE);

            List<Map<String, Object>> rows = bookmarkedAyahDatabaseHelper.getRandomAyahs(2);
            for (int i = 0; i < rows.size(); i++) {
                View ayahCard = getAyahCard(rows.get(i));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                if (i > 0) {
                    params.topMargin = 32;
                }

                ayahCard.setLayoutParams(params);
                generatedAyahsContainer.addView(ayahCard);

            }
        });
    }

    private View getAyahCard(Map<String, Object> row) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View ayahCardView = layoutInflater.inflate(R.layout.generated_ayah_card, null);

        TextView ayahCardTitle = ayahCardView.findViewById(R.id.ayahCardTitle);
        String surah = row.get(BookmarkedAyahDatabaseHelper.COLUMN_SURA).toString();
        ayahCardTitle.setText(surah);

        TextView ayahCardSubTitle = ayahCardView.findViewById(R.id.ayahCardSubTitle);
        String ayahNumber = row.get(BookmarkedAyahDatabaseHelper.COLUMN_AYAH_NUMBER).toString();
        String subtitle = getString(R.string.ayah_prefix) + ": " + ayahNumber;
        ayahCardSubTitle.setText(subtitle);

        TextView ayahCardContent = ayahCardView.findViewById(R.id.ayahCardContent);
        String ayah = row.get(BookmarkedAyahDatabaseHelper.COLUMN_AYAH).toString();
        ayahCardContent.setText(ayah);

        return ayahCardView;
    }
}