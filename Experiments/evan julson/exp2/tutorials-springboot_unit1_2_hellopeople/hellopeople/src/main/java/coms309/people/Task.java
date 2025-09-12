package coms309.people;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter // Lombok Shortcut for generating getter methods (Matches variable names set ie firstName -> getFirstName)
@Setter // Similarly for setters as well
@NoArgsConstructor // Default constructor
public class Task {
    private String name;

    private LocalDate deadline;

    private boolean complete;

    public Task(String name, LocalDate deadline){
        this.name = name;
        this.deadline = deadline;
        this.complete = false;
    }

    public Task(String name, LocalDate deadline, boolean complete){
        this.name = name;
        this.deadline = deadline;
        this.complete = complete;
    }

    @Override
    public String toString() {
        if(complete){
            return "Task " + name + " is due on " + deadline + " and is completed";
        }
        else{
            return "Task " + name + " is due on " + deadline + " and is not completed";
        }
    }
}
