package com.example.randomayahgenerator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SurahViewer extends AppCompatActivity {
    private int ayahNumber;
    private String surahName;
    private TextView surahTitle;
    private ScrollView scrollView;
    private LinearLayout ayahsContainer;
    private View selectedAyahToScrollTo;
    private ImageView returnToHomeScreenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_surah_viewer);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        instantiateViews();
        setClickListeners();
        getIntentData();
        setTitle();
        setContent();
        scrollToAyah();
    }

    private void instantiateViews() {
        surahTitle = findViewById(R.id.surahTitle);
        scrollView = findViewById(R.id.scrollView);
        ayahsContainer = findViewById(R.id.ayahsContainer);
        returnToHomeScreenButton = findViewById(R.id.returnToHomeScreenButton);
    }

    private void setClickListeners() {
        returnToHomeScreenButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void getIntentData() {
        Intent intent = getIntent();
        surahName = intent.getStringExtra(BookmarkedAyahDatabaseHelper.COLUMN_SURAH);
        ayahNumber = intent.getIntExtra(BookmarkedAyahDatabaseHelper.COLUMN_AYAH_NUMBER, 0);
    }

    private void setTitle() {
        String newTitle = surahTitle.getText() + " " + surahName;
        surahTitle.setText(newTitle);
    }

    private void setContent() {
        QuranDatabaseHelper quranDatabaseHelper = new QuranDatabaseHelper(this);
        List<String> ayahs = quranDatabaseHelper.getAyahsBySurahName(surahName);
        if (ayahs.isEmpty()) {
            return;
        }

        for (int i = 0; i < ayahs.size(); i++) {
            String ayah = ayahs.get(i);
            View rowView = getRowView(ayah, i + 1);
            ayahsContainer.addView(rowView);
        }
    }

    private View getRowView(String ayahContent, int currentAyahNumber) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.row_design_for_surah_view_activity, null);
        setRowAyahNumber(view, currentAyahNumber);
        setRowAyahContent(view, ayahContent);
        if (ayahNumber == currentAyahNumber) {
            LinearLayout linearLayout = view.findViewById(R.id.rootLayer);
            linearLayout.setBackgroundResource(R.drawable.selected_ayah_number_background);
            selectedAyahToScrollTo = view;
        }
        return view;
    }

    private void setRowAyahNumber(View rowView, int ayahNumber) {
        TextView rowAyahNumber = rowView.findViewById(R.id.rowAyahNumber);
        String subtitle = getString(R.string.ayah_prefix) + ": " + ayahNumber;
        rowAyahNumber.setText(subtitle);
    }

    private void setRowAyahContent(View rowView, String ayahContent) {
        TextView ayahCardContent = rowView.findViewById(R.id.rowAyahContent);
        ayahCardContent.setText(ayahContent);
    }

    private void scrollToAyah() {
        if (selectedAyahToScrollTo != null) {
            scrollView.postDelayed(() -> {
                int top = selectedAyahToScrollTo.getTop();
                int marginTop = 50;
                scrollView.smoothScrollTo(0, top - marginTop);
                selectedAyahToScrollTo.requestFocus();
            }, 300);
        }
    }
}