package com.rest.api.controller;

import com.rest.api.entity.User;
import com.rest.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(method=RequestMethod.GET, value="/users")
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @RequestMapping(method=RequestMethod.GET, value="/users/{id}")
    public Optional<User> getUser(@PathVariable("id") String id) {
        return userRepository.findById(id);
    }

    @RequestMapping(method=RequestMethod.POST, value="/users/add")
    public User addUser(@RequestBody User user) {
        userRepository.save(user);
        return user;
    }

    @RequestMapping(method=RequestMethod.PUT, value="/users/update")
    public void updateUser(@RequestBody User user) {
        userRepository.save(user);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteUser(@PathVariable("id") String id) {
        userRepository.deleteById(id);
    }

}
