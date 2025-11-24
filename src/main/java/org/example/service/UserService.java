package org.example.service;

import org.example.exception.ClientNotFoundException;
import org.example.exception.UserNotFoundException;
import org.example.model.Client;
import org.example.model.User;
import org.example.repository.ClientRepository;
import org.example.repository.UserRepository;

import java.util.List;

public class UserService {

    private final UserRepository repository;

    public UserService() {
        this.repository = new UserRepository();
    }

    public List<User> getUsers(){
        return repository.getRepository();
    }


    public User getUserByEmail(String email) {
        if(isUserFound(email)){
            return repository.findUserByEmail(email);
        }
        throw new UserNotFoundException("Usuário com Email: " + email + " não encontrado");
    }

    public boolean isUserFound(String email) {
        User user = repository.findUserByEmail(email);
        if (user != null) {
            return true;
        }
        throw new UserNotFoundException("Usuário com Email: " + email + " não encontrado");
    }
}
