package models.ForPointsVisitedByAnimal;

import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;

@Entity(name = "PointsVisitedByAnimal")
@Table(name = "points_visited_by_animal", schema = "animals")
public class PointsVisitedByAnimal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "datetimeofvisitlocationpoint")
    private Timestamp dateTimeOfVisitLocationPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "locationpointid", referencedColumnName = "id")
    private Integer locationPointId;

    @Fluent public Integer getId(){return this.id;}

    @Fluent public void setDateTimeOfVisitLocationPoint(Timestamp dateTimeOfVisitLocationPoint){this.dateTimeOfVisitLocationPoint = dateTimeOfVisitLocationPoint;}
    public Timestamp getDateTimeOfVisitLocationPoint(){return this.dateTimeOfVisitLocationPoint;}

    @Fluent public void setLocationPointId(Integer locationPointId){this.locationPointId = locationPointId;}
    public Integer getLocationPointId(){return this.locationPointId;}
}
