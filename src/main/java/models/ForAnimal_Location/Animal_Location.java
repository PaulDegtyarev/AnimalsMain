package models.ForAnimal_Location;

import jakarta.persistence.*;
import models.ForAnimal.Animal;
import models.ForLocation.Location;

import java.io.Serializable;
import java.util.List;

@Entity(name = "Animal_Location")
@Table(name = "animal_locations", schema = "animals")
public class Animal_Location implements Serializable {
    @OneToMany
    private List<Animal> animal;

    @OneToMany
    private List<Location> location;
}
