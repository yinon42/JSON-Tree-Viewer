package com.example.jsonviewerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.jsontreeview.JsonViewerView;

public class MainActivity extends AppCompatActivity {

    // === UI Elements ===
    private EditText urlInput;
    private EditText jsonInput;
    private ProgressBar progressBar;
    private JsonViewerView viewer;

    private Button loadJsonBtn, loadUrlBtn, btnSaveJson, btnImportFile;
    private Button btnShare, btnCopyJson, btnPrevMatch, btnNextMatch;
    private Button btnExpandAll, btnCollapseAll;

    private ActivityResultLauncher<Intent> filePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupFilePickerLauncher();
        setupButtons();
        requestStoragePermissionIfNeeded();
    }

    private void initViews() {
        urlInput = findViewById(R.id.url_input);
        jsonInput = findViewById(R.id.json_input);
        viewer = findViewById(R.id.json_viewer);
        progressBar = findViewById(R.id.progressBar);

        loadJsonBtn = findViewById(R.id.load_json_button);
        loadUrlBtn = findViewById(R.id.load_url_button);
        btnSaveJson = findViewById(R.id.btnSaveJson);
        btnImportFile = findViewById(R.id.btnImportFile);
        btnShare = findViewById(R.id.btnShare);
        btnCopyJson = findViewById(R.id.btnCopyJson);
        btnPrevMatch = findViewById(R.id.btnPrevMatch);
        btnNextMatch = findViewById(R.id.btnNextMatch);
        btnExpandAll = findViewById(R.id.btnExpandAll);
        btnCollapseAll = findViewById(R.id.btnCollapseAll);
    }

    private void setupFilePickerLauncher() {
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            viewer.importFromUri(this, uri);
                        }
                    }
                }
        );
    }

    private void setupButtons() {
        btnSaveJson.setOnClickListener(v -> viewer.exportJsonToFile());
        btnCopyJson.setOnClickListener(v -> viewer.copyToClipboard());
        btnShare.setOnClickListener(v -> viewer.shareJson(this));

        btnImportFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/json");
            filePickerLauncher.launch(intent);
        });

        loadUrlBtn.setOnClickListener(v -> {
            String urlStr = urlInput.getText().toString().trim();
            if (urlStr.isEmpty()) {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    java.net.URL url = new java.net.URL(urlStr);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(5000);

                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();

                    String finalResult = result.toString();
                    runOnUiThread(() -> {
                        try {
                            viewer.displayJson(finalResult);
                        } catch (Exception e) {
                            Toast.makeText(this, "Failed to parse JSON", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Failed to load from URL", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    });
                }
            }).start();
        });

        loadJsonBtn.setOnClickListener(v -> {
            String jsonStr = jsonInput.getText().toString().trim();
            if (jsonStr.isEmpty()) {
                Toast.makeText(this, "Please paste a valid JSON", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                viewer.displayJson(jsonStr);
            } catch (Exception e) {
                Toast.makeText(this, "Invalid JSON format", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        btnPrevMatch.setOnClickListener(v -> viewer.goToPreviousMatch());
        btnNextMatch.setOnClickListener(v -> viewer.goToNextMatch());
        btnExpandAll.setOnClickListener(v -> viewer.expandAllNodes());
        btnCollapseAll.setOnClickListener(v -> viewer.collapseAllNodes());
    }

    private void requestStoragePermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
