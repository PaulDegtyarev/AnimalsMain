package models.ForAnimal;


import jakarta.persistence.*;
import models.ForAreas.Area;
import models.ForLocation.Location;
import models.ForUsers.Users;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity(name="Animal")
@Table(name = "animal", schema = "animals")
public class Animal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(name = "weight")
    public Float weight;

    @Column(name = "length")
    public Float length;

    @Column(name = "height")
    public Float height;

    @Column(name = "gender")
    public String gender;

    @Column(name = "lifestatus")
    public String lifestatus;

    @Column(name = "chippingdatetime")
    public Timestamp chippingdatetime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chipperid", referencedColumnName = "id")
    public Users chipperid;

    @ManyToOne
    @JoinColumn(name = "chippinglocationid", referencedColumnName = "area_id")
    public Area chippinglocationid;

    @Column(name = "deathdatetime")
    public Timestamp deathdatetime;

    @ManyToMany
    @JoinTable(name = "AnimalType",
            joinColumns = {@JoinColumn(name = "animal_id")},
            inverseJoinColumns = { @JoinColumn(name = "type_id")})
    @Column(insertable=false, updatable=false)
    private Set<Animal> animals = new HashSet<>();


    public Integer getId(){
        return id;
    }

    public Float getWeight(){
        return weight;
    }

    public Float getLength(){
        return length;
    }

    public Float getHeight() {
        return height;
    }

    public String getGender() {
        return gender;
    }

    public String getLifeStatus() {
        return lifestatus;
    }

    public Timestamp getChippingDateTime() {
        return chippingdatetime;
    }

    public Users getChipperid() {
        return chipperid;
    }

    public Timestamp getDeathDateTime() {
        return deathdatetime;
    }
}
