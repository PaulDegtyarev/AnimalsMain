package persistance.ForAreas;

import io.vertx.core.json.JsonObject;
import models.ForAreas.Area;

import java.util.Optional;

public interface AreasPersistance {
    static AreasPersistance create(){return new AreasPersistanceImpl();}

    Optional<JsonObject> getAreaById(Integer areaId);

    Optional<JsonObject> addNewArea(JsonObject infoAboutNewArea);

    Optional<JsonObject> updateAreaById(Integer idForUpdate, JsonObject updateInfoAboutArea);

    Optional<JsonObject> deleteAreaById(Integer areaId);
}
