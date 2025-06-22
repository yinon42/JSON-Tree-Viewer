package com.example.jsontreeview;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Adapter for displaying JSON nodes in a tree-like expandable view.
 */

public class JsonNodeAdapter extends RecyclerView.Adapter<JsonNodeViewHolder> {

    private final List<JsonNode> visibleNodes = new ArrayList<>();
    private final List<JsonNode> allNodes = new ArrayList<>();
    private final List<Integer> highlightPositions = new ArrayList<>();
    private final Consumer<JsonNode> onEditRequested;

    private String currentQuery = "";
    private RecyclerView recyclerViewRef;

    public interface OnNodeActionListener {
        void onEdit(JsonNode node);
        void onDelete(JsonNode node);
        void onAddChild(JsonNode node);
        void onCopy(JsonNode node, boolean copyKey);
    }

    private OnNodeActionListener nodeActionListener;

    public JsonNodeAdapter(Consumer<JsonNode> onEditRequested) {
        this.onEditRequested = onEditRequested;
    }

    public void setOnNodeActionListener(OnNodeActionListener listener) {
        this.nodeActionListener = listener;
    }

    public void setData(List<JsonNode> roots) {
        visibleNodes.clear();
        allNodes.clear();
        for (JsonNode node : roots) {
            collectAllNodes(node);
        }
        updateVisibleNodes();
    }

    private void collectAllNodes(JsonNode node) {
        allNodes.add(node);
        if (node.hasChildren()) {
            for (JsonNode child : node.getChildren()) {
                collectAllNodes(child);
            }
        }
    }

    private void updateVisibleNodes() {
        visibleNodes.clear();
        for (JsonNode node : allNodes) {
            if (node.getDepth() == 0) {
                traverseTree(node, 0);
            }
        }
        notifyDataSetChanged();
    }

    private void traverseTree(JsonNode node, int depth) {
        node.setDepth(depth);
        visibleNodes.add(node);
        if (node.isExpanded() && node.hasChildren()) {
            for (JsonNode child : node.getChildren()) {
                traverseTree(child, depth + 1);
            }
        }
    }

    public void highlightQuery(String query) {
        currentQuery = query.toLowerCase();
        highlightPositions.clear();
        for (int i = 0; i < visibleNodes.size(); i++) {
            JsonNode node = visibleNodes.get(i);
            if (node.getKey().toLowerCase().contains(query) || node.getValue().toLowerCase().contains(query)) {
                highlightPositions.add(i);
            }
        }
        notifyDataSetChanged();
    }

    public int getHighlightCount() {
        return highlightPositions.size();
    }

    public void scrollToHighlight(int index) {
        if (index >= 0 && index < highlightPositions.size() && recyclerViewRef != null) {
            int position = highlightPositions.get(index);
            recyclerViewRef.scrollToPosition(position);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerViewRef = recyclerView;
    }

    @Override
    public JsonNodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_json_node, parent, false);
        return new JsonNodeViewHolder(view, new JsonNodeViewHolder.ActionHandler() {
            @Override
            public void onToggleExpand(JsonNode node) {
                node.setExpanded(!node.isExpanded());
                updateVisibleNodes();
            }

            @Override
            public void onEdit(JsonNode node) {
                onEditRequested.accept(node);
            }

            @Override
            public void onLongClick(Context context, JsonNode node) {
                showContextMenu(context, node);
            }
        });
    }

    @Override
    public void onBindViewHolder(JsonNodeViewHolder holder, int position) {
        holder.bind(visibleNodes.get(position), currentQuery);
    }

    @Override
    public int getItemCount() {
        return visibleNodes.size();
    }

    private void showContextMenu(Context context, JsonNode node) {
        String[] options = {"Edit Value", "Add Child", "Delete Node", "Copy Key", "Copy Value"};

        new AlertDialog.Builder(context)
                .setTitle("Node: " + node.getKey())
                .setItems(options, (dialog, which) -> {
                    if (nodeActionListener == null) return;
                    switch (which) {
                        case 0: nodeActionListener.onEdit(node); break;
                        case 1: nodeActionListener.onAddChild(node); break;
                        case 2: nodeActionListener.onDelete(node); break;
                        case 3: nodeActionListener.onCopy(node, true); break;
                        case 4: nodeActionListener.onCopy(node, false); break;
                    }
                })
                .show();
    }

    public List<JsonNode> getTopLevelNodes() {
        List<JsonNode> result = new ArrayList<>();
        for (JsonNode node : allNodes) {
            if (node.getDepth() == 0) {
                result.add(node);
            }
        }
        return result;
    }
}