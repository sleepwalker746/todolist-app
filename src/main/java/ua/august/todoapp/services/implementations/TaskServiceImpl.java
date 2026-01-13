package ua.august.todoapp.services.implementations;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Task;
import ua.august.todoapp.exceptions.AccessDeniedException;
import ua.august.todoapp.exceptions.TaskNotFoundException;
import ua.august.todoapp.mapper.TaskMapper;
import ua.august.todoapp.repositories.TaskRepository;
import ua.august.todoapp.services.interfaces.TaskService;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService  {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public TaskDTO findById(int id, Integer ownerId) {
        log.debug("Attempting to find task with id: {}", id);
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException(id));
        if (task.getOwner() == null || !task.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException(id);
        }
        return taskMapper.toTaskDTO(task);
    }

    @Override
    public List<TaskDTO> findByOwnerId(Integer ownerId) {
        log.debug("Attempting to find all tasks by owner with id: {}", ownerId);
        List<Task> tasks = taskRepository.findByOwnerId(ownerId);
        return tasks.stream()
                .map(taskMapper::toTaskDTO)
                .toList();
    }

    @Override
    @Transactional
    public void save(TaskDTO taskDTO, Person person) {
        log.info("Saving task with id {}", taskDTO.getId());
        Task task = taskMapper.toTask(taskDTO);
        task.setOwner(person);
        taskRepository.save(task);
        log.info("Task with id {} saved successfully", task.getId());
    }

    @Override
    @Transactional
    public void update(int id, TaskDTO updatedTaskDTO, int ownerId) {
        log.info("Updating task with id {}", id);
        Task task = taskRepository.findById(id)
                        .orElseThrow(() -> new TaskNotFoundException(id));
        if (task.getOwner() == null || !task.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException(id);
        }
        taskMapper.updateTaskFromDTO(updatedTaskDTO, task);
        taskRepository.save(task);
        log.info("Task with id {} updated successfully", id);
    }

    @Override
    @Transactional
    public void delete(int id, int ownerId) {
        log.info("Deleting task with id {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        if (task.getOwner() == null || !task.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException(id);
        }
        taskRepository.deleteById(id);
        log.info("Task with id {} deleted successfully", id);
    }
}
