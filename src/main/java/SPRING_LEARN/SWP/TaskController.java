package SPRING_LEARN.SWP;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") Long id) {
        log.info("Called getTaskById method with id = " + id);
        try {
            return ResponseEntity.status(HttpStatus.OK).body(taskService.getTaskById(id));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        log.info("Called findAllTasks method");
        return ResponseEntity.status(HttpStatus.OK).body(taskService.findAllTasks());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task taskToCreate) {
        log.info("called createTask method");
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskToCreate));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable("id") Long id, @RequestBody Task taskToUpdate) {
        log.info("called updateTask method with id = " + id + " taskToUpdate = " + taskToUpdate);
        var updatedTask = taskService.updateTask(id, taskToUpdate);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable("id") Long id) {
        log.info("called deleteTask method with id = " + id);
        try {
            taskService.deleteTask(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<Task> updateTaskStatusToInProgress(@PathVariable("id") Long id) {
        log.info("Called updateTaskStatus method with id = " + id);
        try {
            taskService.updateTaskStatusToInProgress(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
