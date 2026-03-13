package fr.onyxleroy.to_do.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import fr.onyxleroy.to_do.R;
import fr.onyxleroy.to_do.Tag;
import fr.onyxleroy.to_do.Todo;
import fr.onyxleroy.to_do.utils.FoldedTagsManager;

public class StatisticsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TODO = 1;
    private static final String UNTAGGED_HEADER = "___UNTAGGED___";

    private final List<Object> items = new ArrayList<>();
    private final TodoAdapter.OnTodoClickListener listener;
    private final Context context;
    private final Set<String> foldedTags = new HashSet<>();
    private List<Tag> tags;
    private List<Todo> todos;

    public StatisticsAdapter(Context context, List<Tag> tags, List<Todo> todos, TodoAdapter.OnTodoClickListener listener) {
        this.context = context;
        this.listener = listener;
        this.tags = tags;
        this.todos = todos;
        this.foldedTags.addAll(FoldedTagsManager.loadFoldedTags(context));
        buildItemsList();
    }

    public void updateData(List<Tag> tags, List<Todo> todos) {
        this.tags = tags;
        this.todos = todos;
        foldedTags.clear();
        foldedTags.addAll(FoldedTagsManager.loadFoldedTags(context));
        buildItemsList();
    }

    private void buildItemsList() {
        items.clear();

        List<Tag> sortedTags = new ArrayList<>();
        if (tags != null) {
            sortedTags = new ArrayList<>(tags);
            Collections.sort(sortedTags, (a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        }

        Map<String, List<Todo>> todosByTag = new HashMap<>();
        for (Tag tag : sortedTags) {
            todosByTag.put(tag.getId(), new ArrayList<>());
        }
        List<Todo> untaggedTodos = new ArrayList<>();

        for (Todo todo : todos) {
            if (todo.getTags() == null || todo.getTags().isEmpty()) {
                untaggedTodos.add(todo);
            } else {
                for (Tag tag : todo.getTags()) {
                    List<Todo> tagTodos = todosByTag.get(tag.getId());
                    if (tagTodos != null) {
                        tagTodos.add(todo);
                    }
                }
            }
        }

        boolean untaggedFolded = foldedTags.contains(UNTAGGED_HEADER);
        if (!untaggedFolded && !untaggedTodos.isEmpty()) {
            items.add(UNTAGGED_HEADER);
            items.addAll(untaggedTodos);
        } else if (!untaggedTodos.isEmpty()) {
            items.add(UNTAGGED_HEADER);
        }

        for (Tag tag : sortedTags) {
            List<Todo> tagTodos = todosByTag.get(tag.getId());
            if (tagTodos != null && !tagTodos.isEmpty()) {
                boolean isFolded = foldedTags.contains(tag.getId());
                items.add(tag);
                if (!isFolded) {
                    items.addAll(tagTodos);
                }
            }
        }
    }

    public void refreshFoldedState() {
        foldedTags.clear();
        foldedTags.addAll(FoldedTagsManager.loadFoldedTags(context));
    }

    @Override
    public int getItemViewType(int position) {
        Object item = items.get(position);
        if (item instanceof String && UNTAGGED_HEADER.equals(item)) {
            return TYPE_HEADER;
        }
        return item instanceof Tag ? TYPE_HEADER : TYPE_TODO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_statistics_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_todo, parent, false);
            return new TodoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(items.get(position), position);
        } else if (holder instanceof TodoViewHolder) {
            ((TodoViewHolder) holder).bind((Todo) items.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTagName;
        private View colorIndicator;
        private View headerContainer;
        private TextView textViewExpandIcon;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTagName = itemView.findViewById(R.id.textViewTagName);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            headerContainer = itemView.findViewById(R.id.headerContainer);
            textViewExpandIcon = itemView.findViewById(R.id.textViewExpandIcon);
        }

        public void bind(Object header, int position) {
            String tagId = null;
            boolean isFolded = false;

            if (header instanceof String && UNTAGGED_HEADER.equals(header)) {
                textViewTagName.setText(itemView.getContext().getString(R.string.untagged));
                colorIndicator.setVisibility(View.GONE);
                tagId = UNTAGGED_HEADER;
                isFolded = foldedTags.contains(UNTAGGED_HEADER);
            } else if (header instanceof Tag) {
                Tag tag = (Tag) header;
                textViewTagName.setText(tag.getName());
                colorIndicator.setVisibility(View.VISIBLE);
                GradientDrawable background = new GradientDrawable();
                background.setShape(GradientDrawable.RECTANGLE);
                background.setCornerRadius(8 * itemView.getResources().getDisplayMetrics().density);
                background.setColor(tag.getColor());
                colorIndicator.setBackground(background);
                tagId = tag.getId();
                isFolded = foldedTags.contains(tag.getId());
            }

            textViewExpandIcon.setText(isFolded ? "\u25B6" : "\u25BC");

            GradientDrawable bgDrawable = new GradientDrawable();
            bgDrawable.setShape(GradientDrawable.RECTANGLE);
            bgDrawable.setColor(0xFFE0E0E0);
            headerContainer.setBackground(bgDrawable);

            final String finalTagId = tagId;
            headerContainer.setOnClickListener(v -> {
                if (finalTagId != null) {
                    boolean currentlyFolded = foldedTags.contains(finalTagId);
                    FoldedTagsManager.setFolded(context, finalTagId, !currentlyFolded);
                    if (!currentlyFolded) {
                        foldedTags.add(finalTagId);
                    } else {
                        foldedTags.remove(finalTagId);
                    }
                    buildItemsList();
                    notifyDataSetChanged();
                }
            });
        }
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewDateTime;
        private TextView textViewRepeat;
        private LinearLayout tagsContainer;
        private Button buttonEdit;
        private Button buttonDelete;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewRepeat = itemView.findViewById(R.id.textViewRepeat);
            tagsContainer = itemView.findViewById(R.id.tagsContainer);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(Todo todo, int position) {
            textViewTitle.setText(todo.getTitle());

            if (todo.getDescription() != null && !todo.getDescription().isEmpty()) {
                textViewDescription.setText(todo.getDescription());
                textViewDescription.setVisibility(View.VISIBLE);
            } else {
                textViewDescription.setVisibility(View.GONE);
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateTimeStr = sdf.format(new Date(todo.getDateTimeMillis()));
            textViewDateTime.setText(dateTimeStr);

            Todo.RepeatType repeatType = todo.getRepeatType();
            if (repeatType != null && repeatType != Todo.RepeatType.NONE) {
                String repeatText = getRepeatText(repeatType);
                textViewRepeat.setText(repeatText);
                textViewRepeat.setVisibility(View.VISIBLE);
            } else {
                textViewRepeat.setVisibility(View.GONE);
            }

            List<Tag> tags = todo.getTags();
            if (tags != null && !tags.isEmpty()) {
                tagsContainer.setVisibility(View.VISIBLE);
                tagsContainer.removeAllViews();
                for (Tag tag : tags) {
                    View tagView = createTagView(tag);
                    tagsContainer.addView(tagView);
                }
            } else {
                tagsContainer.setVisibility(View.GONE);
            }

            buttonEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(todo, position);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(todo, position);
                }
            });
        }

        private String getRepeatText(Todo.RepeatType repeatType) {
            switch (repeatType) {
                case HOURLY:
                    return itemView.getContext().getString(R.string.repeat_hourly);
                case DAILY:
                    return itemView.getContext().getString(R.string.repeat_daily);
                case WEEKLY:
                    return itemView.getContext().getString(R.string.repeat_weekly);
                case MONTHLY:
                    return itemView.getContext().getString(R.string.repeat_monthly);
                case YEARLY:
                    return itemView.getContext().getString(R.string.repeat_yearly);
                default:
                    return "";
            }
        }

        private View createTagView(Tag tag) {
            int padding = (int) (4 * itemView.getResources().getDisplayMetrics().density);
            int textPadding = (int) (2 * itemView.getResources().getDisplayMetrics().density);

            TextView tagText = new TextView(itemView.getContext());
            tagText.setText(tag.getName());
            tagText.setTextSize(10);
            tagText.setPadding(textPadding * 2, padding, textPadding * 2, padding);

            GradientDrawable background = new GradientDrawable();
            background.setShape(GradientDrawable.RECTANGLE);
            background.setCornerRadius(12 * itemView.getResources().getDisplayMetrics().density);
            background.setColor(tag.getColor());
            tagText.setBackground(background);
            tagText.setTextColor(Color.WHITE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, (int) (4 * itemView.getResources().getDisplayMetrics().density), 0);
            tagText.setLayoutParams(params);

            return tagText;
        }
    }
}
