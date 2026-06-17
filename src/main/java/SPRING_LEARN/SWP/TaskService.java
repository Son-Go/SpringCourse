package SPRING_LEARN.SWP;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import javax.naming.NoPermissionException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private final HashMap<Long, Task> taskMap;
    private final AtomicLong idCounter;

    public TaskService() {
        taskMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public  Task getTaskById(Long id) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException("Not found task with id " + id);
        }
        return new Task(id, 100L, 1L, Status.CREATED , LocalDateTime.now(), LocalDate.now().plusDays(1), Priority.HIGH);
    }

    public List<Task> findAllTasks() {
        return taskMap.values().stream().toList();
    }

    public Task createTask(Task taskToCreate) {
        if (taskToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (taskToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        var newTask = new Task(
                idCounter.incrementAndGet(),
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                Status.CREATED,
                taskToCreate.createDateTime(),
                taskToCreate.deadlineDate(),
                taskToCreate.priority()
        );
        taskMap.put(newTask.id(), newTask);
        return newTask;
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException("not found task by id = " + id);
        }
        var task = taskMap.get(id);
        var updatedTask = new Task(
                task.id(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                Status.IN_PROGRESS,
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );
        taskMap.put(task.id(), updatedTask);
        return updatedTask;
    }

    public void deleteTask(Long id) {
        if (!taskMap.containsKey(id)) {
            throw new NoSuchElementException("not found task by id = " + id);
        }
        taskMap.remove(id);
    }
}
