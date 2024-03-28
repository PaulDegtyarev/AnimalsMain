package services.ForTypes;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import models.ForAnimal.Animal;
import models.ForAreaAndPoints.AreaAndPoints;
import models.ForAreaPoints.AreaPoints;
import models.ForAreas.Area;
import models.ForTypes.Types;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import persistance.ForTypes.TypesPersistance;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

public class TypesManagerServiceImpl implements TypesManagerService{
    private final TypesPersistance typesPersistance;

    public TypesManagerServiceImpl(TypesPersistance typesPersistance) {
        this.typesPersistance = typesPersistance;
    }

    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Types.class)
            .addAnnotatedClass(Animal.class);

    @Override
    public boolean checkTypeIsExist(Integer typeId){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToCheckTypeById = session.createQuery("FROM Types where type_id = :typeId")
                .setParameter("typeId", typeId);

        List<Types> typesList = queryToCheckTypeById.list();
        session.getTransaction().commit();
        session.close();

        // true
        return typesList.isEmpty();
    }

    @Override
    public void getTypesById(Integer typeId,
                             ServiceRequest request,
                             Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<JsonObject> t = typesPersistance.getTypesById(typeId);
        if (t.isPresent()){
            JsonObject typesJson = JsonObject.mapFrom(t.get());
            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(typesJson)));
        }
    }

}
