package com.example.jsontreeview;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

/**
 * ViewHolder for a single JSON node item.
 */

public class JsonNodeViewHolder extends RecyclerView.ViewHolder {
    private TextView keyText;
    private TextView valueText;
    private ImageView expandIcon;
    private JsonNode boundNode;
    private String currentQuery = "";
    private JsonNodeViewHolder.ActionHandler actionHandler;

    public interface ActionHandler {
        void onToggleExpand(JsonNode node);
        void onEdit(JsonNode node);
        void onLongClick(Context context, JsonNode node);
    }

    public JsonNodeViewHolder(View itemView, ActionHandler actionHandler) {
        super(itemView);
        this.actionHandler = actionHandler;
        keyText = itemView.findViewById(R.id.text_key);
        valueText = itemView.findViewById(R.id.text_value);
        expandIcon = itemView.findViewById(R.id.icon_expand);
    }

    public void bind(JsonNode node, String query) {
        boundNode = node;
        currentQuery = query.toLowerCase();

        keyText.setText(highlightMatch(node.getKey()));
        valueText.setText(highlightMatch(node.getValue()));

        // Set value color based on type
        switch (node.getValueType()) {
            case "string": valueText.setTextColor(Color.parseColor("#1976D2")); break;
            case "number": valueText.setTextColor(Color.parseColor("#388E3C")); break;
            case "boolean": valueText.setTextColor(Color.parseColor("#F57C00")); break;
            case "null": valueText.setTextColor(Color.GRAY); break;
            default: valueText.setTextColor(Color.BLACK); break;
        }

        // Indentation based on depth
        int paddingStart = 40 * node.getDepth();
        itemView.setPadding(paddingStart, itemView.getPaddingTop(), itemView.getPaddingRight(), itemView.getPaddingBottom());

        // Expand icon
        if (node.hasChildren()) {
            expandIcon.setVisibility(View.VISIBLE);
            expandIcon.setImageResource(node.isExpanded() ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
        } else {
            expandIcon.setVisibility(View.GONE);
        }

        itemView.setOnClickListener(v -> {
            if (node.hasChildren()) {
                actionHandler.onToggleExpand(node);
            }
        });

        valueText.setOnClickListener(v -> actionHandler.onEdit(node));

        itemView.setOnLongClickListener(v -> {
            actionHandler.onLongClick(v.getContext(), node);
            return true;
        });
    }

    private SpannableString highlightMatch(String text) {
        SpannableString span = new SpannableString(text);
        int index = text.toLowerCase().indexOf(currentQuery);
        if (index >= 0) {
            span.setSpan(new BackgroundColorSpan(0xFFFFFF00), index, index + currentQuery.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return span;
    }

    public JsonNode getNode() {
        return boundNode;
    }
}
