package models.ForTypes;

import io.vertx.codegen.annotations.Fluent;
import jakarta.persistence.*;
import models.ForAnimal.Animal;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity(name = "Types")
@Table(name = "types", schema = "animals")
public class Types implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(insertable=false, updatable=false)
    private Integer type_id;

    @Column(name = "type_of_animal")
    private String type_of_animal;

    @ManyToMany
    @Column(insertable=false, updatable=false)
    @JoinTable(name = "AnimalType",
    joinColumns = {@JoinColumn(name = "type_id")},
            inverseJoinColumns = { @JoinColumn(name = "id")})
    private Set<Types> types = new HashSet<>();

    public Integer getType_id(){return this.type_id;}

    public void setType_of_animal(String type_of_animal){
        this.type_of_animal = type_of_animal;
    }
    public String getType_of_animal(){return this.type_of_animal;}
}
