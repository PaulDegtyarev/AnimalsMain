package persistance.forUsers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import models.ForUsers.Users;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.*;

import static java.lang.System.out;

public class UsersPersistanceImpl implements UsersPersistence {
    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Users.class);

    @Override
    public Optional<Users> registration(JsonObject userInfo){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Users newUser = new Users();
        newUser.setFirstName(userInfo.getString("firstName"));
        newUser.setLastName(userInfo.getString("lastName"));
        newUser.setEmail(userInfo.getString("email"));
        newUser.setPassword(userInfo.getString("password"));
        newUser.setRole("USER");
        newUser.setUserToken(userInfo.getString("token"));

        session.persist("users", newUser);
        session.getTransaction().commit();
        session.close();

        return Optional.ofNullable(newUser);
    }

    @Override
    public Optional<JsonObject> getUsersById(Integer id) {
        Session session = configuration
                .buildSessionFactory()
                .openSession();
        JsonObject userJson = new JsonObject();
        session.beginTransaction();

        Query queryToGetUserById = session.createQuery("FROM Users WHERE id = :userId");
        queryToGetUserById.setParameter("userId", id);
        List<Users> userList = queryToGetUserById.list();
        if (!userList.isEmpty()) {
            Users userModel = userList.get(0);

            userJson.put("id", userModel.getId())
                    .put("firstName", userModel.getFirstName())
                    .put("lastName", userModel.getLastName())
                    .put("email", userModel.getEmail())
                    .put("role", userModel.getRole());

            session.getTransaction().commit();
            session.close();
        }

        return Optional.ofNullable(userJson);
    }

    @Override
    public Optional<JsonArray> getUsersByParameter(String firstName,
                                     String lastName,
                                     String email,
                                     Integer from,
                                     Integer size){
        Session session = configuration
                .buildSessionFactory()
                .openSession();

        List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Users> criteriaQuery = builder.createQuery(Users.class);
        Root<Users> root = criteriaQuery.from(Users.class);
        criteriaQuery.select(root);

        if (firstName != null && !firstName.isEmpty()){
            predicates.add(builder.like(builder.lower(root.get("firstname")), "%" + firstName.toLowerCase() + "%"));
        } else {

        }
        if (lastName != null){
            predicates.add(builder.like(builder.lower(root.get("lastname")), "%" + lastName.toLowerCase() + "%"));
        }
        if (email != null){
            predicates.add(builder.like(builder.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
        }

        if (!predicates.isEmpty()){
            criteriaQuery.where(builder.and(predicates.toArray(new Predicate[0])));
        }
        criteriaQuery.orderBy(builder.asc(root.get("id")));

        List<Users> searchedUsers = session.createQuery(criteriaQuery)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        JsonArray userArray = new JsonArray();

        for (Users searchedusers : searchedUsers){
            JsonObject userJson = new JsonObject();
            userJson.put("id", searchedusers.getId());
            userJson.put("firstName", searchedusers.getFirstName());
            userJson.put("lastName", searchedusers.getLastName());
            userJson.put("email", searchedusers.getEmail());
            userJson.put("role", searchedusers.getRole());
            userArray.add(userJson);
        }
        return Optional.ofNullable(userArray);
    }

    @Override
    public Optional<Users> addUser(JsonObject info){
        Session session = configuration
                .buildSessionFactory()
                .openSession();

        session.beginTransaction();
        Users userAdded = new Users();

        userAdded.setFirstName(info.getString("firstName"));
        userAdded.setLastName(info.getString("lastName"));

        userAdded.setEmail(info.getString("email"));
        userAdded.setPassword(info.getString("password"));
        userAdded.setRole(info.getString("role"));
        userAdded.setUserToken(info.getString("token"));

        session.persist("users", userAdded);
        session.getTransaction().commit();
        session.close();

        return Optional.ofNullable(userAdded);
    }

    @Override
    public Optional<Users> updateUserById(Integer userIdForUpdate, JsonObject newInfo){
        Session session = configuration
                .buildSessionFactory()
                .openSession();

        session.beginTransaction();

        Query queryToTakeUserForUpdate = session.createQuery("FROM Users WHERE id = :userId");
        queryToTakeUserForUpdate.setParameter("userId", userIdForUpdate);

        List<Users> listToTakeUserForUpdate = queryToTakeUserForUpdate.list();

        Users userForUpdate = listToTakeUserForUpdate.get(0);

        userForUpdate.setFirstName(newInfo.getString("firstName"));
        userForUpdate.setLastName(newInfo.getString("lastName"));
        userForUpdate.setEmail(newInfo.getString("email"));
        userForUpdate.setPassword(newInfo.getString("password"));
        userForUpdate.setRole(newInfo.getString("role"));


        session.update(userForUpdate);
        session.getTransaction().commit();
        session.close();

        return Optional.ofNullable(userForUpdate);
    }

    @Override
    public Optional<Users> deleteUserById(Integer idUserForDelete){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToTakeUserForDelete = session.createQuery("FROM Users Where id = :userId");
        queryToTakeUserForDelete.setParameter("userId", idUserForDelete);

        List<Users> listUserToDelete = queryToTakeUserForDelete.list();

        Users userForDelete = listUserToDelete.get(0);

        session.delete(userForDelete);
        session.getTransaction().commit();
        session.close();

        return Optional.ofNullable(userForDelete);
    }
}
