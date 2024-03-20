package services.forLocation;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import models.ForAnimal.Animal;
import models.ForLocation.Location;
import models.ForPointsVisitedByAnimal.PointsVisitedByAnimal;
import models.ForZonePoints.ZonePoints;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import persistance.ForLocation.LocationPersistence;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

public class LocationManagerServiceImpl implements LocationManagerService {

    private final LocationPersistence locationPersistence;

    public LocationManagerServiceImpl(LocationPersistence locationPersistence) {
        this.locationPersistence = locationPersistence;
    }

    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Location.class)
            .addAnnotatedClass(PointsVisitedByAnimal.class)
            .addAnnotatedClass(ZonePoints.class)
            .addAnnotatedClass(Animal.class);

    @Override
    public boolean linkedWithAnimal(Integer locationIdForDelete){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToCheckLindkWithAnimal = session.createQuery("FROM Animal WHERE chippinglocationid = :locationIdForDelete");
        queryToCheckLindkWithAnimal.setParameter("locationIdForDelete", locationIdForDelete);

        List<Animal> listToCheckLindkWithAnimal = queryToCheckLindkWithAnimal.list();

        //true
        return !listToCheckLindkWithAnimal.isEmpty();
    }

    @Override
    public boolean chippingOrNot(JsonObject info){
        Double latitude = info.getDouble("latitude");
        Double longitude = info.getDouble("longitude");

        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToCheckChippingOrNot = session.createQuery("FROM ZonePoints WHERE latitude = :latitude AND longitude = :longitude");
        queryToCheckChippingOrNot.setParameter("latitude", latitude);
        queryToCheckChippingOrNot.setParameter("longitude", longitude);

        List<ZonePoints> listToCheckChippingOrNot = queryToCheckChippingOrNot.list();
        out.println(listToCheckChippingOrNot);

        //true
        return !listToCheckChippingOrNot.isEmpty();
    }

    @Override
    public boolean visitedOrNot(Integer idLocation){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToCheckVisitedOrNot = session.createQuery("FROM PointsVisitedByAnimal pvba JOIN pvba.location l WHERE l.id = :locationPointId");
        queryToCheckVisitedOrNot.setParameter("locationPointId", idLocation);

        List<PointsVisitedByAnimal> listToCheckVisitedOrNot = queryToCheckVisitedOrNot.list();
        out.println(listToCheckVisitedOrNot);
        session.getTransaction().commit();
        session.close();

        // true
        return !listToCheckVisitedOrNot.isEmpty();
    }

    @Override
    public boolean checkLocationId(String locationParam){
        if (locationParam == null || locationParam.isBlank()) return false;
        int locationId = Integer.parseInt(locationParam);
        return locationId > 0;
    }

    @Override
    public boolean emptyOrNot(Integer idForSearch){
        Session session = configuration
                .buildSessionFactory()
                .openSession();
        session.beginTransaction();

        Query queryToCheckLocation = session.createQuery("FROM Location WHERE id = :locationId");
        queryToCheckLocation.setParameter("locationId", idForSearch);

        List<Location> locationList = queryToCheckLocation.list();
        session.getTransaction().commit();
        session.close();

        out.println(locationList);

        //false
        return locationList.isEmpty();
    }

    @Override
    public boolean dataIsValid(JsonObject info){
        Double latitude = info.getDouble("latitude");
        Double longitude = info.getDouble("longitude");

        // Здесь по кд вернет true
        return latitude != null && latitude >= -90 && latitude <= 90 && longitude != null && longitude >= -180 && longitude <= 180;
    }

    @Override
    public boolean coordIsFree(JsonObject info){
        Double newLatitude = info.getDouble("latitude");
        Double newLongitude = info.getDouble("longitude");

        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToCheckLaltitudeORLongitudeIsFree = session.createQuery("FROM Location WHERE latitude = :newLatitude AND longitude = :newLongitude");
        queryToCheckLaltitudeORLongitudeIsFree.setParameter("newLatitude", newLatitude);
        queryToCheckLaltitudeORLongitudeIsFree.setParameter("newLongitude", newLongitude);

        List<Location> listToCheckLaltitudeANDLongitudeIsFree = queryToCheckLaltitudeORLongitudeIsFree.list();

        session.getTransaction().commit();
        session.close();

        //По кд вернет true
        return listToCheckLaltitudeANDLongitudeIsFree.isEmpty();
    }


    @Override
    public void getLocationById(Integer locationId,
                                ServiceRequest request,
                                Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<JsonObject> l = locationPersistence.getLocationById(locationId);
        if (l.isPresent()){
            JsonObject locatinoJson = JsonObject.mapFrom(l.get());
            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(locatinoJson)));
        }
    }

    @Override
    public void addLocation(JsonObject info,
                            ServiceRequest request,
                            Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<Location> l = locationPersistence.addLocation(info);

        if (l.isPresent()) {
            JsonObject locationJson = JsonObject.mapFrom(l.get());
            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(locationJson)));
        }
    }

    @Override
    public void updateLocationById(Integer idLocationForUpdate,
                                   JsonObject infoForUpdate,
                                   ServiceRequest request,
                                   Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<Location> l = locationPersistence.updateLocationById(idLocationForUpdate, infoForUpdate);
        if (l.isPresent()) {
            JsonObject locationInfoJson = JsonObject.mapFrom(l.get());
            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(locationInfoJson)));
        }
    }

    @Override
    public void deleteLocationById(Integer idLocationForDelete,
                                   ServiceRequest request,
                                   Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<Location> l = locationPersistence.deleteLocationById(idLocationForDelete);

        if (l.isPresent()){
            JsonObject locationInfoJson = JsonObject.mapFrom(l.get());
            locationInfoJson.remove("id");
            locationInfoJson.remove("latitude");
            locationInfoJson.remove("longitude");
            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(locationInfoJson)));
        }
    }
}
