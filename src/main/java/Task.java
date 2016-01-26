import java.util.List;
import org.sql2o.*;

public class Task {
  private int id;
  private String description;
  private boolean completion;

  public int getId() {
    return id;
  }

  public String getDescription() {
    return description;
  }

  public boolean isCompleted() {
    return completion;
  }

  public Task(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object otherTask){
    if (!(otherTask instanceof Task)) {
      return false;
    } else {
      Task newTask = (Task) otherTask;
      return this.getDescription().equals(newTask.getDescription()) &&
             this.getId() == newTask.getId();
    }
  }

  public static List<Task> all() {
    String sql = "SELECT id, description FROM tasks";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql).executeAndFetch(Task.class);
    }
  }

  public static List<Task> all(boolean completed) {
    String sql = "SELECT id, description FROM tasks WHERE is_completed = :completed";
    try(Connection con = DB.sql2o.open()) {
      return con.createQuery(sql)
        .addParameter("completed", completed)
        .executeAndFetch(Task.class);
    }
  }

  public static Task find(int id) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT id, description FROM tasks where id=:id";
      Task task = con.createQuery(sql)
        .addParameter("id", id)
        .executeAndFetchFirst(Task.class);
      return task;
    }
  }

  public void save() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO tasks (description) VALUES (:description)";
      this.id = (int) con.createQuery(sql, true)
        .addParameter("description", this.description)
        .executeUpdate()
        .getKey();
    }
  }

  public void update(String description) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET description = :description WHERE id = :id";
      con.createQuery(sql)
        .addParameter("description", description)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void delete() {
    try(Connection con = DB.sql2o.open()) {
    String sql = "DELETE FROM tasks WHERE id = :id;";
      con.createQuery(sql)
        .addParameter("id", id)
        .executeUpdate();
    }
  }

  public void addCategory(Category category) {
    try(Connection con = DB.sql2o.open()) {
      String sql = "INSERT INTO categories_tasks (category_id, task_id) VALUES (:category_id, :task_id)";
      con.createQuery(sql)
      .addParameter("category_id", category.getId())
      .addParameter("task_id", this.getId())
      .executeUpdate();
    }
  }

  public List<Category> getCategories() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "SELECT categories.id, categories.name FROM tasks INNER JOIN categories_tasks AS c_t ON tasks.id = c_t.task_id INNER JOIN categories ON categories.id = c_t.category_id WHERE c_t.task_id = :task_id";
      return con.createQuery(sql)
        .addParameter("task_id", this.getId())
        .executeAndFetch(Category.class);
    }
  }

  public void complete() {
    try(Connection con = DB.sql2o.open()) {
      String sql = "UPDATE tasks SET is_completed = true WHERE id = :id";
      con.createQuery(sql)
      .addParameter("id", this.getId())
      .executeUpdate();
      completion = true;
    }
  }
}
