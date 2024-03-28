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

    //Для отношения новой и имеющихся зон
//    boolean checkConflictBetweenNewAreaAndAvailableAreas(JsonObject infoAboutNewArea);

    boolean checkAreaIsBusy(JsonObject infoAboutnewArea);

    boolean checkArea(Integer areaId);

    void getAreaById(Integer areaId,
                     ServiceRequest request,
                     Handler<AsyncResult<ServiceResponse>> resultHandler);

    boolean validateData(JsonObject data);

    void addNewArea(JsonObject infoAboutNewArea,
                    ServiceRequest request,
                    Handler<AsyncResult<ServiceResponse>> resultHandler);

    void updateAreaById(Integer areaIdForUpdate,
                        JsonObject updateInfoAboutArea,
                        ServiceRequest request,
                        Handler<AsyncResult<ServiceResponse>> resultHandler);

    void deleteAreaById(Integer areaId,
                        ServiceRequest request,
                        Handler<AsyncResult<ServiceResponse>> resultHandler);
}
