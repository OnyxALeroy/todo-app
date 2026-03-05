package fr.onyxleroy.to_do.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Toast;

import fr.onyxleroy.to_do.R;
import fr.onyxleroy.to_do.Tag;

public class AddTagDialog {
    private static final int[] COLORS = {
            Color.parseColor("#F44336"),
            Color.parseColor("#E91E63"),
            Color.parseColor("#9C27B0"),
            Color.parseColor("#673AB7"),
            Color.parseColor("#3F51B5"),
            Color.parseColor("#2196F3"),
            Color.parseColor("#4CAF50"),
            Color.parseColor("#FF9800"),
            Color.parseColor("#795548"),
            Color.parseColor("#607D8B")
    };

    private final Context context;
    private final OnTagSavedListener listener;
    private final Tag existingTag;
    private int selectedColor = COLORS[0];
    private Button selectedButton;

    public interface OnTagSavedListener {
        void onTagSaved(Tag tag);
    }

    public AddTagDialog(Context context, OnTagSavedListener listener) {
        this(context, listener, null);
    }

    public AddTagDialog(Context context, OnTagSavedListener listener, Tag existingTag) {
        this.context = context;
        this.listener = listener;
        this.existingTag = existingTag;
    }

    public void show() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_tag, null);

        EditText editTextTagName = dialogView.findViewById(R.id.editTextTagName);
        GridLayout colorGrid = dialogView.findViewById(R.id.colorGrid);

        if (existingTag != null) {
            editTextTagName.setText(existingTag.getName());
            selectedColor = existingTag.getColor();
        }

        setupColorGrid(colorGrid);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(existingTag == null ? R.string.add_tag : R.string.edit)
                .setView(dialogView)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String name = editTextTagName.getText().toString().trim();

                if (name.isEmpty()) {
                    Toast.makeText(context, R.string.please_enter_tag_name, Toast.LENGTH_SHORT).show();
                    return;
                }

                Tag tag;
                if (existingTag != null) {
                    tag = existingTag;
                    tag.setName(name);
                    tag.setColor(selectedColor);
                } else {
                    tag = new Tag(name, selectedColor);
                }

                if (listener != null) {
                    listener.onTagSaved(tag);
                }

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void setupColorGrid(GridLayout grid) {
        int cellSize = (int) (48 * context.getResources().getDisplayMetrics().density);
        int cellMargin = (int) (4 * context.getResources().getDisplayMetrics().density);

        grid.setColumnCount(5);
        grid.setRowCount(2);

        for (int i = 0; i < COLORS.length; i++) {
            Button colorButton = new Button(context);
            colorButton.setLayoutParams(new GridLayout.LayoutParams());
            colorButton.setBackgroundColor(Color.TRANSPARENT);
            colorButton.setPadding(0, 0, 0, 0);

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(8 * context.getResources().getDisplayMetrics().density);
            drawable.setColor(COLORS[i]);
            colorButton.setBackground(drawable);

            if (COLORS[i] == selectedColor) {
                drawable.setStroke(6, Color.BLACK);
                selectedButton = colorButton;
            }

            GridLayout.LayoutParams params = (GridLayout.LayoutParams) colorButton.getLayoutParams();
            params.width = 0;
            params.height = cellSize;
            params.columnSpec = GridLayout.spec(i % 5, 1f);
            params.rowSpec = GridLayout.spec(i / 5);
            params.setMargins(cellMargin, cellMargin, cellMargin, cellMargin);
            colorButton.setLayoutParams(params);

            final int color = COLORS[i];
            colorButton.setOnClickListener(v -> {
                if (selectedButton != null) {
                    GradientDrawable prevDrawable = (GradientDrawable) selectedButton.getBackground();
                    prevDrawable.setStroke(0, Color.TRANSPARENT);
                }

                GradientDrawable newDrawable = (GradientDrawable) colorButton.getBackground();
                newDrawable.setStroke(6, Color.BLACK);

                selectedButton = colorButton;
                selectedColor = color;
            });

            grid.addView(colorButton);
        }
    }
}
