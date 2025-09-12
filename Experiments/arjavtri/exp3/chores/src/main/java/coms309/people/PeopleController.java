package coms309.people;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Controller used to showcase Create and Read from a LIST
 *
 * @author Vivek Bengre
 */

@RestController
public class PeopleController {

    // Note that there is only ONE instance of PeopleController in
    // Springboot system.
    HashMap<String, Person> peopleList = new  HashMap<>();
    HashMap<String, Chore> choreList = new  HashMap<>();

    //CRUDL (create/read/update/delete/list)
    // use POST, GET, PUT, DELETE, GET methods for CRUDL

    // THIS IS THE LIST OPERATION
    // gets all the people in the list and returns it in JSON format
    // This controller takes no input.
    // Springboot automatically converts the list to JSON format
    // in this case because of @ResponseBody
    // Note: To LIST, we use the GET method
    @GetMapping("/people")
    public  HashMap<String,Person> getAllPersons() {
        return peopleList;
    }

    @GetMapping("/chores")
    public  HashMap<String,Chore> getAllChores() {return choreList;}

    // THIS IS THE CREATE OPERATION
    // springboot automatically converts JSON input into a person object and
    // the method below enters it into the list.
    // It returns a string message in THIS example.
    // Note: To CREATE we use POST method
    @PostMapping("/people")
    public  String createPerson(@RequestBody Person person) {
        System.out.println(person);
        peopleList.put(person.getFirstName(), person);
        String s = "New person "+ person.getFirstName() + " Saved";
        return s;
        //public  ResponseEntity<Map<String, String>>  //unused
        // createPerson(@RequestBody Person person) { // unused
        //Map <String, String> body = new HashMap<>();// unused
        //body.put("message", s); // unused
        //ResponseEntity<>(body, HttpStatus.OK); // unused
    }

    @PostMapping("/chore")
    public String createChore(@RequestBody Chore chore) {
        System.out.println(chore);
        choreList.put(chore.getName(), chore);
        String s = "New chore "+ chore.getName() + " Saved";
        return s;
    }


    // THIS IS THE READ OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We extract the person from the HashMap.
    // springboot automatically converts Person to JSON format when we return it
    // Note: To READ we use GET method
    @GetMapping("/people/{firstName}")
    public Person getPerson(@PathVariable String firstName) {
        Person p = peopleList.get(firstName);
        return p;
    }

    // THIS IS A GET METHOD
    // RequestParam is expected from the request under the key "name"
    // returns all names that contains value passed to the key "name"
    @GetMapping("/people/contains")
    public List<Person> getPersonByParam(@RequestParam("name") String name) {
        List<Person> res = new ArrayList<>();
        for (Person p : peopleList.values()) {
            if (p.getFirstName().contains(name) || p.getLastName().contains(name))
                res.add(p);
        }
        return res;
    }

    @PutMapping("/people/{firstName}/{choreName}")
    public Person updatePerson(@PathVariable String choreName, @PathVariable String firstName) {
        if(choreList.containsKey(choreName) && peopleList.containsKey(firstName)) {
            Person update = peopleList.get(firstName);
            Chore chore = choreList.get(choreName);
            update.addChore(chore);
            return update;
        } else {
            return null;
        }

    }

    @GetMapping("/people/chores")
    public List<Chore> getChoresOfPerson(@RequestParam("name") String name){
        if(peopleList.containsKey(name)) {
            return peopleList.get(name).getChores();
        }
        return null;
    }

    @GetMapping("/people/completedNumber")
    public int getNumberOfChoresFinished(@RequestParam("name") String name){
        if(peopleList.containsKey(name)) {
            return peopleList.get(name).getCompletedChores().size();
        }
        return -1;
    }

    // THIS IS THE UPDATE OPERATION
    // We extract the person from the HashMap and modify it.
    // Springboot automatically converts the Person to JSON format
    // Springboot gets the PATHVARIABLE from the URL
    // Here we are returning what we sent to the method
    // Note: To UPDATE we use PUT method
    @PutMapping("/people/{firstName}")
    public Person updatePerson(@PathVariable String firstName, @RequestBody Person p) {
        peopleList.replace(firstName, p);
        return peopleList.get(firstName);
    }

    @PutMapping("/people/{firstName}/deleteChore")
    public Person deleteChore(@PathVariable String firstName, @RequestParam String chore) {
        if(peopleList.containsKey(firstName) && choreList.containsKey(chore)) {
            Person update = peopleList.get(firstName);
            update.deleteChore(choreList.get(chore));
            return update;
        }
        return null;
    }

    @GetMapping("/people/{firstName}/finishedChores")
    public int getNumberOfFinishedChores(@PathVariable String firstName) {
        if(peopleList.containsKey(firstName)){
            Person p = peopleList.get(firstName);
            return p.getCompletedChores().size();
        }
        return 0;
    }

    @PutMapping("/people/{firstName}/markComplete")
    public Person markChoreCompleted(@PathVariable String firstName, @RequestParam String chore) {
        if(peopleList.containsKey(firstName) && choreList.containsKey(chore)) {
            Person p = peopleList.get(firstName);
            Chore c = choreList.get(chore);
            p.completeChore(c);
            return p;
        }
        return null;
    }

    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // Note: To DELETE we use delete method

    @DeleteMapping("/people")
    public HashMap<String, Person> deletePerson(@RequestParam("name") String name) {
        if (peopleList.containsKey(name)) {
            peopleList.remove(name);
        }
        return peopleList;
    }

    @DeleteMapping("/chores")
    public HashMap<String, Chore> deleteChore(@RequestParam String name) {
        if (choreList.containsKey(name)) {
            choreList.remove(name);
        }
        return choreList;
    }


} // end of people controller



