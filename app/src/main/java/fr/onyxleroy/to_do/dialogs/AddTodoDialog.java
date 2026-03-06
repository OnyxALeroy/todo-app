package fr.onyxleroy.to_do.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.onyxleroy.to_do.R;
import fr.onyxleroy.to_do.Tag;
import fr.onyxleroy.to_do.Todo;
import fr.onyxleroy.to_do.utils.TagStorageManager;

public class AddTodoDialog {
    private final Context context;
    private final OnTodoSavedListener listener;
    private Todo existingTodo;
    private final Calendar selectedDateTime;
    private Todo.RepeatType selectedRepeatType = Todo.RepeatType.NONE;
    private List<Tag> availableTags;
    private List<Tag> selectedTags = new ArrayList<>();

    public interface OnTodoSavedListener {
        void onTodoSaved(Todo todo);
    }

    public AddTodoDialog(Context context, OnTodoSavedListener listener) {
        this.context = context;
        this.listener = listener;
        this.selectedDateTime = Calendar.getInstance();
        loadTags();
    }

    public AddTodoDialog(Context context, OnTodoSavedListener listener, Todo existingTodo) {
        this.context = context;
        this.listener = listener;
        this.existingTodo = existingTodo;
        this.selectedDateTime = Calendar.getInstance();
        if (existingTodo != null) {
            selectedDateTime.setTimeInMillis(existingTodo.getDateTimeMillis());
            this.selectedRepeatType = existingTodo.getRepeatType() != null 
                    ? existingTodo.getRepeatType() 
                    : Todo.RepeatType.NONE;
            this.selectedTags = new ArrayList<>(existingTodo.getTags());
        }
        loadTags();
    }

    private void loadTags() {
        availableTags = TagStorageManager.loadTags(context);
        if (availableTags == null) {
            availableTags = new ArrayList<>();
        }
    }

