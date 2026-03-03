package fr.onyxleroy.to_do;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import fr.onyxleroy.to_do.adapters.TodoAdapter;
import fr.onyxleroy.to_do.dialogs.AddTodoDialog;
import fr.onyxleroy.to_do.utils.NotificationHelper;
import fr.onyxleroy.to_do.utils.TodoStorageManager;

public class MainActivity extends AppCompatActivity implements TodoAdapter.OnTodoClickListener {

    private RecyclerView recyclerViewTodos;
    private TextView textViewEmpty;
    private List<Todo> todos;
    private TodoAdapter adapter;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerViewTodos = findViewById(R.id.recyclerViewTodos);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        FloatingActionButton fabAddTodo = findViewById(R.id.fabAddTodo);

        todos = new ArrayList<>();
        adapter = new TodoAdapter(todos, this);
        recyclerViewTodos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTodos.setAdapter(adapter);

        fabAddTodo.setOnClickListener(v -> showAddTodoDialog());

        requestNotificationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodos();
    }

    private void loadTodos() {
        todos = TodoStorageManager.loadTodos(this);
        if (todos == null) {
            todos = new ArrayList<>();
        }
        adapter.updateTodos(todos);
        updateEmptyState();
    }

    private void saveTodos() {
        TodoStorageManager.saveTodos(this, todos);
    }

    private void updateEmptyState() {
        if (todos.isEmpty()) {
            textViewEmpty.setVisibility(View.VISIBLE);
            recyclerViewTodos.setVisibility(View.GONE);
        } else {
            textViewEmpty.setVisibility(View.GONE);
            recyclerViewTodos.setVisibility(View.VISIBLE);
        }
    }

    private void showAddTodoDialog() {
        AddTodoDialog dialog = new AddTodoDialog(this, this::onTodoSaved);
        dialog.show();
    }

    private void onTodoSaved(Todo todo) {
        boolean found = false;
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(todo.getId())) {
                todos.set(i, todo);
                found = true;
                break;
            }
        }
        if (!found) {
            todos.add(todo);
        }
        adapter.updateTodos(todos);
        saveTodos();
        updateEmptyState();

        NotificationHelper.scheduleNotification(this, todo);
    }

    @Override
    public void onEditClick(Todo todo, int position) {
        AddTodoDialog dialog = new AddTodoDialog(this, this::onTodoSaved, todo);
        dialog.show();
    }

    @Override
    public void onDeleteClick(Todo todo, int position) {
        todos.remove(position);
        adapter.updateTodos(todos);
        saveTodos();
        updateEmptyState();

        NotificationHelper.cancelNotification(this, todo.getId());
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }
}
