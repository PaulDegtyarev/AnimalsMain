package services.forUsers;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import models.ForAnimal.Animal;

import models.ForLocation.Location;
import models.ForTypes.Types;
import models.ForUsers.Users;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import persistance.forUsers.UsersPersistence;

import java.util.List;
import java.util.Optional;

public class UsersManagerServiceImpl implements UsersManagerService {

    private final UsersPersistence persistence;

    public UsersManagerServiceImpl(UsersPersistence persistence){
        this.persistence = persistence;
    }

    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Users.class)
            .addAnnotatedClass(Animal.class)
            .addAnnotatedClass(Location.class);

    @Override
    public boolean linkedWithAnimal(String userIdForDeleteParameter){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToCheckLinkWithAnimal = session.createQuery("FROM Users u JOIN Animal a ON u.id = a.chipperid.id WHERE u.id = :userId");
        queryToCheckLinkWithAnimal.setParameter("userId", userIdForDeleteParameter);

        List<Animal> listToCheckLinkWithAnimal = queryToCheckLinkWithAnimal.list();

        session.getTransaction().commit();
        session.close();

        return !listToCheckLinkWithAnimal.isEmpty();
    }

    @Override
    public boolean emailIsBusy(String newEmail){
        Session session = configuration
                .buildSessionFactory()
                .openSession();
        session.beginTransaction();

        Query queryToCheckEmailBusyOrNot = session.createQuery("FROM Users WHERE email = :newEmail");
        queryToCheckEmailBusyOrNot.setParameter("newEmail", newEmail);
        List<Users> listToCheckEmailBusyOrNot = queryToCheckEmailBusyOrNot.list();
        return listToCheckEmailBusyOrNot.isEmpty();
    }

    @Override
    public String checkRole(String userToken){
        Session session = configuration
                .buildSessionFactory()
                .openSession();
        session.beginTransaction();

        Query queryToCheckRole = session.createQuery("FROM Users where usertoken = :userToken");
        queryToCheckRole.setParameter("userToken", userToken);

        List<Users> listToCheckAuthorize = queryToCheckRole.list();

        Users user = listToCheckAuthorize.get(0);

        String storedRole = user.getRole();
        session.getTransaction().commit();
        session.close();

        return storedRole;
    }

    @Override
    public boolean emptyOrNot (Integer idForSearch){
        Session session = configuration
                .buildSessionFactory()
                .openSession();
        session.beginTransaction();

        Query queryToCheckUser = session.createQuery("FROM Users WHERE id = :userId");
        queryToCheckUser.setParameter("userId", idForSearch);

        List<Users> listToCheckUser = queryToCheckUser.list();

        session.getTransaction().commit();
        session.close();

        return !listToCheckUser.isEmpty();
    }

    @Override
    public boolean stranger(String userToken, Integer idForSearch) {
        Session session = configuration
                .buildSessionFactory()
                .openSession();
        session.beginTransaction();

        Query queryToCheckStranger = session.createQuery("FROM Users WHERE usertoken = :userToken");
        queryToCheckStranger.setParameter("userToken", userToken);

        List<Users> listToCheckStranger = queryToCheckStranger.list();
        Users maybeStrangerUser = listToCheckStranger.get(0);
        Integer storedId = maybeStrangerUser.getId();

        session.getTransaction().commit();
        session.close();

        return storedId.equals(idForSearch);
    }

    @Override
    public boolean checkAccountId(String accountParam){
        if (accountParam == null || accountParam.isBlank()) return false;
        Integer accountId = Integer.parseInt(accountParam);
        if(accountId <= 0) return  false;
        return true;
    }

    @Override
    public boolean checkUserToAuthorize(String userToken){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToCheckAuthorize = session.createQuery("FROM Users where usertoken = :userToken");
        queryToCheckAuthorize.setParameter("userToken", userToken);

        List<Users> listToCheckAuthorize = queryToCheckAuthorize.list();
        session.getTransaction().commit();
        session.close();

        return !listToCheckAuthorize.isEmpty();
    }


    @Override
    public void registration(JsonObject userInfo,
                             ServiceRequest request,
                             Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<Users> u = persistence.registration(userInfo);
        if (u.isPresent()){
            JsonObject userInfoJson = JsonObject.mapFrom(u.get());
            userInfoJson.remove("userToken");
            userInfoJson.remove("password");
            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(userInfoJson)));
        }

    }

    @Override
    public void getUserById(
            Integer id,
            ServiceRequest request,
            Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<JsonObject> u = persistence.getUsersById(id);

        if (u.isPresent()){

            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(u.get())));
        }
    }

    @Override
    public void getUserByParam(
            String firstName,
            String lastName,
            String email,
            Integer from,
            Integer size,
            ServiceRequest request,
            Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<JsonArray> u = persistence.getUsersByParameter(firstName, lastName, email, from, size);
        if (u.isPresent()){

            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(u.get())));
        } else {
            resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(404).setStatusMessage("Not found")));
        }
    }

    @Override
    public void addUser(JsonObject info,
                        ServiceRequest request,
                        Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<Users> userAdded = persistence.addUser(info);

        if (userAdded.isPresent()){
            JsonObject userJson = JsonObject.mapFrom(userAdded.get());
            userJson.remove("userToken");
            userJson.remove("password");

            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(userJson)));
        } else {
            resultHandler.handle(Future.succeededFuture(new ServiceResponse().setStatusCode(404).setStatusMessage("Not found")));
        }
    }

    @Override
    public void updateUserById(
            Integer userIdForUpdate,
            JsonObject newInfo,
            ServiceRequest request,
            Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<Users> u = persistence.updateUserById(userIdForUpdate, newInfo);

        if (u.isPresent()){
            JsonObject updateUserJson = JsonObject.mapFrom(u.get());
            updateUserJson.remove("password");
            updateUserJson.remove("userToken");

            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(updateUserJson)));
        }
    }

    @Override
    public void deleteUserById(Integer userIdForDelete,
                               ServiceRequest request,
                               Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<Users> u = persistence.deleteUserById(userIdForDelete);
        if (u.isPresent()){
            JsonObject deletedUser = JsonObject.mapFrom(u.get());
            deletedUser.remove("userToken");
            deletedUser.remove("password");

            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(deletedUser)));
        }
    }
}
