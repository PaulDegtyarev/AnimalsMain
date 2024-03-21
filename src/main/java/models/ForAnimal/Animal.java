package models.ForAnimal;


import jakarta.persistence.*;
import models.ForLocation.Location;
import models.ForUsers.Users;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Set;

@Entity(name="Animal")
@Table(name = "animal", schema = "animals")
public class Animal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "animaltypes")
    public BigInteger[] animaltypes;

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
    @JoinColumn(name = "chippinglocationid", referencedColumnName = "id")
    public Location chippinglocationid;


    @Column(name = "visitedLocations")
    public BigInteger[] visitedlocations;

    @Column(name = "deathdatetime")
    public Timestamp deathdatetime;


    public Integer getId(){
        return id;
    }
    public BigInteger[] getAnimaltypes(){
        return animaltypes;
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

    public Location getChippingLocationId() {
        return chippinglocationid;
    }

    public BigInteger[] getVisitedlocations() {
        return visitedlocations;
    }

    public Timestamp getDeathDateTime() {
        return deathdatetime;
    }
}
