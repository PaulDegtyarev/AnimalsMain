package persistance.ForAreas;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import models.ForAreaAndPoints.AreaAndPoints;
import models.ForAreas.Area;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

public class AreasPersistanceImpl implements AreasPersistance{
    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Area.class)
            .addAnnotatedClass(AreaAndPoints.class)
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
}
