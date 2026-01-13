package ua.august.todoapp.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.august.todoapp.dto.TaskDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.exceptions.AccessDeniedException;
import ua.august.todoapp.security.PersonDetails;
import ua.august.todoapp.services.interfaces.PersonDetailsService;
import ua.august.todoapp.services.interfaces.TaskService;


import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private PersonDetailsService personDetailsService;

    private PersonDetails testPersonDetails;

    @BeforeEach
    public void setup() {
        Person person = new Person();
        person.setId(2);
        person.setUsername("test");
        person.setPassword("password");
        testPersonDetails = new PersonDetails(person);
    }

    @Test
    void shouldReturn403WhenDeletingNotOwnedTask () throws Exception {

        // arrange
        doThrow(new AccessDeniedException(1))
                .when(taskService).delete(eq(1), eq(2));

        // act and assert
        mockMvc.perform(delete("/tasks/1")
                        .with(user(testPersonDetails))
                        .with(csrf()))
                .andExpect(status().isForbidden());

    }

    @Test
    void shouldDeleteTaskSuccessfully() throws Exception {
        // act
        mockMvc.perform(delete("/tasks/1")
                        .with(user(testPersonDetails))
                        .with(csrf()))
                // assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService).delete(eq(1), eq(2));
    }

    @Test
    void shouldReturn403WhenUpdatingNotOwnedTask () throws Exception {
        // arrange
        doThrow(new AccessDeniedException(1))
                .when(taskService).update(eq(1), any(), eq(2));

        // act and assert
        mockMvc.perform(patch("/tasks/1")
                        .param("title", "title")
                        .param("description", "description")
                        .param("priority", "HIGH")
                        .param("status", "DONE")

                        .with(user(testPersonDetails))
                        .with(csrf()))
                        .andExpect(status().isForbidden());
    }
    @Test
    void shouldUpdateTaskSuccessfully() throws Exception {

        // act
        mockMvc.perform(patch("/tasks/1")
                        .param("title", "Updated Title")
                        .param("description", "Updated Desc")
                        .param("priority", "LOW")
                        .param("status", "DONE")
                        .with(user(testPersonDetails))
                        .with(csrf()))
                // assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService).update(eq(1), any(), eq(2));
    }

    @Test
    void shouldCreateTaskAndRedirect() throws Exception {
        // arrange
        when(personDetailsService.findByUsername(testPersonDetails.getUsername()))
                .thenReturn(testPersonDetails.getPerson());

        // act
        mockMvc.perform(post("/tasks")
                        .param("title", "New Task")
                        .param("description", "Description")
                        .param("priority", "HIGH")
                        .param("status", "DONE")
                        .with(user(testPersonDetails))
                        .with(csrf()))
                // assert
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks"));

        verify(taskService).save(any(), eq(testPersonDetails.getPerson()));
    }

    @Test
    void shouldReturnTaskList() throws Exception {

        // arrange
        when(personDetailsService.findByUsername(testPersonDetails.getUsername()))
                .thenReturn(testPersonDetails.getPerson());

        when(taskService.findByOwnerId(testPersonDetails.getPerson().getId()))
                .thenReturn(List.of());

        // act
        mockMvc.perform(get("/tasks")
                        .with(user(testPersonDetails)))
                // assert
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/index"))
                .andExpect(model().attributeExists("tasks"));


        verify(taskService).findByOwnerId(testPersonDetails.getPerson().getId());
    }

    @Test
    void shouldReturnFormWithErrorsWhenCreatingInvalidTask() throws Exception {
        // act
        mockMvc.perform(post("/tasks")
                        .param("title", "")
                        .param("description", "Description")
                        .with(user(testPersonDetails))
                        .with(csrf()))
                // assert
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/new"))
                .andExpect(model().attributeHasFieldErrors("task", "title"));

        verify(taskService, never()).save(any(), any());
    }

    @Test
    void shouldShowTaskDetails() throws Exception {
        // arrange
        TaskDTO mockTask = new TaskDTO();
        mockTask.setId(1);
        mockTask.setTitle("test");

        when(personDetailsService.findByUsername(testPersonDetails.getUsername()))
                .thenReturn(testPersonDetails.getPerson());

        when(taskService.findById(eq(1), eq(testPersonDetails.getPerson().getId())))
                .thenReturn(mockTask);

        // act
        mockMvc.perform(get("/tasks/1")
                        .with(user(testPersonDetails)))
                // assert
                .andExpect(status().isOk())
                .andExpect(view().name("tasks/show"))
                .andExpect(model().attribute("task", mockTask));

        verify(taskService).findById(eq(1), eq(testPersonDetails.getPerson().getId()));
    }

}