package se.todo.todoapi.dto;

public class CreateTaskRequest {

    private String title;
    private String description;
    private boolean completed;
    private int userId;
    private int categoryId;

    public CreateTaskRequest() {

    }

    public CreateTaskRequest(String title, String description, boolean completed, int userId, int categoryId) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getUserId() {
        return userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
