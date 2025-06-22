package com.example.jsontreeview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Draws tree lines to visually represent node hierarchy in the JSON tree.
 */

public class TreeItemDecoration extends RecyclerView.ItemDecoration {

    private final Paint paint;

    public TreeItemDecoration() {
        paint = new Paint();
        paint.setColor(0xFFCCCCCC); // Light gray
        paint.setStrokeWidth(4f);
    }

    @Override
    public void onDrawOver(@NonNull Canvas canvas, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int count = parent.getChildCount();

        for (int i = 0; i < count; i++) {
            View view = parent.getChildAt(i);
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);

            if (holder instanceof JsonNodeViewHolder) {
                JsonNodeViewHolder nodeHolder = (JsonNodeViewHolder) holder;
                JsonNode node = nodeHolder.getNode();

                int depth = node.getDepth();
                if (depth == 0) continue;

                int startX = view.getLeft() + (40 * depth) - 20;
                int centerY = (view.getTop() + view.getBottom()) / 2;

                canvas.drawLine(startX, view.getTop(), startX, view.getBottom(), paint);
                canvas.drawLine(startX, centerY, startX + 20, centerY, paint);
            }
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(0, 10, 0, 10); // Adds vertical spacing between items
    }
}
