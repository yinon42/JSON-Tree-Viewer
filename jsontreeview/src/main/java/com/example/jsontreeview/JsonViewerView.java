package com.example.jsontreeview;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Custom view for displaying and editing JSON as an interactive tree structure.
 */

public class JsonViewerView extends FrameLayout {

    private JsonNodeAdapter adapter;
    private JSONObject currentJson;
    private List<JsonNode> rootNodes;
    private int currentHighlightIndex = -1;

    public JsonViewerView(Context context) {
        super(context);
        init(context);
    }

    public JsonViewerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    //Initializes the view layout, recycler and listeners.
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_json_viewer, this, true);
        EditText searchInput = view.findViewById(R.id.searchInput);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewJson);

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new JsonNodeAdapter(this::showEditDialog);

        adapter.setOnNodeActionListener(new JsonNodeAdapter.OnNodeActionListener() {
            @Override public void onEdit(JsonNode node) { showEditDialog(node); }
            @Override public void onDelete(JsonNode node) { deleteNode(node); }
            @Override public void onAddChild(JsonNode node) { showAddChildDialog(node); }
            @Override public void onCopy(JsonNode node, boolean copyKey) {
                String text = copyKey ? node.getKey() : node.getValue();
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(ClipData.newPlainText("JSON", text));
                Toast.makeText(getContext(), (copyKey ? "Key" : "Value") + " copied", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String query = s.toString().trim().toLowerCase();
                currentHighlightIndex = -1;
                adapter.highlightQuery(query);
            }
        });
    }

    // === Navigation Between Matches ===

    public void goToNextMatch() {
        if (adapter.getHighlightCount() == 0) return;
        currentHighlightIndex = (currentHighlightIndex + 1) % adapter.getHighlightCount();
        adapter.scrollToHighlight(currentHighlightIndex);
    }

    public void goToPreviousMatch() {
        if (adapter.getHighlightCount() == 0) return;
        currentHighlightIndex = (currentHighlightIndex - 1 + adapter.getHighlightCount()) % adapter.getHighlightCount();
        adapter.scrollToHighlight(currentHighlightIndex);
    }

    public void expandAllNodes() {
        if (rootNodes == null) return;
        setExpandedRecursive(rootNodes, true);
        adapter.setData(rootNodes);
    }

    public void collapseAllNodes() {
        if (rootNodes == null) return;
        setExpandedRecursive(rootNodes, false);
        adapter.setData(rootNodes);
    }

    private void setExpandedRecursive(List<JsonNode> nodes, boolean expanded) {
        for (JsonNode node : nodes) {
            node.setExpanded(expanded);
            setExpandedRecursive(node.getChildren(), expanded);
        }
    }

    // === Export, Import, Clipboard, Share ===

    public void exportJsonToFile() {
        try {
            String json = getUpdatedJsonString();
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) downloadsDir.mkdirs();
            File file = new File(downloadsDir, "updated_json.json");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(json);
            }
            Toast.makeText(getContext(), "Saved to Downloads:\n" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to save JSON", Toast.LENGTH_SHORT).show();
        }
    }

    public void importFromUri(Context context, Uri uri) {
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            displayJson(jsonBuilder.toString());
            Toast.makeText(context, "Imported JSON", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to import JSON", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyToClipboard() {
        try {
            String json = getUpdatedJsonString();
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setPrimaryClip(ClipData.newPlainText("JSON", json));
            Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Failed to copy", Toast.LENGTH_SHORT).show();
        }
    }

    public void shareJson(Context context) {
        try {
            String json = getUpdatedJsonString();
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, json);
            sendIntent.setType("text/plain");
            context.startActivity(Intent.createChooser(sendIntent, "Share JSON via..."));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Failed to share JSON", Toast.LENGTH_SHORT).show();
        }
    }

    // === JSON Handling ===

    public void displayJson(String jsonString) {
        try {
            currentJson = new JSONObject(jsonString);
            rootNodes = JsonParserUtil.parseJson(jsonString);
            adapter.setData(rootNodes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUpdatedJsonString() {
        try {
            JSONObject rebuiltJson = JsonParserUtil.buildJsonFromNodes(rootNodes);
            return rebuiltJson.toString(4);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    // === Edit / Add / Delete Dialogs ===

    private void showEditDialog(JsonNode node) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Edit Value: " + node.getKey());
        final EditText input = new EditText(getContext());
        input.setText(node.getValue());
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            node.setValue(input.getText().toString());
            adapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void showAddChildDialog(JsonNode parent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Child to: " + parent.getKey());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_child, null);
        EditText keyInput = view.findViewById(R.id.input_key);
        EditText valueInput = view.findViewById(R.id.input_value);
        builder.setView(view);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String key = keyInput.getText().toString().trim();
            String value = valueInput.getText().toString().trim();
            if (!key.isEmpty()) {
                JsonNode child = new JsonNode(key, value);
                parent.getChildren().add(child);
                parent.setExpanded(true);
                adapter.setData(rootNodes);
            } else {
                Toast.makeText(getContext(), "Key is required", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void deleteNode(JsonNode nodeToDelete) {
        for (JsonNode parent : rootNodes) {
            if (deleteNodeRecursive(parent, nodeToDelete)) {
                adapter.setData(rootNodes);
                Toast.makeText(getContext(), "Node deleted", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }

    private boolean deleteNodeRecursive(JsonNode parent, JsonNode target) {
        if (parent.getChildren().remove(target)) return true;
        for (JsonNode child : parent.getChildren()) {
            if (deleteNodeRecursive(child, target)) return true;
        }
        return false;
    }
}
