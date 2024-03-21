package models.ForAreas;

import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;



import java.io.Serializable;


@Entity(name = "Area")
@Table(name = "areas", schema = "animals")
public class Area implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer area_id;

    @Column(name = "name")
    private String name;

    @Fluent public Integer getId(){return this.area_id;}

    @Fluent public void setName(String name){this.name = name;}
    public String getName(){return this.name;}
}
