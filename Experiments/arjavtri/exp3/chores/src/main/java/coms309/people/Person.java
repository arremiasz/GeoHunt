package coms309.people;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;

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

    private ArrayList<Chore> choresList =  new ArrayList<>();

    private ArrayList<Chore> completedChores =  new ArrayList<>();

    private int totalPoints;

//    public Person(){
//
//    }

    public Person(String firstName, String lastName, String address, String telephone, String chores){
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
        this.totalPoints = 0;
    }

    public Person(String firstName, String lastName, String address, String telephone, String chores, int totalPoints){
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
        this.totalPoints = totalPoints;
    }


    public boolean addChore(Chore chores){
        if(choresList.contains(chores)){
            return false;
        }
        this.choresList.add(chores);
        return true;
    }

    public boolean isChoreLate(Chore chores){
        LocalDate today = LocalDate.now();
        LocalDate due = chores.getDueDate();
        if(today.isAfter(due)){
            return true;
        }
        return false;
    }



    public boolean deleteChore(Chore chore){
        if(!choresList.contains(chore)){
            return false;
        }
        this.choresList.remove(chore);
        return true;
    }

    public boolean completeChore(Chore chore){
        if(!completedChores.contains(chore) && choresList.contains(chore)){
            if(!isChoreLate(chore)){
                totalPoints += chore.getPoints();
            }
            chore.markCompleted();
            completedChores.add(chore);
            choresList.remove(chore);
            return true;
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

    public ArrayList<Chore> getChores(){
        return this.choresList;
    }

    @Override
    public String toString() {
        return firstName + " "
                + lastName + " "
                + address + " "
                + telephone;
    }
}
