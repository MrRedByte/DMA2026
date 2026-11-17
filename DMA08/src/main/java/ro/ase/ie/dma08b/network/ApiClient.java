package ro.ase.ie.dma08b.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ro.ase.ie.dma08b.BuildConfig;

/** Builds Retrofit once for the whole app: creating it is expensive. */
public final class ApiClient {

    // The base URL must end with /
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/";

    private static TodoApi todoApi;

    private ApiClient() {
        // Only static methods
    }

    public static synchronized TodoApi getTodoApi() {
        if (todoApi == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(BuildConfig.DEBUG
                    ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);

            // Without timeouts a bad network can keep the user waiting for a long time.
            // A timeout ends in onFailure() with a SocketTimeoutException (an IOException).
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            todoApi = retrofit.create(TodoApi.class);
        }
        return todoApi;
    }
}
