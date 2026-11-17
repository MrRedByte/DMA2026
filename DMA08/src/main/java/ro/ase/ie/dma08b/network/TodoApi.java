package ro.ase.ie.dma08b.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ro.ase.ie.dma08b.model.Todo;

/** Describes the endpoints. Retrofit generates the implementation. */
public interface TodoApi {

    @GET("todos")
    Call<List<Todo>> getTodos();

    /** Not used by the screen: shows how a query parameter is added (?userId=1). */
    @GET("todos")
    Call<List<Todo>> getTodosByUser(@Query("userId") int userId);
}
