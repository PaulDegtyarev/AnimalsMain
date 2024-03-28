package persistance.ForTypes;

import io.vertx.core.json.JsonObject;
import models.ForAnimal.Animal;

import models.ForTypes.Types;
import org.hibernate.Session;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Optional;

public class TypesPersistanceImpl implements TypesPersistance{
    Configuration configuration = new Configuration().configure("hibernate.cfg.xml")
            .addAnnotatedClass(Types.class)
            .addAnnotatedClass(Animal.class);

    @Override
    public Optional<JsonObject> getTypesById(Integer typeId){
        JsonObject finalJson = new JsonObject();

        Session session = configuration.buildSessionFactory().openSession();
        session.beginTransaction();

        Query queryToGetTypeById = session.createQuery("SELECT t.type_id id, t.type_of_animal type FROM animals.animal_type ant JOIN ant.animal_id a ON ant.animal_id = a.id JOIN ant.type_id t ON ant.type_id = t.type_id WHERE t.type_id = :typeId;")
                .setParameter("typeId", typeId);

        List<Object[]> resultList = queryToGetTypeById.list();

        Object[] row = resultList.get(0);
        finalJson
                .put("id", row[0])
                        .put("type", row[1]);

        session.getTransaction().commit();
        session.close();


        return Optional.ofNullable(finalJson);
    }
}
