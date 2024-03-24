package services.ForAreas;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import models.ForAreas.Area;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import persistance.ForAreas.AreasPersistance;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AreasManagerServiceImpl implements AreasManagerService {
    private final AreasPersistance areasPersistance;

    public AreasManagerServiceImpl(AreasPersistance areasPersistance) {
        this.areasPersistance = areasPersistance;
    }

    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Area.class);

    public static class Vector2 {
        public double x;
        public double y;

        public Vector2(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Vector2 subtract(Vector2 other) {
            return new Vector2(this.x - other.x, this.y - other.y);
        }

        public Vector2 multiply(Vector2 other) {
            return new Vector2(this.x * other.x, this.y * other.y);
        }
    }

    public boolean areCrossing(Vector2 v11, Vector2 v12, Vector2 v21, Vector2 v22, Vector2 crossing) {
        Vector2 cut1 = v12.subtract(v11);
        Vector2 cut2 = v22.subtract(v21);
        double prod1, prod2;

        prod1 = crossProduct(cut1, v21.subtract(v11));
        prod2 = crossProduct(cut1, v22.subtract(v11));

        if ((sign(prod1) == sign(prod2)) || (prod1 == 0) || (prod2) == 0) return false;

        prod1 = crossProduct(cut2, v11.subtract(v21));
        prod2 = crossProduct(cut2, v12.subtract(v21));

        if ((sign(prod1) == sign(prod2)) && (prod1 == 0) && (prod2 == 0)) return false;

        if (crossing != null) {
            crossing.x = v11.x + cut1.x * Math.abs(prod1) / Math.abs(prod2 - prod1);
            crossing.y = v11.y + cut1.y * Math.abs(prod1) / Math.abs(prod2 - prod1);
        }
        return true;
    }

    private double crossProduct(Vector2 v1, Vector2 v2) {
        return v1.x * v2.y - v1.y * v2.x;
    }

    public int sign(double value) {
        return Double.compare(value, 0.0);
    }

    @Override
    public boolean validateData(JsonObject data) {
        if (data.getString("name").trim().isEmpty()) {
            return true;
        }

        JsonArray coords = data.getJsonArray("areaPoints");
        if (coords.size() < 3) {
            return true;
        }

        for (Object point : coords) {
            JsonObject jsonPoint = (JsonObject) point;

            if (!jsonPoint.containsKey("longitude") || (jsonPoint.getDouble("longitude") < -180 || jsonPoint.getDouble("longitude") > 180) ||
                    !jsonPoint.containsKey("latitude") || (jsonPoint.getDouble("latitude") < -90 || jsonPoint.getDouble("latitude") > 90)) {
                return true;
            }
        }

        for (int i = 0; i < coords.size(); i++) {
            JsonObject point1 = coords.getJsonObject(i);
            JsonObject point2 = coords.getJsonObject((i + 1) % coords.size());

            if (Objects.equals(point1.getDouble("latitude"), point2.getDouble("latitude")) &&
                    Objects.equals(point1.getDouble("longitude"), point2.getDouble("longitude"))) {
                return true;
            }

            Vector2 v11 = new Vector2(point1.getDouble("latitude"), point1.getDouble("longitude"));
            Vector2 v12 = new Vector2(point2.getDouble("latitude"), point2.getDouble("longitude"));

            for (int j = i + 1; j < coords.size(); j++) {
                JsonObject point3 = coords.getJsonObject(j);
                JsonObject point4 = coords.getJsonObject((j + coords.size() - 1) % coords.size());

                Vector2 v21 = new Vector2(point3.getDouble("latitude"), point3.getDouble("longitude"));
                Vector2 v22 = new Vector2(point4.getDouble("latitude"), point4.getDouble("longitude"));

                Vector2 crossing = new Vector2(0.0, 0.0);
                if (areCrossing(v11, v12, v21, v22, crossing)) return true;
            }
        }

        // Возвращает false, если не пересекается
        return false;
    }




@Override
    public boolean checkArea(Integer areaId){

        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToCheckArea = session.createQuery("FROM Area where area_id = :areaId");
        queryToCheckArea.setParameter("areaId",areaId);

        List<Area> listToCheckArea = queryToCheckArea.list();

        session.getTransaction().commit();
        session.close();

        //true
        return listToCheckArea.isEmpty();
    }


    @Override
    public void getAreaById(Integer areaId,
                            ServiceRequest request,
                            Handler<AsyncResult<ServiceResponse>> resultHandler){
        Optional<JsonObject> a = areasPersistance.getAreaById(areaId);
        if (a.isPresent()){
            JsonObject areaJson = JsonObject.mapFrom(a.get());
            resultHandler.handle(Future.succeededFuture(ServiceResponse.completedWithJson(areaJson)));
        }
    }
}
