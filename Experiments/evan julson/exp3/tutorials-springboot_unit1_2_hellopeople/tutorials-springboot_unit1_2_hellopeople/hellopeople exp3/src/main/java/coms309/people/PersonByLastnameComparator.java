package coms309.people;

import java.util.Comparator;

public class PersonByLastnameComparator implements Comparator<Person> {
    @Override
    public int compare(Person o1, Person o2) {
        String firstName1 = o1.getFirstName();
        String firstName2 = o2.getFirstName();
        String lastName1 = o1.getLastName();
        String lastName2 = o2.getLastName();

        if(lastName1.equals(lastName2)){
            return firstName1.compareTo(firstName2);
        }
        return lastName1.compareTo(lastName2);
    }
}
