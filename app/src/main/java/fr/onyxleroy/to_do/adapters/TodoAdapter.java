package fr.onyxleroy.to_do.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.onyxleroy.to_do.R;
import fr.onyxleroy.to_do.Todo;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {
    private List<Todo> todos;
    private final OnTodoClickListener listener;

    public interface OnTodoClickListener {
        void onEditClick(Todo todo, int position);
        void onDeleteClick(Todo todo, int position);
    }

    public TodoAdapter(List<Todo> todos, OnTodoClickListener listener) {
        this.todos = todos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_todo, parent, false);
        return new TodoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        Todo todo = todos.get(position);
        holder.bind(todo, position);
    }

    @Override
    public int getItemCount() {
        return todos.size();
    }

    public void updateTodos(List<Todo> newTodos) {
        this.todos = newTodos;
        notifyDataSetChanged();
    }

    class TodoViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewDescription;
        private TextView textViewDateTime;
        private TextView textViewRepeat;
        private Button buttonEdit;
        private Button buttonDelete;

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            textViewDateTime = itemView.findViewById(R.id.textViewDateTime);
            textViewRepeat = itemView.findViewById(R.id.textViewRepeat);
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
    }
}