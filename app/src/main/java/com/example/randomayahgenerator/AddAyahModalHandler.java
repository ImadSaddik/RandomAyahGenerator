package com.example.randomayahgenerator;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AddAyahModalHandler {
    private Activity activity;
    private boolean isModalOpen = false;
    private DatabaseHelper databaseHelper;
    private QuranDatabaseHelper quranDatabaseHelper;
    private OnAyahAddedListener ayahAddedListener;

    public AddAyahModalHandler(Activity activity, OnAyahAddedListener ayahAddedListener) {
        this.activity = activity;
        this.quranDatabaseHelper = new QuranDatabaseHelper(this.activity);
        this.databaseHelper = new DatabaseHelper(this.activity);
        this.ayahAddedListener = ayahAddedListener;
    }

    public void showModal() {
        if (isModalOpen) {
            return;
        }
        // Extracts the views from the layout file
        LayoutInflater layoutInflater = this.activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.add_ayah_dialog, null);
        AutoCompleteTextView surahDropdown = view.findViewById(R.id.surahDropdown);
        AutoCompleteTextView ayahDropdown = view.findViewById(R.id.ayahDropdown);
        TextInputLayout ayahTextInputLayout = view.findViewById(R.id.ayahLayout);
        TextView ayahTextView = view.findViewById(R.id.ayahPreviewTextView);
        ImageView closeModalIcon = view.findViewById(R.id.closeAddAyahModalIcon);
        TextView addAyahButton = view.findViewById(R.id.addAyahButtonModal);


        addAyahButton.setEnabled(false);
        ayahTextInputLayout.setVisibility(View.GONE);

        // Populate the surah dropdown with the list of surah names
        List<String> surahNames = quranDatabaseHelper.getListOfSurahNames();
        ArrayAdapter<String> surahAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, surahNames);
        surahDropdown.setAdapter(surahAdapter);

        // Populate the ayah dropdown with the list of ayah numbers for the selected surah
        surahDropdown.setOnItemClickListener((adapterView, view1, position, l) -> {
            String selectedSurah = (String) adapterView.getItemAtPosition(position);
            List<Integer> ayahNumbers = quranDatabaseHelper.getSurahAyahMapping().get(selectedSurah);
            if (ayahNumbers == null) {
                ayahNumbers = new ArrayList<>();
            }
            ayahDropdown.setText("");
            ayahTextView.setText("");
            addAyahButton.setEnabled(false);
            ayahTextInputLayout.setVisibility(View.VISIBLE);

            ArrayAdapter<Integer> ayahAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, ayahNumbers);
            ayahDropdown.setAdapter(ayahAdapter);
        });

        // Show the selected Ayah in the text view
        ayahDropdown.setOnItemClickListener((adapterView, view2, position, l) -> {
            String selectedSurah = surahDropdown.getText().toString();
            int selectedAyah = (int) adapterView.getItemAtPosition(position);
            ayahTextView.setText(quranDatabaseHelper.getAyahText(selectedSurah, selectedAyah));
            addAyahButton.setEnabled(true);
        });

        AlertDialog dialog = getAlertDialog(view);
        // Set click listeners
        closeModalIcon.setOnClickListener(v -> hideDialog(dialog));
        addAyahButton.setOnClickListener(v -> {
            String surah = surahDropdown.getText().toString();
            int ayahNumber = Integer.parseInt(ayahDropdown.getText().toString());
            String ayahText = ayahTextView.getText().toString();

            databaseHelper.addAyah(ayahText, ayahNumber, surah);
            hideDialog(dialog);
        });

        // Show the dialog
        isModalOpen = true;
        dialog.show();
    }

    private AlertDialog getAlertDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setView(view);

        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dialogInterface -> isModalOpen = false);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.add_ayah_dialog_rounded_background);
        return dialog;
    }

    private void hideDialog(AlertDialog dialog) {
        dialog.dismiss();
        isModalOpen = false;
        if (ayahAddedListener != null) {
            ayahAddedListener.onAyahAdded();
        }
    }
}
