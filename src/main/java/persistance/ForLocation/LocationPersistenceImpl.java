package persistance.ForLocation;

import io.vertx.core.json.JsonObject;
import models.ForLocation.Location;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

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

        out.println(newLocation);

        return Optional.ofNullable(newLocation);

    }
}
