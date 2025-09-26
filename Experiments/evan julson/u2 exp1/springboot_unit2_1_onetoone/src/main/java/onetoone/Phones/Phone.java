package onetoone.Phones;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import onetoone.Persons.Person;

@Getter
@Setter
@Entity
public class Phone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String manufacturer;
    private int cost;
    private int cameraCount;
    private String chargingPort;
    private String operatingSystem;

    @OneToOne
    @JsonIgnore
    private Person Person;

    public Phone( double screenSize, int cameraCount, String chargingPort, String operatingSystem, String manufacturer, int cost) {
        this.cameraCount = cameraCount;
        this.chargingPort = chargingPort;
        this.operatingSystem = operatingSystem;
        this.manufacturer = manufacturer;
        this.cost = cost;
    }

    public Phone() {
    }



}
