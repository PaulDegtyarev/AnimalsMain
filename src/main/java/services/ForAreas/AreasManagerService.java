package services.ForAreas;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import persistance.ForAreas.AreasPersistance;

public interface AreasManagerService {
    static AreasManagerService create(AreasPersistance areasPersistance){
        return new AreasManagerServiceImpl(areasPersistance);
    }

    boolean checkArea(Integer areaId);

    void getAreaById(Integer areaId,
                     ServiceRequest request,
                     Handler<AsyncResult<ServiceResponse>> resultHandler);

    boolean validateData(JsonObject data);

}
