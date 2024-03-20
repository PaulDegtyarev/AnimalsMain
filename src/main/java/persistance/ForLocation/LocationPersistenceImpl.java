package persistance.ForLocation;

import io.vertx.core.json.JsonObject;
import models.ForLocation.Location;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class LocationPersistenceImpl implements LocationPersistence {
    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Location.class);

    @Override
    public Optional<JsonObject> getLocationById(Integer locationId){
        Session session = configuration.buildSessionFactory().openSession();

        JsonObject locationJson = new JsonObject();

        session.beginTransaction();

        Query queryToGetLocationById = session.createQuery("FROM Location WHERE id = :locationId");
        queryToGetLocationById.setParameter("locationId", locationId);

        List<Location> locationList = queryToGetLocationById.list();

        if (!locationList.isEmpty()){
            Location location = locationList.get(0);

            locationJson.put("id", location.getLocationId());
            locationJson.put("latitude", location.getLocationLatitude());
            locationJson.put("longitude", location.getLocationLongitude());

            session.getTransaction().commit();
            session.close();
        }


        return Optional.ofNullable(locationJson);
    }

    @Override
    public Optional<Location> addLocation(JsonObject infoAboutNewLocation){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Location newLocation = new Location();
        newLocation.setLocationLatitude(infoAboutNewLocation.getDouble("latitude"));
        newLocation.setLocationLongitude(infoAboutNewLocation.getDouble("longitude"));

        session.persist("location", newLocation);
        session.getTransaction().commit();
        session.close();


        return Optional.ofNullable(newLocation);

    }

    @Override
    public Optional<Location> updateLocationById(Integer idLocationForUpdate, JsonObject updateInfo){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToGetLolcationById = session.createQuery("FROM Location WHERE id = :locationId");
        queryToGetLolcationById.setParameter("locationId", idLocationForUpdate);

        List<Location> listLocations = queryToGetLolcationById.list();

        Location locationForUpdate = listLocations.get(0);

        locationForUpdate.setLocationLatitude(updateInfo.getDouble("latitude"));
        locationForUpdate.setLocationLongitude(updateInfo.getDouble("longitude"));

        session.update(locationForUpdate);
        session.getTransaction().commit();
        session.close();

        return Optional.ofNullable(locationForUpdate);
    }

    @Override
    public Optional<Location> deleteLocationById(Integer locationIdForDelete){
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToGetLcaotionForDelete = session.createQuery("FROM Location WHERE id = :locationIdForDelete");
        queryToGetLcaotionForDelete.setParameter("locationIdForDelete", locationIdForDelete);

        List<Location> listLocations = queryToGetLcaotionForDelete.list();

        Location locationForDelete = listLocations.get(0);

        session.delete(locationForDelete);
        session.getTransaction().commit();
        session.close();

        return Optional.ofNullable(locationForDelete);
    }
}
