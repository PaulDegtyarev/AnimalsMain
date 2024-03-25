package models.ForAreaAndPoints;

import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;
import models.ForAreaPoints.AreaPoints;
import models.ForAreas.Area;


import java.io.Serializable;

@Entity(name = "AreaAndPoints")
@Table(name = "area_and_points", schema = "animals")
public class AreaAndPoints implements Serializable {
    @Id
    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area_id ;

    @Id
    @ManyToOne
    @JoinColumn(name = "area_point_id")
    public AreaPoints area_point_id;

    @Fluent public void setArea_id(Area areaId){this.area_id = areaId;}
    public Area getArea_id(){return area_id;}
    @Fluent public void setArea_point_id(AreaPoints areaPointId){this.area_point_id = areaPointId;}
    public AreaPoints getArea_point_id(){return area_point_id;}
}
