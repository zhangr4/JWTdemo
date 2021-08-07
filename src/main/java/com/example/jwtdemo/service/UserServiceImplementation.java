package com.example.jwtdemo.service;

import com.example.jwtdemo.domain.Role;
import com.example.jwtdemo.domain.User;
import com.example.jwtdemo.repository.RoleRepository;
import com.example.jwtdemo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImplementation implements UserService{

    private final UserRepository userRepo;

    private final RoleRepository roleRepo;


    @Override
    public User saveUser(User user) {
        log.info("Saving new user {} to the database",user.getUserName());
        return userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Save new role {} to the database", role.getName());
        return roleRepo.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("adding role and passing the role name {} to the user {}", roleName, username);
        User user = userRepo.findByUserName(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    @Override
    public User getUser(String username) {
        log.info("fetching user {}", username);
        return userRepo.findByUserName(username);
    }

    @Override
    public List<User> getUsers() {
        log.info("fetching all user");
        return userRepo.findAll();
    }
}
