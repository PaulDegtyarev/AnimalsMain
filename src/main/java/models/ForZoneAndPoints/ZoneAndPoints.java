package models.ForZoneAndPoints;

import io.swagger.models.auth.In;
import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;
import models.ForZonePoints.ZonePoints;
import models.ForZones.Zones;

import java.io.Serializable;

@Entity(name = "ZoneAndPoints")
@Table(name = "zoneandpoints", schema = "animals")
public class ZoneAndPoints implements Serializable {
    @Column(name = "zone_id")
    public Zones zone_id;

    @Column(name = "point_id")
    private ZonePoints point_id;
}
