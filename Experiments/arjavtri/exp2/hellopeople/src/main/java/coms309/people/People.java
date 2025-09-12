package coms309.people;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */
@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class People {

    private String firstName;

    private String lastName;

    private String address;

    private String telephone;

    private ArrayList<String> chores =  new ArrayList<>();

    private int numChoresFinished;

//    public Person(){
//
//    }

    public People(String firstName, String lastName, String address, String telephone, String chores, int numChoresFinished){
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
    }


    public void addChore(String chores){
        this.chores.add(chores);
    }

    public void incrementChoreCounter(){
        this.numChoresFinished++;
    }

    public boolean deleteChore(String chore){
        for(int i = 0 ; i < chores.size() ; i++){
            if(chores.get(i).equals(chore)){
                chores.remove(i);
                incrementChoreCounter();
                return true;
            }
        }
        return false;
    }



    /**
     * Getter and Setters below are technically redundant and can be removed.
     * They will be generated from the @Getter and @Setter tags above class
     */

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public ArrayList<String> getChores(){
        return this.chores;
        }

    @Override
    public String toString() {
        return firstName + " " 
               + lastName + " "
               + address + " "
               + telephone
                + numChoresFinished + " chores finished " + chores.size() + " chores left " ;
    }
}
