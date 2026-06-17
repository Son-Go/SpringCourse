package SPRING_LEARN.SWP;

import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import javax.naming.NoPermissionException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public  Task getTaskById(Long id) {
        TaskEntity taskEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found task by id = " + id));

        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Not found task with id " + id);
        }
        log.info("Successfully find task with id = " + id);
        return toDomainTask(taskEntity);
    }

    public List<Task> findAllTasks() {
        List<TaskEntity> allEntities = repository.findAll();

        log.info("Successfully find all tasks");
        return allEntities.stream().map(this::toDomainTask).toList();
    }

    public Task createTask(Task taskToCreate) {
        if (taskToCreate.id() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (taskToCreate.status() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        var entityToSave = new TaskEntity(
                null,
                taskToCreate.creatorId(),
                taskToCreate.assignedUserId(),
                Status.CREATED,
                taskToCreate.createDateTime(),
                taskToCreate.deadlineDate(),
                taskToCreate.priority()
        );
        var savedEntity = repository.save(entityToSave);
        log.info("Successfully created task");
        return toDomainTask(savedEntity);
    }

    public Task updateTask(Long id, Task taskToUpdate) {
        var taskEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found task by id = " + id));
        var taskToSave = new TaskEntity(
                taskEntity.getId(),
                taskToUpdate.creatorId(),
                taskToUpdate.assignedUserId(),
                Status.IN_PROGRESS,
                taskToUpdate.createDateTime(),
                taskToUpdate.deadlineDate(),
                taskToUpdate.priority()
        );
        var updatedTask = repository.save(taskToSave);
        log.info("Successfully updated task with id = " + id);
        return toDomainTask(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("not found task by id = " + id);
        }
        repository.deleteById(id);
        log.info("Successfully deleted task with id = " + id);
    }

    private Task toDomainTask(TaskEntity task) {
        return  new Task(
                task.getId(),
                task.getCreatorId(),
                task.getAssignedUserId(),
                task.getStatus(),
                task.getCreateDateTime(),
                task.getDeadlineDate(),
                task.getPriority()
        );
    }

    public void updateTaskStatusToInProgress(Long id) {
        var taskEntity = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Not found task with id = " + id));

        if (taskEntity.getAssignedUserId() == null) {
            throw new IllegalArgumentException("Cant start task which is not assigned to user");
        }

        long activeTasksCount = repository.countByAssignedUserIdAndStatus(taskEntity.getAssignedUserId(), Status.IN_PROGRESS);
        if (activeTasksCount >= 4) {
            throw new IllegalStateException("assigned user " + taskEntity.getAssignedUserId() + " has approached the limit of assigned tasks");
        }

        taskEntity.setStatus(Status.IN_PROGRESS);
        repository.save(taskEntity);

        log.info("Task status successfully changed to IN_PROGRESS");
    }
}
