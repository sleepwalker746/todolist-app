package ua.august.todoapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.services.interfaces.PersonDetailsService;
import ua.august.todoapp.services.interfaces.RegistrationService;
import ua.august.todoapp.util.PersonValidator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(PersonValidator.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrationService registrationService;

    @MockitoBean
    private PersonDetailsService personDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {

    }

    @Test
    @WithMockUser
    void shouldReturnRegistrationViewWithErrorsWhenUsernameIsEmpty() throws Exception {
        mockMvc.perform(post("http://localhost:8080/auth/registration")
                .param("username", "")
                .param("password", "password")
                .param("confirmPassword", "password")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration"))
                .andExpect(model().attributeHasFieldErrors("person", "username"));

        verify(registrationService, never()).register(any());
    }

    @Test
    @WithMockUser
    void shouldReturnRegistrationViewWithErrorsWhenPasswordIsEmpty() throws Exception {
        mockMvc.perform(post("http://localhost:8080/auth/registration")
                        .param("username", "username")
                        .param("password", "")
                        .param("confirmPassword", "password")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration"))
                .andExpect(model().attributeHasFieldErrors("person", "password"));

        verify(registrationService, never()).register(any());
    }

    @Test
    @WithMockUser
    void shouldReturnRegistrationViewWithErrorsWhenPasswordIsLessThan3Symbols() throws Exception {
        mockMvc.perform(post("http://localhost:8080/auth/registration")
                        .param("username", "username")
                        .param("password", "123")
                        .param("confirmPassword", "123456")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration"))
                .andExpect(model().attributeHasFieldErrors("person", "password"));

        verify(registrationService, never()).register(any());
    }

    @Test
    @WithMockUser
    void shouldReturnRegistrationViewWithErrorsWhenConfirmedPasswordIsLessThan3Symbols() throws Exception {
        mockMvc.perform(post("http://localhost:8080/auth/registration")
                        .param("username", "username")
                        .param("password", "123456")
                        .param("confirmPassword", "123")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration"))
                .andExpect(model().attributeHasFieldErrors("person", "confirmPassword"));

        verify(registrationService, never()).register(any());
    }

    @Test
    @WithMockUser
    void shouldReturnRegistrationViewWithErrorsWhenPasswordsDoNotMatch() throws Exception {
        mockMvc.perform(post("http://localhost:8080/auth/registration")
                        .param("username", "username")
                        .param("password", "1235678")
                        .param("confirmPassword", "123456")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration"))
                .andExpect(model().attributeHasFieldErrors("person", "confirmPassword"));

        verify(registrationService, never()).register(any());
    }

    @Test
    @WithMockUser
    void shouldReturnErrorWhenUsernameIsTaken() throws Exception {

        String existedUsername = "username";

        when(personDetailsService.existsByUsername(existedUsername)).thenReturn(true);

        mockMvc.perform(post("http://localhost:8080/auth/registration")
                .param("username", existedUsername)
                .param("password", "123456")
                .param("confirmPassword", "123456")
                .with(csrf()))

                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration"))
                .andExpect(model().attributeHasFieldErrors("person", "username"));

        verify(registrationService, never()).register(any());
    }



    @Test
    @WithMockUser
    void shouldShowRegistrationForm() throws Exception {
        mockMvc.perform(get("http://localhost:8080/auth/registration"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/registration"))
                .andExpect(model().attributeExists("person"));
    }

    @Test
    @WithMockUser
    void shouldRegisterAndRedirectWhenDataIsValid() throws Exception {
        PersonDTO validPerson = new PersonDTO();
        validPerson.setUsername("username");
        validPerson.setPassword("password");
        validPerson.setConfirmPassword("password");

        mockMvc.perform(post("/auth/registration")
                .param("username", validPerson.getUsername())
                .param("password", validPerson.getPassword())
                .param("confirmPassword", validPerson.getConfirmPassword())
                .with(csrf()))

                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login"))
                .andExpect(model().hasNoErrors());

        verify(registrationService).register(any(PersonDTO.class));
    }


}