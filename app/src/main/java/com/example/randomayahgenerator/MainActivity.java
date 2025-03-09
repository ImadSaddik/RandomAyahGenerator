package com.example.randomayahgenerator;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnDatabaseActionsListener {
    private DrawerLayout drawerLayout;
    private NavigationView rightNavigationView;
    private ArrayList<Integer> generatedAyahIDs;
    private ImageView rightNavigationDrawerIcon;
    private GestureDetectorCompat gestureDetector;
    private LinearLayout generatedAyahsContainer;
    private AddAyahModalHandler addAyahModalHandler;
    private TextView noAyahFoundText, generationTypeHintText;
    private BookmarkedAyahDatabaseHelper bookmarkedAyahDatabaseHelper;
    private HandleNavigationDrawersVisibility handleNavigationDrawersVisibility;
    private Button addAyahButton, randomGenerationButton, repeatGenerationButton;
    private HandleRightNavigationDrawerActions handleRightNavigationDrawerActions;

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
        updateUIBasedOnAyahCount();
        stateManagement(savedInstanceState);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onAyahAdded() {
        updateUIBasedOnAyahCount();
    }

    @Override
    public void onRowsDeleted() {
        updateUIBasedOnAyahCount();
    }

    @Override
    public void onSpecificRowDeleted() {
        updateUIBasedOnAyahCount();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == ResultCodes.REQUEST_CODE_SAVE_DATABASE || requestCode == ResultCodes.REQUEST_CODE_LOAD_DATABASE) {
                handleDatabaseAction(requestCode, data);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUIBasedOnAyahCount();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (addAyahModalHandler.getIsModalOpen()) {
            outState.putBoolean("addAyahDialogIsVisible", true);
        }
        if (!generatedAyahIDs.isEmpty()) {
            outState.putIntegerArrayList("ayahIds", generatedAyahIDs);
        }
    }

    private void instantiateViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        rightNavigationView = findViewById(R.id.rightNavigationDrawer);
        rightNavigationDrawerIcon = findViewById(R.id.rightNavigationDrawerIcon);

        noAyahFoundText = findViewById(R.id.noAyahFoundText);
        generationTypeHintText = findViewById(R.id.generationTypeHintText);

        addAyahButton = findViewById(R.id.addAyahButton);
        randomGenerationButton = findViewById(R.id.randomGenerationButton);
        repeatGenerationButton = findViewById(R.id.repeatGenerationButton);

        generatedAyahsContainer = findViewById(R.id.generatedAyahsContainer);
    }

    private void instantiateObjects() {
        generatedAyahIDs = new ArrayList<>();
        handleNavigationDrawersVisibility = new HandleNavigationDrawersVisibility(
            rightNavigationDrawerIcon,
            drawerLayout
        );
        HandleSwipeAndDrawers handleSwipeAndDrawers = new HandleSwipeAndDrawers(drawerLayout);
        gestureDetector = new GestureDetectorCompat(
                this,
                handleSwipeAndDrawers
        );
        bookmarkedAyahDatabaseHelper = new BookmarkedAyahDatabaseHelper(this);
        addAyahModalHandler = new AddAyahModalHandler(this, this);
        handleRightNavigationDrawerActions = new HandleRightNavigationDrawerActions(
                drawerLayout,
                rightNavigationView,
                this,
                this
        );
    }

    public void updateUIBasedOnAyahCount() {
        boolean hasNoGeneratedAyahs = generatedAyahsContainer.getChildCount() == 0;
        boolean has2OrMoreRecords = bookmarkedAyahDatabaseHelper.isTableContainingAtLeast2Records();

        noAyahFoundText.setVisibility(has2OrMoreRecords ? View.GONE : View.VISIBLE);
        if (hasNoGeneratedAyahs) {
            generationTypeHintText.setVisibility(has2OrMoreRecords ? View.VISIBLE : View.GONE);
            addAyahButton.setVisibility(has2OrMoreRecords ? View.GONE : View.VISIBLE);
            repeatGenerationButton.setVisibility(View.GONE);
            randomGenerationButton.setVisibility(has2OrMoreRecords ? View.VISIBLE : View.GONE);
        } else {
            generationTypeHintText.setVisibility(View.GONE);
            addAyahButton.setVisibility(has2OrMoreRecords ? View.GONE : View.VISIBLE);
            repeatGenerationButton.setVisibility(has2OrMoreRecords ? View.VISIBLE : View.GONE);
            randomGenerationButton.setVisibility(View.GONE);

            if (!has2OrMoreRecords) {
                generatedAyahsContainer.removeAllViews();
            }
        }
    }

    private void setClickListeners() {
        handleRightNavigationDrawerActions.setLoadDataClickListener();
        handleRightNavigationDrawerActions.setSaveDataClickListener();
        handleRightNavigationDrawerActions.setDeleteDataClickListener();
        handleRightNavigationDrawerActions.setViewDashboardClickListener();
        handleRightNavigationDrawerActions.setViewDataClickListener();

        addAyahButton.setOnClickListener(v -> addAyahModalHandler.showDialog());
        repeatGenerationButton.setOnClickListener(v -> {
            generationTypeHintText.setVisibility(View.VISIBLE);
            randomGenerationButton.setVisibility(View.VISIBLE);
            repeatGenerationButton.setVisibility(View.GONE);

            generatedAyahsContainer.removeAllViews();
            generatedAyahIDs.clear();
        });
        randomGenerationButton.setOnClickListener(v -> {
            generationTypeHintText.setVisibility(View.GONE);
            randomGenerationButton.setVisibility(View.GONE);
            repeatGenerationButton.setVisibility(View.VISIBLE);

            List<Map<String, Object>> rows = bookmarkedAyahDatabaseHelper.getRandomAyahs(2);
            addAyahCardsToContainer(rows);
        });
    }

    private View getAyahCard(Map<String, Object> row) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View ayahCardView = layoutInflater.inflate(R.layout.generated_ayah_card, null);

        TextView ayahCardTitle = ayahCardView.findViewById(R.id.ayahCardTitle);
        String surah = row.get(BookmarkedAyahDatabaseHelper.COLUMN_SURAH).toString();
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

    private void incrementPlayCount(Map<String, Object> row) {
        String surah = row.get(BookmarkedAyahDatabaseHelper.COLUMN_SURAH).toString();
        int ayahNumber = Integer.parseInt(row.get(BookmarkedAyahDatabaseHelper.COLUMN_AYAH_NUMBER).toString());
        bookmarkedAyahDatabaseHelper.incrementPlayCount(surah, ayahNumber);
    }

    private void handleDatabaseAction(int requestCode, Intent data) {
        if (requestCode == ResultCodes.REQUEST_CODE_SAVE_DATABASE) {
            Uri destinationUri = data.getData();
            DatabaseUtils.saveDatabase(this, destinationUri);
            this.drawerLayout.closeDrawer(this.rightNavigationView);
        } else if (requestCode == ResultCodes.REQUEST_CODE_LOAD_DATABASE) {
            Uri sourceUri = data.getData();
            DatabaseUtils.loadDatabase(this, sourceUri);
            this.drawerLayout.closeDrawer(this.rightNavigationView);
            updateUIBasedOnAyahCount();
        }
    }

    private void stateManagement(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        if (savedInstanceState.getBoolean("addAyahDialogIsVisible")) {
            addAyahModalHandler.showDialog();
        }

        if (savedInstanceState.getIntegerArrayList("ayahIds") != null) {
            generatedAyahIDs = savedInstanceState.getIntegerArrayList("ayahIds");
            if (generatedAyahIDs == null) {
                return;
            }

            List<Map<String, Object>> rows = new ArrayList<>();
            for (int ayahId : generatedAyahIDs) {
                rows.add(bookmarkedAyahDatabaseHelper.getAyahByID(ayahId));
            }
            addAyahCardsToContainer(rows);
        }
    }

    private void addAyahCardsToContainer(List<Map<String, Object>> rows) {
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            Object columnID = rows.get(i).get(BookmarkedAyahDatabaseHelper.COLUMN_ID);
            if (columnID != null) {
                int ayahID = Integer.parseInt(columnID.toString());
                generatedAyahIDs.add(ayahID);
            }

            View ayahCard = getAyahCard(row);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (i > 0) {
                params.topMargin = 32;
            }

            ayahCard.setLayoutParams(params);
            generatedAyahsContainer.addView(ayahCard);
            incrementPlayCount(row);

            ayahCard.setOnClickListener(v -> {
                String surah = row.get(BookmarkedAyahDatabaseHelper.COLUMN_SURAH).toString();
                int ayahNumber =  Integer.parseInt(row.get(BookmarkedAyahDatabaseHelper.COLUMN_AYAH_NUMBER).toString());

                Intent intent = new Intent(this, SurahViewer.class);
                intent.putExtra(BookmarkedAyahDatabaseHelper.COLUMN_SURAH, surah);
                intent.putExtra(BookmarkedAyahDatabaseHelper.COLUMN_AYAH_NUMBER, ayahNumber);
                startActivity(intent);
            });
        }
        updateUIBasedOnAyahCount();
    }
}