package coms309.people;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */
@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class Person {

    /**
     * -- GETTER --
     *  Getter and Setters below are technically redundant and can be removed.
     *  They will be generated from the @Getter and @Setter tags above class
     */
    private String userName;

    private String firstName;

    private String lastName;

    private String address;

    private String telephone;

    private HashMap<String, Task> taskList = new HashMap<>();

//    public Person(){
//
//    }

    public Person(String userName, String firstName, String lastName, String address, String telephone){
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.telephone = telephone;
    }

    // Will attempt to create a task with a given name and due date.
    // Returns true if task is successfully created.
    public boolean createTask(String name, String date){
        if(taskList.containsKey(name)){
            return false;
            // Task with given name already exists
        }
        LocalDate dueDate = LocalDate.parse(date);
        Task newTask = new Task(name, dueDate);
        taskList.put(name, newTask);
        return true;
    }

    // Gets tasks
    public Task getTask(String name){
        return taskList.get(name);
    }

    // Removes and returns a task from a list.
    public Task removeTask(String name){
        Task removedTask = taskList.get(name);
        taskList.remove(name);
        return removedTask;
    }

    @Override
    public String toString() {
        return userName + " "
               + firstName + " "
               + lastName + " "
               + address + " "
               + telephone;
    }

    @Override
    public boolean equals(Object other){
        if(!this.getClass().equals(other.getClass())){
            return false;
        }
        Person otherPerson = (Person)other;

        if(!userName.equals(otherPerson.getUserName())){
            return false;
        }
        if(!firstName.equals(otherPerson.getFirstName())){
            return false;
        }
        if(!lastName.equals(otherPerson.getLastName())){
            return false;
        }
        if(!address.equals(otherPerson.getAddress())){
            return false;
        }
        if(!telephone.equals(otherPerson.getTelephone())){
            return false;
        }
        return true;
    }
}
