package ro.ase.ie.dma08b.data;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ro.ase.ie.dma08b.R;
import ro.ase.ie.dma08b.model.Todo;
import ro.ase.ie.dma08b.network.ApiClient;
import ro.ase.ie.dma08b.network.TodoApi;

/**
 * The only class that knows where the data comes from (here: the network only).
 * <p>
 * The caller asks for todos and gets a result through {@link LoadCallback}; it never sees
 * Retrofit, HTTP codes or exceptions. This is the class to extend later:
 * <ul>
 *   <li>Room: read/write the database here, keep the same {@link LoadCallback} contract.</li>
 *   <li>ViewModel: the ViewModel will simply call {@link #loadTodos(LoadCallback)}.</li>
 * </ul>
 */
public class TodoRepository {

    /** Result of a load. Exactly one of the two methods is called, on the main thread. */
    public interface LoadCallback {
        void onSuccess(List<Todo> todos);

        /** @param messageRes string resource with a user-friendly text */
        void onError(int messageRes, Object... formatArgs);
    }

    private final TodoApi api = ApiClient.getTodoApi();

    /** @return the running call, so the caller can cancel it (e.g. in onDestroy) */
    public Call<List<Todo>> loadTodos(LoadCallback callback) {
        Call<List<Todo>> call = api.getTodos();

        // enqueue(): the request runs on OkHttp's threads, the callbacks come back on the main thread
        call.enqueue(new Callback<List<Todo>>() {
            @Override
            public void onResponse(@NonNull Call<List<Todo>> call,
                                   @NonNull Response<List<Todo>> response) {
                // onResponse is also called for 404 / 500: always check isSuccessful()
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(R.string.error_server, response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Todo>> call, @NonNull Throwable t) {
                if (call.isCanceled()) {
                    return;   // we cancelled it ourselves: nobody is waiting for the result
                }
                if (t instanceof SocketTimeoutException) {
                    callback.onError(R.string.error_timeout);
                } else if (t instanceof IOException) {
                    // no connection, DNS failure, connection lost
                    callback.onError(R.string.error_no_connection);
                } else {
                    // anything else: usually the JSON does not match the model
                    callback.onError(R.string.error_unexpected);
                }
            }
        });
        return call;
    }
}