    public void show() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_todo, null);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        Button buttonSelectDateTime = dialogView.findViewById(R.id.buttonSelectDateTime);
        Spinner spinnerRepeat = dialogView.findViewById(R.id.spinnerRepeat);
        LinearLayout tagsContainer = dialogView.findViewById(R.id.tagsContainer);
        Button buttonAddTag = dialogView.findViewById(R.id.buttonAddTag);

        String[] repeatOptions = new String[]{
                context.getString(R.string.repeat_none),
                context.getString(R.string.repeat_hourly),
                context.getString(R.string.repeat_daily),
                context.getString(R.string.repeat_weekly),
                context.getString(R.string.repeat_monthly),
                context.getString(R.string.repeat_yearly)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                repeatOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRepeat.setAdapter(adapter);

        spinnerRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedRepeatType = Todo.RepeatType.fromValue(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedRepeatType = Todo.RepeatType.NONE;
            }
        });

        if (existingTodo != null) {
            editTextTitle.setText(existingTodo.getTitle());
            editTextDescription.setText(existingTodo.getDescription());
            spinnerRepeat.setSelection(existingTodo.getRepeatType() != null 
                    ? existingTodo.getRepeatType().getValue() 
                    : 0);
        }

        updateDateTimeButtonText(buttonSelectDateTime);

        buttonSelectDateTime.setOnClickListener(v -> showDateTimePicker(buttonSelectDateTime));

        updateTagsContainer(tagsContainer);

        buttonAddTag.setOnClickListener(v -> showTagSelectionDialog(tagsContainer));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(existingTodo == null ? R.string.add_todo : R.string.edit)
                .setView(dialogView)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                String title = editTextTitle.getText().toString().trim();
                String description = editTextDescription.getText().toString().trim();

                if (title.isEmpty()) {
                    Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedDateTime.getTimeInMillis() <= System.currentTimeMillis()) {
                    Toast.makeText(context, "Please select a future date and time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Todo todo;
                if (existingTodo != null) {
                    todo = existingTodo;
                    todo.setTitle(title);
                    todo.setDescription(description);
                    todo.setDateTimeMillis(selectedDateTime.getTimeInMillis());
                    todo.setRepeatType(selectedRepeatType);
                    todo.setTags(new ArrayList<>(selectedTags));
                } else {
                    todo = new Todo(title, description, selectedDateTime.getTimeInMillis(), selectedRepeatType);
                    todo.setTags(new ArrayList<>(selectedTags));
                }

                if (listener != null) {
                    listener.onTodoSaved(todo);
                }

                dialog.dismiss();
            });
        });

        dialog.show();
        
        editTextTitle.clearFocus();
        editTextDescription.clearFocus();
    }

    private void showDateTimePicker(Button button) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            context,
                            (timeView, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                selectedDateTime.set(Calendar.SECOND, 0);
                                updateDateTimeButtonText(button);
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void updateDateTimeButtonText(Button button) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        button.setText(sdf.format(selectedDateTime.getTime()));
    }

    private void updateTagsContainer(LinearLayout tagsContainer) {
        tagsContainer.removeAllViews();

        for (Tag tag : selectedTags) {
            View tagView = createTagView(tag, tagsContainer);
            tagsContainer.addView(tagView);
        }
    }

    private View createTagView(Tag tag, LinearLayout container) {
        int padding = (int) (8 * context.getResources().getDisplayMetrics().density);
        int textPadding = (int) (4 * context.getResources().getDisplayMetrics().density);

        Button tagButton = new Button(context);
        tagButton.setText(tag.getName());
        tagButton.setTextSize(12);
        tagButton.setPadding(textPadding, padding, textPadding, padding);

        GradientDrawable background = new GradientDrawable();
        background.setShape(GradientDrawable.RECTANGLE);
        background.setCornerRadius(16 * context.getResources().getDisplayMetrics().density);
        background.setColor(tag.getColor());
        tagButton.setBackground(background);
        tagButton.setTextColor(Color.WHITE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, (int) (4 * context.getResources().getDisplayMetrics().density), 0);
        tagButton.setLayoutParams(params);

        tagButton.setOnClickListener(v -> {
            selectedTags.remove(tag);
            updateTagsContainer(container);
        });

        return tagButton;
    }

    private void showTagSelectionDialog(LinearLayout tagsContainer) {
        loadTags();
        
        if (availableTags.isEmpty()) {
            showCreateTagDialog(tagsContainer);
            return;
        }

        String[] tagNames = new String[availableTags.size() + 1];
        tagNames[0] = context.getString(R.string.create_new_tag);
        for (int i = 0; i < availableTags.size(); i++) {
            tagNames[i + 1] = availableTags.get(i).getName();
        }

        new AlertDialog.Builder(context)
                .setTitle(R.string.manage_tags)
                .setItems(tagNames, (dialog, which) -> {
                    if (which == 0) {
                        showCreateTagDialog(tagsContainer);
                    } else {
                        Tag selectedTag = availableTags.get(which - 1);
                        if (!selectedTags.contains(selectedTag)) {
                            selectedTags.add(selectedTag);
                            updateTagsContainer(tagsContainer);
                        }
                    }
                })
                .show();
    }

    private void showCreateTagDialog(LinearLayout tagsContainer) {
        AddTagDialog addTagDialog = new AddTagDialog(context, tag -> {
            List<Tag> allTags = TagStorageManager.loadTags(context);
            if (allTags == null) allTags = new ArrayList<>();
            
            boolean exists = false;
            for (Tag t : allTags) {
                if (t.getName().equalsIgnoreCase(tag.getName())) {
                    exists = true;
                    break;
                }
            }
            
            if (!exists) {
                allTags.add(tag);
                TagStorageManager.saveTags(context, allTags);
            }
            
            if (!selectedTags.contains(tag)) {
                selectedTags.add(tag);
            }
            updateTagsContainer(tagsContainer);
        });
        addTagDialog.show();
    }
}
