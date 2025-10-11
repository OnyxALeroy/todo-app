package fr.onyxleroy.to_do.dialogs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fr.onyxleroy.to_do.R;
import fr.onyxleroy.to_do.Todo;

public class AddTodoDialog {
    private final Context context;
    private final OnTodoSavedListener listener;
    private Todo existingTodo;
    private final Calendar selectedDateTime;

    public interface OnTodoSavedListener {
        void onTodoSaved(Todo todo);
    }

    public AddTodoDialog(Context context, OnTodoSavedListener listener) {
        this.context = context;
        this.listener = listener;
        this.selectedDateTime = Calendar.getInstance();
    }

    public AddTodoDialog(Context context, OnTodoSavedListener listener, Todo existingTodo) {
        this.context = context;
        this.listener = listener;
        this.existingTodo = existingTodo;
        this.selectedDateTime = Calendar.getInstance();
        if (existingTodo != null) {
            selectedDateTime.setTimeInMillis(existingTodo.getDateTimeMillis());
        }
    }

    public void show() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_todo, null);

        EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        Button buttonSelectDateTime = dialogView.findViewById(R.id.buttonSelectDateTime);

        // Pre-fill if editing
        if (existingTodo != null) {
            editTextTitle.setText(existingTodo.getTitle());
            editTextDescription.setText(existingTodo.getDescription());
        }

        updateDateTimeButtonText(buttonSelectDateTime);

        buttonSelectDateTime.setOnClickListener(v -> showDateTimePicker(buttonSelectDateTime));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(existingTodo == null ? "Add Todo" : "Edit Todo")
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
                    // Edit existing todo
                    todo = existingTodo;
                    todo.setTitle(title);
                    todo.setDescription(description);
                    todo.setDateTimeMillis(selectedDateTime.getTimeInMillis());
                } else {
                    // Create new todo
                    todo = new Todo(title, description, selectedDateTime.getTimeInMillis());
                }

                if (listener != null) {
                    listener.onTodoSaved(todo);
                }

                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void showDateTimePicker(Button button) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                context,
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    // After date is selected, show time picker
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
}