package ro.ase.ie.dma08b.model;

import com.google.gson.annotations.SerializedName;

/**
 * Model (POJO) for one item of https://jsonplaceholder.typicode.com/todos
 *
 * <pre>
 * { "userId": 1, "id": 1, "title": "delectus aut autem", "completed": false }
 * </pre>
 *
 * Gson fills the fields by matching their names with the JSON keys.
 * @SerializedName is only needed when the names differ; it is used here to show the syntax.
 */
public class Todo {

    private int id;

    @SerializedName("userId")
    private int userId;

    private String title;
    private boolean completed;

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public boolean isCompleted() {
        return completed;
    }
}
