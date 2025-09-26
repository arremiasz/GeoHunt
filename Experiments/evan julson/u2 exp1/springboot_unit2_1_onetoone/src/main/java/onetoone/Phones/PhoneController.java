package onetoone.Phones;

import onetoone.Laptops.Laptop;
import onetoone.Persons.Person;
import onetoone.Persons.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PhoneController {

    @Autowired
    PhoneRepository phoneRepository;

    @Autowired
    PersonRepository personRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path = "/Phones")
    List<Phone> getAllPhones(){
        return phoneRepository.findAll();
    }

    @GetMapping(path = "/Phones/{id}")
    Phone getPhoneById(@PathVariable int id){
        return phoneRepository.findById(id);
    }

    @PostMapping(path = "/Phones")
    String createPhone(@RequestBody Phone Phone){
        if (Phone == null)
            return failure;
        phoneRepository.save(Phone);
        return success;
    }

    @PutMapping(path = "/Phones/{id}")
    Phone updatePhone(@PathVariable int id, @RequestBody Phone request){
        Phone phone = phoneRepository.findById(id);
        if(phone == null)
            return null;
        phoneRepository.save(request);
        return phoneRepository.findById(id);
    }

    @DeleteMapping(path = "/Phones/{id}")
    String deletePhone(@PathVariable int id){

        // Check if there is an object depending on Person and then remove the dependency
        Person person = personRepository.findByPhone_Id(id);
        person.setPhone(null);
        personRepository.save(person);

        // delete the phone if the changes have not been reflected by the above statement
        phoneRepository.deleteById(id);
        return success;
    }
}
