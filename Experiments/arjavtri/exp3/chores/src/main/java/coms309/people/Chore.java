package coms309.people;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


/**
 * Provides the Definition/Structure for the people row
 *
 * @author Vivek Bengre
 */
@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class Chore {

    private String name;

    private int difficult; //difficulty out of 10

    private int time;

    private LocalDate dueDate;

    private boolean completed;

    private int points;

    public Chore(String name, int difficult, LocalDate dueDate) {
        this.name = name;
        this.difficult = difficult;
        this.dueDate = dueDate;
        this.completed = false;
        this.points = 10; // default points per chore
    }

    public Chore(String name, int difficult, LocalDate dueDate, int points) {
        this.name = name;
        this.difficult = difficult;
        this.dueDate = dueDate;
        this.completed = false;
        this.points = points;
    }

    public void markCompleted() {
        this.completed = true;
    }





    /**
     * Getter and Setters below are technically redundant and can be removed.
     * They will be generated from the @Getter and @Setter tags above class
     */




    @Override
    public String toString() {
        return getName() + " - " + getDifficult() + "/10 difficulty, due on: " + getDueDate();
    }
}
