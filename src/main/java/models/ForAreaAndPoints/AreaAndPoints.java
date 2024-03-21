package models.ForAreaAndPoints;

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
}
