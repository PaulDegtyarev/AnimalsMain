package models.ForLocation;

import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;
import models.ForPointsVisitedByAnimal.PointsVisitedByAnimal;

import java.io.Serializable;
import java.util.List;

@Entity(name = "Location")
@Table(name = "locations", schema = "animals")
public class Location implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

//    @OneToMany
//    private List<PointsVisitedByAnimal> point_visited_by_animal_id;


    @Fluent public Integer getLocationId(){
        return this.id;
    }

    @Fluent public void setLocationLatitude(Double latitude){
        this.latitude = latitude;
    }
    public Double getLocationLatitude(){
        return this.latitude;
    }

    @Fluent public void setLocationLongitude(Double longitude){
        this.longitude = longitude;
    }
    public Double getLocationLongitude(){
        return this.longitude;
    }

    @Override
    public String toString(){
        return String.valueOf(this.getLocationId() + this.getLocationLatitude() + this.getLocationLongitude());
    }
}
