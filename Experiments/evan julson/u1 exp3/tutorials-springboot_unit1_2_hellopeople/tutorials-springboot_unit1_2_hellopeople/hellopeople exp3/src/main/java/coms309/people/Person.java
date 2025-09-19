package coms309.people;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */
@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class Person {

    private String firstName;

    private String lastName;

    private String address;

    private String telephone;

//    public Person(){
//
//    }

    public Person(String firstName, String lastName, String address, String telephone){
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
    }


    /**
     * Getter and Setters below are technically redundant and can be removed.
     * They will be generated from the @Getter and @Setter tags above class
     */

    @Override
    public String toString() {
        return firstName + " " 
               + lastName + " "
               + address + " "
               + telephone;
    }
}
