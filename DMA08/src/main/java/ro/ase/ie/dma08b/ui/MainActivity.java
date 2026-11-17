package ro.ase.ie.dma08b.ui;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import retrofit2.Call;
import ro.ase.ie.dma08b.data.TodoRepository;
import ro.ase.ie.dma08b.databinding.ActivityMainBinding;
import ro.ase.ie.dma08b.model.Todo;

/**
 * Asks the repository for the todos and shows the result in a RecyclerView.
 * No ViewModel: the Activity keeps the running call itself.
 * (Rotation recreates the Activity, so the request is sent again.)
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private TodoAdapter adapter;
    private final TodoRepository repository = new TodoRepository();

    private Call<List<Todo>> runningCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // RecyclerView needs a LayoutManager and an adapter
        adapter = new TodoAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvTodos.setLayoutManager(layoutManager);
        binding.rvTodos.addItemDecoration(
                new DividerItemDecoration(this, layoutManager.getOrientation()));
        binding.rvTodos.setAdapter(adapter);

        binding.btnRetry.setOnClickListener(v -> loadTodos());
        loadTodos();
    }

    /** Also used by the Retry button. */
    private void loadTodos() {
        if (runningCall != null) {
            runningCall.cancel();   // never have two requests racing
        }
        showLoading();

        runningCall = repository.loadTodos(new TodoRepository.LoadCallback() {
            @Override
            public void onSuccess(List<Todo> todos) {
                showTodos(todos);
            }

            @Override
            public void onError(int messageRes, Object... formatArgs) {
                showError(getString(messageRes, formatArgs));
            }
        });
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.rvTodos.setVisibility(View.GONE);
        binding.errorGroup.setVisibility(View.GONE);
    }

    private void showTodos(List<Todo> todos) {
        binding.progressBar.setVisibility(View.GONE);
        binding.errorGroup.setVisibility(View.GONE);
        binding.rvTodos.setVisibility(View.VISIBLE);
        adapter.submitList(todos);
    }

    private void showError(String message) {
        binding.progressBar.setVisibility(View.GONE);
        binding.rvTodos.setVisibility(View.GONE);
        binding.errorGroup.setVisibility(View.VISIBLE);
        binding.tvError.setText(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The screen is gone: stop the request so its callback never touches a dead Activity
        if (runningCall != null) {
            runningCall.cancel();
        }
    }
}
