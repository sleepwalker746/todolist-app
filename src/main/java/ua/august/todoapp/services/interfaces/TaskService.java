package ua.august.todoapp.services.interfaces;

import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Person;


import java.util.List;

public interface TaskService {
     TaskDTO findById(int id, Integer ownerId);
     List<TaskDTO> findByOwnerId(Integer id);
     void save(TaskDTO task, Person owner);
     void update(int id, TaskDTO updatedTask, int ownerId);
     void delete(int id, int ownerId);
}
