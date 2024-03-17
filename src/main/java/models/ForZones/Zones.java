package models.ForZones;

import io.swagger.models.auth.In;
import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;
import models.ForZoneAndPoints.ZoneAndPoints;

import java.io.Serializable;
import java.util.List;

@Entity(name = "Zones")
@Table(name = "zones", schema = "animals")
public class Zones implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @OneToMany
    private List<ZoneAndPoints> zones;

    @Fluent public Integer getId(){return this.id;}

    @Fluent public void setName(String name){this.name = name;}
    public String getName(){return this.name;}
}
