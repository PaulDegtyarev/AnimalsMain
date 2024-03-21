package models.ForAreaPoints;

import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;
import models.ForAreas.Area;

import java.io.Serializable;
import java.util.Set;

@Entity(name = "AreaPoints")
@Table(name = "area_points", schema = "animals")
public class AreaPoints implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer area_point_id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;


    @Fluent public Integer getId(){return this.area_point_id;}

    @Fluent public void setLatitude(Double latitude){this.latitude = latitude;}
    public Double getLatitude(){return this.latitude;}

    @Fluent public void setLongitude(Double longitude){this.longitude = longitude;}
    public Double getLongitude(){return this.longitude;}

    @Override
    public String toString(){
        return String.valueOf(this.getId() + this.getLatitude() + this.getLongitude());
    }

}
