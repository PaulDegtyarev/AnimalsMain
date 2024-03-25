package persistance.ForAreas;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import models.ForAreaAndPoints.AreaAndPoints;
import models.ForAreaPoints.AreaPoints;
import models.ForAreas.Area;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class AreasPersistanceImpl implements AreasPersistance{
    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Area.class)
            .addAnnotatedClass(AreaAndPoints.class);

    @Override
    public Optional<JsonObject> getAreaById(Integer areaId){
        JsonObject areaJson = new JsonObject();
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToGetAreaById = session.createQuery("select a.area_id id, a.name name, ap.latitude latitude, ap.longitude longitude from AreaAndPoints aap join aap.area_id a join aap.area_point_id ap where a.area_id = :areaId");
        queryToGetAreaById.setParameter("areaId", areaId);

        List<Object[]> resultList = queryToGetAreaById.getResultList();

        JsonArray areaPointsJsonArray = new JsonArray();
        for (Object[] row : resultList) {
            Double longitude = (Double) row[3];
            Double latitude = (Double) row[2];

            JsonObject areaPointJson = new JsonObject();
            areaPointJson.put("longitude", longitude);
            areaPointJson.put("latitude", latitude);

            areaPointsJsonArray.add(areaPointJson);
        }

        areaJson.put("id", resultList.get(0)[0]);
        areaJson.put("name", resultList.get(0)[1]);
        areaJson.put("arePoints", areaPointsJsonArray);

        session.getTransaction().commit();
        session.close();

        return Optional.ofNullable(areaJson);
    }

    @Override
    public Optional<JsonObject> addNewArea(JsonObject infoAboutNewArea) {
        JsonObject finalJsonArea = new JsonObject();
        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Area newArea = new Area();
        newArea.setName(infoAboutNewArea.getString("name"));
        session.persist(newArea);

        JsonArray areaPointsJsonArray = new JsonArray();
        JsonArray areaPoints = infoAboutNewArea.getJsonArray("areaPoints");
        for (int i = 0; i < areaPoints.size(); i++) {
            JsonObject point = areaPoints.getJsonObject(i);
            AreaPoints newAreaPoint = new AreaPoints();
            newAreaPoint.setLatitude(point.getDouble("latitude"));
            newAreaPoint.setLongitude(point.getDouble("longitude"));
            session.persist(newAreaPoint);

            AreaAndPoints newAreaAndPoints = new AreaAndPoints();
            newAreaAndPoints.setArea_id(newArea);
            newAreaAndPoints.setArea_point_id(newAreaPoint);
            session.persist(newAreaAndPoints);

            JsonObject pointJson = new JsonObject()
                    .put("latitude", newAreaPoint.getLatitude())
                    .put("longitude", newAreaPoint.getLongitude());
            areaPointsJsonArray.add(pointJson);
        }

        session.getTransaction().commit();
        session.close();

        finalJsonArea
                .put("id", newArea.getId())
                .put("name", newArea.getName())
                .put("areaPoints", areaPointsJsonArray);

        return Optional.ofNullable(finalJsonArea);
    }

    @Override
    public Optional<JsonObject> updateAreaById(Integer idForUpdate, JsonObject updateInfoAboutArea){
        JsonObject finalJson = new JsonObject();

        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Area area = session.get(Area.class, idForUpdate);
        area.setName(updateInfoAboutArea.getString("name"));
        session.update(area);

        Query queryToGetAreaForUpdate = session.createQuery("FROM AreaAndPoints aap WHERE aap.area_id = :idForUpdate")
                .setParameter("idForUpdate", updateInfoAboutArea);

        List<AreaAndPoints> areasAndPoints = queryToGetAreaForUpdate.list();

        for (AreaAndPoints areaAndPoints : areasAndPoints){
            AreaPoints areaPoint = areaAndPoints.getArea_point_id();

            JsonArray areaPointsJsonArray = new JsonArray();
            for (Object element : updateInfoAboutArea.getJsonArray("areaPoints")){
                JsonObject areaPointJson = (JsonObject) element;
                if (areaPoint.getId().equals(areaPointJson.getInteger("id"))){
                    areaPoint.setLatitude(areaPointJson.getDouble("latitude"));
                    areaPoint.setLongitude(areaPointJson.getDouble("longitude"));

                    session.update(areaPoint);
                }
            }
        }
        session.getTransaction().commit();
        session.close();

        finalJson.put("id", area.getId());
        finalJson.put("name", area.getName());
        finalJson.put("areaPoints", )

        return Optional.ofNullable(finalJson);
    }
}



@Override
public Optional<JsonObject> updateAreaById(Integer idForUpdate, JsonObject updateInfoAboutArea) {
    JsonObject finalJson = new JsonObject();
    Session session = configuration.buildSessionFactory().openSession();
    session.beginTransaction();

    Area area = session.get(Area.class, idForUpdate);
    area.setName(updateInfoAboutArea.getString("name"));
    session.update(area);

    JsonArray areaPointsJsonArray = new JsonArray();
    for (Object element : updateInfoAboutArea.getJsonArray("areaPoints")) {
        JsonObject areaPointJson = (JsonObject) element;
        AreaPoints areaPoint = session.get(AreaPoints.class, areaPointJson.getInteger("id"));
        if (areaPoint != null) {
            areaPoint.setLatitude(areaPointJson.getDouble("latitude"));
            areaPoint.setLongitude(areaPointJson.getDouble("longitude"));
            session.update(areaPoint);
            areaPointsJsonArray.add(areaPointJson);
        }
    }

    session.getTransaction().commit();
    session.close();

    finalJson.put("id", area.getId());
    finalJson.put("name", area.getName());
    finalJson.put("areaPoints", areaPointsJsonArray);

    return Optional.of(finalJson);
}
