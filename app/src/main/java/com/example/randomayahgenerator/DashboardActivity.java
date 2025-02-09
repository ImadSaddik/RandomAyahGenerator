package com.example.randomayahgenerator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {
    private LinearLayout ayahStatsLayout, surahStatsLayout, totalPlayCountLayout;
    private BookmarkedAyahDatabaseHelper bookmarkedAyahDatabaseHelper;
    private TextView noDataInDatabaseTextView, totalPlayCountTextView, ayahStatsTitle, surahStatsTitle, totalPlayCountTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dahsboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        instantiateViews();
        instantiateObjects();
        showDashboard();
    }

    private void instantiateViews() {
        ayahStatsTitle = findViewById(R.id.ayahStatsTitle);
        surahStatsTitle = findViewById(R.id.surahStatsTitle);
        totalPlayCountTitle = findViewById(R.id.totalPlayCountTitle);
        noDataInDatabaseTextView = findViewById(R.id.noDataInDatabaseText);
        totalPlayCountTextView = findViewById(R.id.totalPlayCountTextView);

        ayahStatsLayout = findViewById(R.id.ayahStatsContainer);
        surahStatsLayout = findViewById(R.id.surahStatsContainer);
        totalPlayCountLayout = findViewById(R.id.totalPlayCountContainer);
    }

    private void instantiateObjects() {
        bookmarkedAyahDatabaseHelper = new BookmarkedAyahDatabaseHelper(this);
    }

    private void showDashboard() {
        List<Map<String, Object>> rows = bookmarkedAyahDatabaseHelper.getAllAyahs();
        updateUIVisibility(rows.isEmpty());

        List<Map<String, Object>> mostPlayedAyahs = bookmarkedAyahDatabaseHelper.getMostPlayedAyahs(5);
        populateAyahStatsLayout(mostPlayedAyahs);

        List<Map<String, Object>> mostPlayedSurah = bookmarkedAyahDatabaseHelper.getMostPlayedSurah(5);
        populateSurahStatsLayout(mostPlayedSurah);

        int totalPlayCount = bookmarkedAyahDatabaseHelper.getTotalPlayCount();
        String text = totalPlayCount + " " + getString(R.string.play_prefix);
        totalPlayCountTextView.setText(text);
    }

    private void updateUIVisibility(boolean isDatabaseEmpty) {
        noDataInDatabaseTextView.setVisibility(isDatabaseEmpty ? View.VISIBLE: View.GONE);
        ayahStatsTitle.setVisibility(isDatabaseEmpty ? View.GONE: View.VISIBLE);
        surahStatsTitle.setVisibility(isDatabaseEmpty ? View.GONE: View.VISIBLE);
        totalPlayCountTitle.setVisibility(isDatabaseEmpty ? View.GONE: View.VISIBLE);
        ayahStatsLayout.setVisibility(isDatabaseEmpty ? View.GONE: View.VISIBLE);
        surahStatsLayout.setVisibility(isDatabaseEmpty ? View.GONE: View.VISIBLE);
        totalPlayCountLayout.setVisibility(isDatabaseEmpty ? View.GONE: View.VISIBLE);
    }

    private void populateAyahStatsLayout(List<Map<String, Object>> mostPlayedAyahs) {
        ayahStatsLayout.removeAllViews();
        for (int i = 0; i < mostPlayedAyahs.size(); i++) {
            Map<String, Object> ayah = mostPlayedAyahs.get(i);

            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.dashboard_ayah_section_row_template, null);
            String surahName = ayah.get(BookmarkedAyahDatabaseHelper.COLUMN_SURA).toString();
            TextView surahNameTextView = view.findViewById(R.id.surahNameTextView);
            surahNameTextView.setText(surahName);

            TextView ayahNumberTextView = view.findViewById(R.id.ayahNumberTextView);
            int ayahNumber = Integer.parseInt(ayah.get(BookmarkedAyahDatabaseHelper.COLUMN_AYAH_NUMBER).toString());
            String text = getString(R.string.ayah_prefix) + " " + ayahNumber;
            ayahNumberTextView.setText(text);

            TextView ayahContentTextView = view.findViewById(R.id.ayahContentTextView);
            String ayahContent = ayah.get(BookmarkedAyahDatabaseHelper.COLUMN_AYAH).toString();
            ayahContentTextView.setText(ayahContent);

            TextView playCountTextView = view.findViewById(R.id.playCountTextView);
            String ayahPlayCount = ayah.get(BookmarkedAyahDatabaseHelper.COLUMN_PLAY_COUNT).toString();
            text = ayahPlayCount + " " + getString(R.string.play_prefix);
            playCountTextView.setText(text);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (i > 0) {
                params.topMargin = 64;
            }
            view.setLayoutParams(params);
            ayahStatsLayout.addView(view);
        }
    }

    private void populateSurahStatsLayout(List<Map<String, Object>> mostPlayedSurah) {
        surahStatsLayout.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();
        for (int i = 0; i < mostPlayedSurah.size(); i++) {
            Map<String, Object> ayah = mostPlayedSurah.get(i);

            View view = layoutInflater.inflate(R.layout.dashboard_surah_section_row_template, null);
            TextView surahNameTextView = view.findViewById(R.id.surahNameTextView);
            String surahName = ayah.get(BookmarkedAyahDatabaseHelper.COLUMN_SURA).toString();
            surahNameTextView.setText(surahName);

            TextView playCountTextView = view.findViewById(R.id.playCountTextView);
            String surahPlayCount = ayah.get(BookmarkedAyahDatabaseHelper.COLUMN_PLAY_COUNT).toString();
            String text = surahPlayCount + " " + getString(R.string.play_prefix);
            playCountTextView.setText(text);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            if (i > 0) {
                params.topMargin = 64;
            }
            view.setLayoutParams(params);
            surahStatsLayout.addView(view);
        }
    }
}