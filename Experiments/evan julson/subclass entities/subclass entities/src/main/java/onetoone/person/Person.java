package onetoone.person;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import onetoone.item.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String name;
    @OneToMany
    List<Item> inventory;

    public Person(String name){
        this.name = name;
        inventory = new ArrayList<>();
    }

    
}
