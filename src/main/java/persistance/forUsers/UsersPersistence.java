package persistance.forUsers;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import models.ForUsers.Users;

import java.util.Optional;


public interface UsersPersistence {

    static UsersPersistence create(){
        return new UsersPersistanceImpl();
    }


    Optional<Users> registration(JsonObject userInfo);

    Optional<JsonObject> getUsersById(Integer id);

    Optional<JsonArray> getUsersByParameter(String firstName,
                                            String lastName,
                                            String email,
                                            Integer from,
                                            Integer size);

    Optional<Users> addUser(JsonObject info);

    Optional<Users> updateUserById(Integer userIdForUpdate, JsonObject newInfo);

    Optional<Users> deleteUserById(Integer idUserForDelete);
}
