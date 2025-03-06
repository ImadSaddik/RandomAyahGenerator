package com.example.randomayahgenerator;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ViewDataActivity extends AppCompatActivity implements OnDatabaseActionsListener {
    private Button addAyahButton;
    private LinearLayout rowsContainer;
    private ScrollView rowsScrollView;
    private TextView noDataInDatabaseTextView;
    private ImageView returnToHomeScreenButton;
    private AddAyahModalHandler addAyahModalHandler;
    private BookmarkedAyahDatabaseHelper bookmarkedAyahDatabaseHelper;
    private final int pageSize = 10;
    private int offset = 0;
    private boolean isLoadingNewRows = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_data);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        instantiateViews();
        instantiateObjects();
        setClickListeners();
        setInfiniteScrolling();
        populateRowsContainer();
        stateManagement(savedInstanceState);
    }

    @Override
    public void onRowsDeleted() {

    }

    @Override
    public void onAyahAdded() {
        populateRowsContainer();
    }

    @Override
    public void onSpecificRowDeleted() {
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (addAyahModalHandler.getIsModalOpen()) {
            outState.putBoolean("addAyahDialogIsVisible", true);
        }
    }

    private void instantiateViews() {
        rowsContainer = findViewById(R.id.rowsContainer);
        rowsScrollView = findViewById(R.id.rowsScrollView);
        addAyahButton = findViewById(R.id.addAyahButton);
        noDataInDatabaseTextView = findViewById(R.id.noDataInDatabaseText);
        returnToHomeScreenButton = findViewById(R.id.returnToHomeScreenButton);
    }

    private void instantiateObjects() {
        bookmarkedAyahDatabaseHelper = new BookmarkedAyahDatabaseHelper(this);
        addAyahModalHandler = new AddAyahModalHandler(this, this);
    }

    private void setClickListeners() {
        returnToHomeScreenButton.setOnClickListener(v -> {
            finish();
        });
        addAyahButton.setOnClickListener(v -> {
            addAyahModalHandler.showDialog();
        });
    }

    private void setInfiniteScrolling() {
        rowsScrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!isLoadingNewRows && isAtBottom(rowsScrollView, rowsContainer) && isMoreDataAvailable()) {
                loadMoreData();
            }
        });
    }

    private boolean isMoreDataAvailable() {
        int ayahCount = bookmarkedAyahDatabaseHelper.getCountOfAyahs();
        return ayahCount > offset + pageSize;
    }

    private boolean isAtBottom(ScrollView scrollView, LinearLayout rowsContainer) {
        if (scrollView.getChildCount() == 0) {
            return false;
        }
        int diff = (rowsContainer.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));
        return diff <= 0;
    }

    private void loadMoreData() {
        isLoadingNewRows = true;
        offset += pageSize;
        List<Map<String, Object>> ayahs = bookmarkedAyahDatabaseHelper.getAllAyahs(pageSize, offset);
        addAyahsToLayout(ayahs);
        isLoadingNewRows = false;
    }

    private void addAyahsToLayout(List<Map<String, Object>> ayahs) {
        for (Map<String, Object> ayah : ayahs) {
            View view = getRowView(ayah);
            rowsContainer.addView(view);
        }
    }

    private void populateRowsContainer() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Map<String, Object>> ayahs = bookmarkedAyahDatabaseHelper.getAllAyahs(pageSize, 0);
            runOnUiThread(() -> {
                rowsContainer.removeAllViews();

                for (Map<String, Object> ayah : ayahs) {
                    View view = getRowView(ayah);
                    rowsContainer.addView(view);
                }

                showHideNoDataInDatabaseTextView(ayahs.isEmpty());
            });
        });

        executor.shutdown();
    }

    private View getRowView(Map<String, Object> row) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.row_design_for_view_activity, null);
        setRowTitle(rowView, row);
        setRowAyahNumber(rowView, row);
        setRowContent(rowView, row);
        setClickListenersForRow(rowView, row);
        return rowView;
    }

    private void setRowTitle(View rowView, Map<String, Object> row) {
        TextView rowTitle = rowView.findViewById(R.id.rowViewActivityTitle);
        String surah = row.get(BookmarkedAyahDatabaseHelper.COLUMN_SURAH).toString();
        rowTitle.setText(surah);
    }

    private void setRowAyahNumber(View rowView, Map<String, Object> row) {
        TextView rowAyahNumber = rowView.findViewById(R.id.rowViewActivityAyahNumber);
        String ayahNumber = row.get(BookmarkedAyahDatabaseHelper.COLUMN_AYAH_NUMBER).toString();
        String subtitle = getString(R.string.ayah_prefix) + ": " + ayahNumber;
        rowAyahNumber.setText(subtitle);
    }

    private void setRowContent(View rowView, Map<String, Object> row) {
        TextView ayahCardContent = rowView.findViewById(R.id.rowViewActivityContent);
        String ayah = row.get(BookmarkedAyahDatabaseHelper.COLUMN_AYAH).toString();
        ayahCardContent.setText(ayah);
    }

    private void setClickListenersForRow(View rowView, Map<String, Object> row) {
        ImageView deleteButton = rowView.findViewById(R.id.rowViewActivityDeleteButton);
        ImageView cancelButton = rowView.findViewById(R.id.rowViewActivityCancelButton);
        ImageView confirmButton = rowView.findViewById(R.id.rowViewActivityConfirmButton);

        deleteButton.setOnClickListener(v -> {
            cancelButton.setVisibility(View.VISIBLE);
            confirmButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.GONE);
        });
        cancelButton.setOnClickListener(v -> {
            cancelButton.setVisibility(View.GONE);
            confirmButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.VISIBLE);
        });
        confirmButton.setOnClickListener(v -> {
            int rowID = Integer.parseInt(row.get(BookmarkedAyahDatabaseHelper.COLUMN_ID).toString());
            bookmarkedAyahDatabaseHelper.deleteRowById(rowID);
            populateRowsContainer();
        });
    }

    private void showHideNoDataInDatabaseTextView(boolean isDatabaseEmpty) {
        if (isDatabaseEmpty) {
            noDataInDatabaseTextView.setVisibility(View.VISIBLE);
        } else {
            noDataInDatabaseTextView.setVisibility(View.GONE);
        }
    }

    private void stateManagement(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            return;
        }

        if (savedInstanceState.getBoolean("addAyahDialogIsVisible")) {
            addAyahModalHandler.showDialog();
        }
    }
}