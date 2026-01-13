package ua.august.todoapp.services.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.repositories.PeopleRepository;
import ua.august.todoapp.security.PersonDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PersonDetailsServiceImplTest {

    @Mock
    private PeopleRepository peopleRepository;

    @InjectMocks
    private PersonDetailsServiceImpl personDetailsService;

    @Test
    void shouldReturnUserDetailsWhenUserExists() {

        // arrange
        String username = "testUser";
        Person person = new Person();
        person.setUsername(username);
        person.setPassword("password");

        when(peopleRepository.findByUsername(username)).thenReturn(Optional.of(person));

        // act
        UserDetails userDetails = personDetailsService.loadUserByUsername(username);

        // assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertInstanceOf(PersonDetails.class, userDetails);

        verify(peopleRepository).findByUsername(username);
    }

    @Test
    void shouldThrowExceptionsWhenUserNotFound() {

        // arrange
        String username = "unknownUser";
        when(peopleRepository.findByUsername(username)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(UsernameNotFoundException.class, () -> {
            personDetailsService.loadUserByUsername(username);
        });

        verify(peopleRepository).findByUsername(username);
    }

    @Test
    void shouldReturnPersonWhenUserExists() {

        // arrange
        String username = "testUser";
        Person person = new Person();
        person.setUsername(username);

        when(peopleRepository.findByUsername(username)).thenReturn(Optional.of(person));

        // act
        Person result = personDetailsService.findByUsername(username);

        // assert
        assertNotNull(result);
        assertEquals(username, result.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        // arrange
        String username = "unknown";
        when(peopleRepository.findByUsername(username)).thenReturn(Optional.empty());

        // act and assert
        assertThrows(UsernameNotFoundException.class, () -> {
            personDetailsService.findByUsername(username);
        });
    }

    @Test
    void shouldReturnTrueWhenUserExists() {

        // arrange
        String username = "existing";
        when(peopleRepository.existsByUsername(username)).thenReturn(true);

        // act
        boolean exists = personDetailsService.existsByUsername(username);

        // assert
        assertTrue(exists);
        verify(peopleRepository).existsByUsername(username);
    }
}