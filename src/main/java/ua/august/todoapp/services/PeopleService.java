package ua.august.todoapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.august.todoapp.entity.Person;
import ua.august.todoapp.entity.Task;
import ua.august.todoapp.repositories.PeopleRepository;

import java.util.List;

@Service
public class PeopleService {

    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleService(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }

    public Person findByName(String name){
        return peopleRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("Имя не найдено!"));
    }
}
