package coms309;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Random;

@RestController
class WelcomeController {

    @GetMapping("/")
    public String welcome() {
        return "arjavtri experiment 1: Welcome! its nice having you here c:";
    }
    
    @GetMapping("/{name}")
    public String welcome(@PathVariable String name) {
        return "You say: " + name + "\n. Nice to meet you!";
    }

    @GetMapping("/giveprojectidea")
    public String project() {
        ArrayList<String> ideas = new ArrayList<>();
        ideas.add("GeoLocation app, A mobile game where users are shown a random Google Maps Street View still from within a radius of their location. The challenge is to physically travel to the spot, take a photo, and verify accuracy using GPS.");
        ideas.add("Household Manager. Analytically organizes and distributes house tasks to users in a household, each with unique deadlines and repeats. Tasks can be tracked house wide, and rotated between users. Household items, groceries, and meals can be tracked and distributed. Everything is statistically tracked and can be viewed. App cosmetics can be rewarded based on completions. Introduces a new way of holding accountability.");
        ideas.add("Paw Pals. App where it show individual dog owners that you can talk and meet up with or events that are happening in the future.");
        Random random = new Random();
        int randomIndex = random.nextInt(ideas.size());
        return ideas.get(randomIndex);
    }

    @GetMapping("/search")
    public String search(@RequestParam String name) {return "Searching for " + name;}

    @GetMapping("/roll")
    public String roll(@RequestParam(defaultValue = "6") int sides) {
        int result = new Random().nextInt(sides) + 1;
        return "Rolled a " + sides + "-sided dice: " + result;
    }

    @GetMapping("/reverse")
    public String reverse(@RequestParam String word) {
        return new StringBuilder(word).reverse().toString();
    }
}
