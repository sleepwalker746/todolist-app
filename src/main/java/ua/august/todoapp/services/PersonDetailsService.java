package ua.august.todoapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.repositories.PeopleRepository;
import ua.august.todoapp.security.PersonDetails;

import java.util.Optional;

@Service
public class PersonDetailsService implements UserDetailsService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PersonDetailsService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public Person findByName(String username){
        return peopleRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Имя не найдено!"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Optional<Person> person = peopleRepository.findByUsername(username);

       if (person.isEmpty()) {
           throw new UsernameNotFoundException("User not found!");
       }

       return new PersonDetails(person.get());

    }

    public boolean existsByUsername(String username) {
        return peopleRepository.existsByUsername(username);
    }

}
