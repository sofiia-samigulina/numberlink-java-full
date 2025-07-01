package sk.tuke.gamestudio.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import sk.tuke.gamestudio.entity.Users;

import java.util.List;

@Transactional
public class UsersServiceJPA implements UsersService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addUser(Users user) throws UsersException {
        entityManager.persist(user);
    }

    @Override
    public String getPassword(String player) throws UsersException {
        Object object = entityManager.createNamedQuery("Users.getPassword").setParameter("player",player).getSingleResult();
        return ((Users)object).getPassword();
    }

    @Override
    public List<Users> getUsers() throws UsersException {
        return entityManager.createNamedQuery("Users.findAll").getResultList();
    }


}
