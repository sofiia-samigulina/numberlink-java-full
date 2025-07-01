package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Users;

import java.util.List;

public interface UsersService {
    void addUser(Users user) throws UsersException;
    String getPassword(String player) throws UsersException;
    List<Users> getUsers() throws UsersException;
}
