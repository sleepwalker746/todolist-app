package ua.august.todoapp.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.august.todoapp.dto.PersonDTO;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.mapper.PersonMapper;
import ua.august.todoapp.repositories.PeopleRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceImplTest {

    @Mock
    private PeopleRepository peopleRepository;
    @Mock
    private PersonMapper personMapper;
    @Mock
    private PasswordEncoder passwordEncoder;



    @InjectMocks

    private RegistrationServiceImpl registrationServiceImpl;

    private Person person;
    private PersonDTO personDTO;

    @BeforeEach
    void setUp() {
        person = new Person();
        personDTO = new PersonDTO();
        person.setUsername("username");
        person.setPassword("password");
        personDTO.setUsername("username");
        personDTO.setPassword("password");
        personDTO.setConfirmPassword("password");
    }

    @Test
    void shouldRegisterSuccessfully() {
        // arrange
        when(personMapper.toPerson(personDTO)).thenReturn(person);
        String encodedPassword = "encodedPassword";
        when(passwordEncoder.encode(person.getPassword())).thenReturn(encodedPassword);

        // act
        registrationServiceImpl.register(personDTO);

        // assert
        verify(peopleRepository).save(person);
        verify(passwordEncoder).encode(personDTO.getPassword());
        assertEquals(encodedPassword, person.getPassword(), "Password should be encoded");
        assertEquals(personDTO.getPassword(), personDTO.getConfirmPassword(), "Password should be matched");

    }

    @Test
    void shouldThrowExceptionWhenDtoIsNull() {
        assertThrows(NullPointerException.class, () -> registrationServiceImpl.register(null));

        verifyNoInteractions(peopleRepository);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void shouldThrowExceptionWhenDatabaseFails() {
        // arrange
        when(personMapper.toPerson(personDTO)).thenReturn(person);
        when(passwordEncoder.encode(any())).thenReturn("encoded");

        doThrow(new RuntimeException("Database down")).when(peopleRepository).save(person);

        // act & assert
        Exception exception = assertThrows(RuntimeException.class, () ->
            registrationServiceImpl.register(personDTO));

        assertEquals("Database down", exception.getMessage());
        verify(peopleRepository).save(person);
    }
}