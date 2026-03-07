package fr.onyxleroy.to_do;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.onyxleroy.to_do.adapters.TagAdapter;
import fr.onyxleroy.to_do.adapters.TodoAdapter;
import fr.onyxleroy.to_do.dialogs.AddTagDialog;
import fr.onyxleroy.to_do.dialogs.AddTodoDialog;
import fr.onyxleroy.to_do.utils.NotificationHelper;
import fr.onyxleroy.to_do.utils.TagStorageManager;
import fr.onyxleroy.to_do.utils.TodoStorageManager;

public class MainActivity extends AppCompatActivity implements 
        TodoAdapter.OnTodoClickListener, 
        TagAdapter.OnTagClickListener,
        NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerViewTodos;
    private RecyclerView recyclerViewTags;
    private LinearLayout settingsContainer;
    private TextView textViewEmpty;
    private TextView textViewEmptyTags;
    private List<Todo> todos;
    private List<Tag> tags;
    private TodoAdapter todoAdapter;
    private TagAdapter tagAdapter;
    private int currentView = 0;
    private final int[] swipeStarted = {0};
    private final float[] startX = {0};

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        
        ImageButton buttonMenu = findViewById(R.id.buttonMenu);
        buttonMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerViewTodos = findViewById(R.id.recyclerViewTodos);
        recyclerViewTags = findViewById(R.id.recyclerViewTags);
        settingsContainer = findViewById(R.id.settingsContainer);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        textViewEmptyTags = findViewById(R.id.textViewEmptyTags);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);

        todos = new ArrayList<>();
        todoAdapter = new TodoAdapter(todos, this);
        recyclerViewTodos.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTodos.setAdapter(todoAdapter);

        tags = new ArrayList<>();
        tagAdapter = new TagAdapter(this);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTags.setAdapter(tagAdapter);

        fabAdd.setOnClickListener(v -> {
            if (currentView == 0) {
                showAddTodoDialog();
            } else {
                showAddTagDialog();
            }
        });

        requestNotificationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodos();
        loadTags();
        syncTagsWithTodos();
        todoAdapter.updateTodos(todos);
        updateView();
    }

    private void loadTodos() {
        todos = TodoStorageManager.loadTodos(this);
        if (todos == null) {
            todos = new ArrayList<>();
        }
        Collections.sort(todos, Comparator.comparingLong(Todo::getDateTimeMillis));
        updateEmptyState();
    }

    private void loadTags() {
        tags = TagStorageManager.loadTags(this);
        if (tags == null) {
            tags = new ArrayList<>();
        }
        Collections.sort(tags, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        tagAdapter.updateTags(tags);
        updateEmptyTagsState();
    }

    private void syncTagsWithTodos() {
        for (Todo todo : todos) {
            if (todo.getTags() != null) {
                for (int i = 0; i < todo.getTags().size(); i++) {
                    Tag todoTag = todo.getTags().get(i);
                    for (Tag masterTag : tags) {
                        if (masterTag.getId().equals(todoTag.getId())) {
                            todoTag.setName(masterTag.getName());
                            todoTag.setColor(masterTag.getColor());
                            break;
                        }
                    }
                }
            }
        }
    }

    private void saveTodos() {
        TodoStorageManager.saveTodos(this, todos);
    }

    private void saveTags() {
        TagStorageManager.saveTags(this, tags);
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

    private void updateEmptyTagsState() {
        if (tags.isEmpty()) {
            textViewEmptyTags.setVisibility(View.VISIBLE);
            recyclerViewTags.setVisibility(View.GONE);
        } else {
            textViewEmptyTags.setVisibility(View.GONE);
            recyclerViewTags.setVisibility(View.VISIBLE);
        }
    }

    private void updateView() {
        if (currentView == 0) {
            recyclerViewTodos.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(todos.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerViewTags.setVisibility(View.GONE);
            textViewEmptyTags.setVisibility(View.GONE);
            settingsContainer.setVisibility(View.GONE);
        } else if (currentView == 1) {
            recyclerViewTodos.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.GONE);
            recyclerViewTags.setVisibility(View.VISIBLE);
            textViewEmptyTags.setVisibility(tags.isEmpty() ? View.VISIBLE : View.GONE);
            settingsContainer.setVisibility(View.GONE);
        } else {
            recyclerViewTodos.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.GONE);
            recyclerViewTags.setVisibility(View.GONE);
            textViewEmptyTags.setVisibility(View.GONE);
            settingsContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onNavigationItemSelected(android.view.MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            currentView = 0;
        } else if (itemId == R.id.nav_tags) {
            currentView = 1;
            loadTags();
        } else if (itemId == R.id.nav_settings) {
            currentView = 2;
        }
        updateView();
        
        androidx.drawerlayout.widget.DrawerLayout drawer = findViewById(R.id.drawerLayout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAddTodoDialog() {
        AddTodoDialog dialog = new AddTodoDialog(this, this::onTodoSaved);
        dialog.show();
    }

    private void showAddTagDialog() {
        AddTagDialog dialog = new AddTagDialog(this, this::onTagSaved);
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
        Collections.sort(todos, Comparator.comparingLong(Todo::getDateTimeMillis));
        todoAdapter.updateTodos(todos);
        saveTodos();
        updateEmptyState();

        NotificationHelper.scheduleNotification(this, todo);
    }

    private void onTagSaved(Tag tag) {
        boolean found = false;
        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i).getId().equals(tag.getId())) {
                tags.set(i, tag);
                found = true;
                break;
            }
        }
        if (!found) {
            tags.add(tag);
        }
        Collections.sort(tags, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        tagAdapter.updateTags(tags);
        saveTags();
        syncTagsWithTodos();
        todoAdapter.updateTodos(todos);
        updateEmptyTagsState();
    }

    @Override
    public void onEditClick(Todo todo, int position) {
        AddTodoDialog dialog = new AddTodoDialog(this, this::onTodoSaved, todo);
        dialog.show();
    }

    @Override
    public void onDeleteClick(Todo todo, int position) {
        todos.remove(position);
        Collections.sort(todos, Comparator.comparingLong(Todo::getDateTimeMillis));
        todoAdapter.updateTodos(todos);
        saveTodos();
        updateEmptyState();

        NotificationHelper.cancelNotification(this, todo.getId());
    }

    @Override
    public void onEditClick(Tag tag, int position) {
        AddTagDialog dialog = new AddTagDialog(this, this::onTagSaved, tag);
        dialog.show();
    }

    @Override
    public void onDeleteClick(Tag tag, int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_tag)
                .setMessage(R.string.confirm_delete_tag)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    tags.remove(position);
                    tagAdapter.updateTags(tags);
                    saveTags();
                    updateEmptyTagsState();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            startX[0] = ev.getX();
            swipeStarted[0] = 0;
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float diffX = ev.getX() - startX[0];

            if (swipeStarted[0] == 0 && Math.abs(diffX) > 50) {
                swipeStarted[0] = 1;
            }

            if (swipeStarted[0] == 1) {
                float screenWidth = getResources().getDisplayMetrics().widthPixels;
                float swipeThreshold = screenWidth / 4.0f;

                if (diffX > swipeThreshold) {
                    if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                    swipeStarted[0] = 0;
                } else if (diffX < -swipeThreshold) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    swipeStarted[0] = 0;
                }
            }
        }

        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            swipeStarted[0] = 0;
        }

        return super.dispatchTouchEvent(ev);
    }
}
