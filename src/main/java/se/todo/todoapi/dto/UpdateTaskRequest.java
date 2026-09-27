package se.todo.todoapi.dto;

public class UpdateTaskRequest {

    private String title;
    private String description;
    private boolean completed;
    private int userId;
    private int categoryId;

    public UpdateTaskRequest() {

    }

    public UpdateTaskRequest(String title, String description, boolean completed, int userId, int categoryId) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.userId = userId;
        this.categoryId = categoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
