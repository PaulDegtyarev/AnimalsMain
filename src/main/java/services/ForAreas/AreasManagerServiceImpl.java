package services.ForAreas;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.service.ServiceRequest;
import io.vertx.ext.web.api.service.ServiceResponse;
import models.ForAreaAndPoints.AreaAndPoints;
import models.ForAreaPoints.AreaPoints;
import models.ForAreas.Area;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import persistance.ForAreas.AreasPersistance;

import java.util.List;
import java.util.Optional;

import static java.lang.System.out;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

public class AreasManagerServiceImpl implements AreasManagerService {
    private final AreasPersistance areasPersistance;

    public AreasManagerServiceImpl(AreasPersistance areasPersistance) {
        this.areasPersistance = areasPersistance;
    }

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

    public static class Vector3 {
        public double x;
        public double y;
        public double z;

        public Vector3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3 subtract(Vector3 other) {
            return new Vector3(this.x - other.x, this.y - other.y, this.z - other.z);
        }

        public Vector3 multiply(Vector3 other) {
            return new Vector3(this.x * other.x, this.y * other.y, this.z * other.z);
        }
    }

    public boolean areCrossing(Vector2 v11, Vector2 v12, Vector2 v21, Vector2 v22, Vector2 crossing) {
        Vector2 cut1 = v12.subtract(v11);
        Vector2 cut2 = v22.subtract(v21);
        Vector3 prod1, prod2;

        prod1 = cross(cut1.multiply(v21.subtract(v11)));
        prod2 = cross(cut1.multiply(v22.subtract(v11)));

        if ((sign(prod1.z) == sign(prod2.z)) || (prod1.z == 0) || (prod2.z == 0)) {
            return false;
        }

        prod1 = cross(cut2.multiply(v11.subtract(v21)));
        prod2 = cross(cut2.multiply(v12.subtract(v21)));

        if ((sign(prod1.z) == sign(prod2.z)) || (prod1.z == 0) || (prod2.z == 0)) {
            return false;
        }

        if (crossing != null) {
            crossing.x = v11.x + cut1.x * Math.abs(prod1.z) / Math.abs(prod2.z - prod1.z);
            crossing.y = v11.y + cut1.y * Math.abs(prod1.z) / Math.abs(prod2.z - prod1.z);
        }

        return true;
    }

    public Vector3 cross(Vector2 vec) {
        return null;
    }

    public int sign(double value) {
        return Double.compare(value, 0.0);
    }

    @Override
    public boolean validateData(JSONObject data) {
        if (data.getString("name").trim().isEmpty()) {
            return true;
        }

        JSONArray coords = data.getJSONArray("areaPoints");
        if (coords.length() < 3) {
            return true;
        }

        for (Object point : coords) {
            JSONObject jsonPoint = (JSONObject) point;

            if (!jsonPoint.has("longitude") || (jsonPoint.getDouble("longitude") < -180 || jsonPoint.getDouble("longitude") > 180) ||
                    !jsonPoint.has("latitude") || (jsonPoint.getDouble("latitude") < -90 || jsonPoint.getDouble("latitude") > 90)) {
                return true;
            }
        }

        for (int i = 0; i < coords.length(); i++) {
            JSONObject point1 = coords.getJSONObject(i);
            JSONObject point2 = coords.getJSONObject((i + coords.length() - 1) % coords.length());

            if (point1.getDouble("latitude") == point2.getDouble("latitude") &&
                    point1.getDouble("longitude") == point2.getDouble("longitude")) {
                return true;
            }

            Vector2 v11 = new Vector2(point1.getDouble("latitude"), point1.getDouble("longitude"));
            Vector2 v12 = new Vector2(point2.getDouble("latitude"), point2.getDouble("longitude"));

            for (int j = i + 1; j < coords.length(); j++) {
                JSONObject point3 = coords.getJSONObject(j);
                JSONObject point4 = coords.getJSONObject((j + coords.length() - 1) % coords.length());

                Vector2 v21 = new Vector2(point3.getDouble("latitude"), point3.getDouble("longitude"));
                Vector2 v22 = new Vector2(point4.getDouble("latitude"), point4.getDouble("longitude"));

                Vector2 crossing = new Vector2(0.0, 0.0);
                if (areCrossing(v11, v12, v21, v22, crossing)) {
                    return true;
                }
            }
        }

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
