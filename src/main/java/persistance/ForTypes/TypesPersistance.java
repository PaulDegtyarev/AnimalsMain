package persistance.ForTypes;

import io.vertx.core.json.JsonObject;

import java.util.Optional;

public interface TypesPersistance {
    static TypesPersistance create(){return new TypesPersistanceImpl();}

    Optional<JsonObject> getTypesById(Integer typeId);
}
