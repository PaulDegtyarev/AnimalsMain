package models.ForZonePoints;

import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;

import java.io.Serializable;

@Entity(name = "ZonePoints")
@Table(name = "zone_points", schema = "animals")
public class ZonePoints implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;


    @Fluent public Integer getId(){return this.id;}

    @Fluent public void setLatitude(Double latitude){this.latitude = latitude;}
    public Double getLatitude(){return this.latitude;}

    @Fluent public void setLongitude(Double longitude){this.longitude = longitude;}
    public Double getLongitude(){return this.longitude;}

    @Override
    public String toString(){
        return String.valueOf(this.getId() + this.getLatitude() + this.getLongitude());
    }

}
