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

    @GetMapping("/people/chores")
    public List<String> getChoresOfPerson(@RequestParam("name") String name){
        List<String> res = new ArrayList<>();
        for (Person p : peopleList.values()) {
            if(p.getFirstName().contains(name) || p.getLastName().contains(name)){
                return p.getChores();

            }
        }
        res.add("not found");
        return res;
    }

    @GetMapping("/people/completedNumber")
    public int getNumberOfChoresFinished(@RequestParam("name") String name){
        for (Person p : peopleList.values()) {
            if(p.getFirstName().contains(name) || p.getLastName().contains(name)){
                return p.getNumChoresFinished();
            }
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

    @PutMapping("/people/{firstName}/addChore") //works
    public Person updateChore(@PathVariable String firstName, @RequestBody String chore) {
        if(peopleList.containsKey(firstName)){
            Person p = peopleList.get(firstName);
            p.addChore(chore);
            return p;
        }
        return null;
    }

    @PutMapping("/people/{firstName}/deleteChore")
    public Person deleteChore(@PathVariable String firstName, @RequestBody String chore) {
        if(peopleList.containsKey(firstName)){
            Person p = peopleList.get(firstName);
            p.deleteChore(chore);
            return p;
        }
        return null;
    }

    @GetMapping("/people/{firstName}/finishedChores")
    public int getNumberOfFinishedChores(@PathVariable String firstName) {
        if(peopleList.containsKey(firstName)){
            Person p = peopleList.get(firstName);
            return p.getNumChoresFinished();
        }
        return -1;
    }

    // THIS IS THE DELETE OPERATION
    // Springboot gets the PATHVARIABLE from the URL
    // We return the entire list -- converted to JSON
    // Note: To DELETE we use delete method
    
    @DeleteMapping("/people/{firstName}")
    public HashMap<String, Person> deletePerson(@PathVariable String firstName) {
        peopleList.remove(firstName);
        return peopleList;
    }
} // end of people controller

