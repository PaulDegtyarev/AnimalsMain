package models.ForUsers;

import java.io.Serializable;

import com.google.gson.Gson;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.json.JsonObject;

import jakarta.persistence.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import java.util.Base64;

@Entity(name = "Users")
@Table(name = "users", schema = "animals")
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name =  "firstname")
    private String firstname;

    @Column(name = "lastname")
    private String lastname;

    @Column(name = "email")
    private String email;

    @Column(name = "role")
    private String role;

    @Column(name = "password")
    private String password;

    @Column(name = "usertoken")
    private String usertoken;

    public static Users fromJson(JsonObject json){
        Gson gson = new Gson();
        return gson.fromJson(json.toString(), Users.class);

    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    @Fluent public void setId(Integer id){
        this.id = id;
    }
    public Integer getId(){
        return this.id;
    }

    @Fluent public void setFirstName(String firstname){
        this.firstname = firstname;
    }
    public String getFirstName(){
        return this.firstname;
    }

    @Fluent public void setLastName(String lastname){
        this.lastname = lastname;
    }
    public String getLastName(){
        return this.lastname;
    }

    @Fluent public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }

    private String generateHashedPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecureRandom random = new SecureRandom().getInstanceStrong();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();

        return Base64.getEncoder().encodeToString(hash);
    }
    @Fluent public void setPassword(String password){
        try {
            this.password = generateHashedPassword(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            e.printStackTrace();
        }
    }
    public String getPassword (){
        return this.password;
    }

    @Fluent public void setUserToken(String usertoken){
        this.usertoken = usertoken;
    }
    public String getUserToken(){
        return this.usertoken;
    }

    @Fluent public void setRole(String role){
        this.role = role;
    }
    public String getRole(){
        return this.role;
    }

    @Override
    public String toString() {

        return this.getId() + this.getFirstName() + this.getLastName() + this.getEmail() + this.getRole();

    }

}