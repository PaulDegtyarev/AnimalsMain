package services.ForTypes;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import persistance.ForTypes.TypesPersistance;

public interface TypesManagerService {
    static TypesManagerService create (TypesPersistance typesPersistance){
        return new TypesManagerServiceImpl(typesPersistance);
    }

    boolean checkTypeIsExist(Integer typeId);

    void getTypesById(Integer typeId,
                      ServiceRequest request,
                      Handler<AsyncResult<ServiceResponse>> resultHandler);
}
