package fr.onyxleroy.to_do.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.onyxleroy.to_do.R;
import fr.onyxleroy.to_do.Tag;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder> {
    private List<Tag> tags = new ArrayList<>();
    private final OnTagClickListener listener;

    public interface OnTagClickListener {
        void onEditClick(Tag tag, int position);
        void onDeleteClick(Tag tag, int position);
    }

    public TagAdapter(OnTagClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, parent, false);
        return new TagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        Tag tag = tags.get(position);
        holder.bind(tag, position);
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }

    public void updateTags(List<Tag> newTags) {
        this.tags = newTags != null ? newTags : new ArrayList<>();
        notifyDataSetChanged();
    }

    class TagViewHolder extends RecyclerView.ViewHolder {
        private View colorIndicator;
        private TextView textViewTagName;
        private Button buttonEdit;
        private Button buttonDelete;

        public TagViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            textViewTagName = itemView.findViewById(R.id.textViewTagName);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }

        public void bind(Tag tag, int position) {
            textViewTagName.setText(tag.getName());

            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(4 * itemView.getResources().getDisplayMetrics().density);
            drawable.setColor(tag.getColor());
            colorIndicator.setBackground(drawable);

            buttonEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(tag, position);
                }
            });

            buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(tag, position);
                }
            });
        }
    }
}
