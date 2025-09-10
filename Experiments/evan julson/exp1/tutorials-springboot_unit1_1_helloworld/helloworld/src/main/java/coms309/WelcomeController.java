package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "Hello and welcome to COMS 309";
    }

    @GetMapping("/Postman")
    public String welcomePostman(){
        return "Hello Postman API";
    }

    @GetMapping("/Class/{number}")
    public String welcome(@PathVariable int number){
        return "Hello and welcome to COMS" + number;
    }
    
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "Hello and welcome to COMS 309: " + name;
    }
}
