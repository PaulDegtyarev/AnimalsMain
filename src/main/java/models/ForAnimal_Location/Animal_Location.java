package models.ForAnimal_Location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import models.ForAnimal.Animal;
import models.ForLocation.Location;

import java.io.Serializable;

@Entity(name = "Animal_Location")
@Table(name = "animal_locations", schema = "animals")
public class Animal_Location implements Serializable {
    @OneToMany
    @Column(name = "animal_id")
    private Animal animal_id;

    @OneToMany
    @Column(name = "location_id")
    private Location location_id;
}
