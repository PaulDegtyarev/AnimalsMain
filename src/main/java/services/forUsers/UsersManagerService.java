package services.forUsers;


import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import io.vertx.ext.web.api.service.WebApiServiceGen;
import persistance.forUsers.UsersPersistence;

@WebApiServiceGen
public interface UsersManagerService {

    static UsersManagerService create(UsersPersistence persistence){
        return new UsersManagerServiceImpl(persistence);
    }


    boolean linkedWithAnimal(String userIdForDeleteParameter);

    boolean emailIsBusy(String newEmail);

    String checkRole(String userToken);

    boolean emptyOrNot(Integer idForSearch);

    boolean stranger(String userToken, Integer idForSearch);

    boolean checkAccountId(String accountParam);

    boolean checkUserToAuthorize(String userToken);

    void updateUserById(
            Integer userIdForUpdate,
            JsonObject newInfo,
            ServiceRequest request,
            Handler<AsyncResult<ServiceResponse>> resultHandler);

    void registration(JsonObject userInfo,
                      ServiceRequest request,
                      Handler<AsyncResult<ServiceResponse>> resultHandler);

    void getUserById(
            Integer id,
            ServiceRequest request,
            Handler<AsyncResult<ServiceResponse>> resultHandler);

    void addUser(JsonObject info,
                 ServiceRequest request,
                 Handler<AsyncResult<ServiceResponse>> resultHandler);

    void getUserByParam(
            String firstName,
            String lastName,
            String email,
            Integer from,
            Integer size,
            ServiceRequest request,
            Handler<AsyncResult<ServiceResponse>> resultHandler);

    void deleteUserById(Integer userIdForDelete,
                        ServiceRequest request,
                        Handler<AsyncResult<ServiceResponse>> resultHandler);
}
