package ro.ase.ie.dma08b.ui;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import ro.ase.ie.dma08b.R;
import ro.ase.ie.dma08b.databinding.ItemTodoBinding;
import ro.ase.ie.dma08b.model.Todo;

/**
 * ListAdapter = RecyclerView.Adapter + DiffUtil: when a new list is submitted,
 * only the rows that really changed are updated.
 */
public class TodoAdapter extends ListAdapter<Todo, TodoAdapter.TodoViewHolder> {

    private static final DiffUtil.ItemCallback<Todo> DIFF = new DiffUtil.ItemCallback<Todo>() {
        @Override
        public boolean areItemsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Todo oldItem, @NonNull Todo newItem) {
            return oldItem.getTitle().equals(newItem.getTitle())
                    && oldItem.isCompleted() == newItem.isCompleted()
                    && oldItem.getUserId() == newItem.getUserId();
        }
    };

    public TodoAdapter() {
        super(DIFF);
    }

    static class TodoViewHolder extends RecyclerView.ViewHolder {
        final ItemTodoBinding binding;

        TodoViewHolder(@NonNull ItemTodoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @NonNull
    @Override
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Runs only when a new row is needed
        ItemTodoBinding binding = ItemTodoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TodoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
        // Runs each time a row shows a todo (rows are recycled)
        Todo todo = getItem(position);
        holder.binding.tvTitle.setText(todo.getTitle());
        holder.binding.tvUser.setText(
                holder.itemView.getContext().getString(R.string.user_format, todo.getUserId()));
        holder.binding.cbCompleted.setChecked(todo.isCompleted());
    }
}
