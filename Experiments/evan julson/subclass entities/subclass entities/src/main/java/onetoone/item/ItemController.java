package onetoone.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {
    @Autowired
    FoodRepository foodRepository;

    @Autowired
    ToolRepository toolRepository;

    @PostMapping("/items/food")
    public Item createFood(@RequestBody Food food){
        foodRepository.save(food);
        return food;
    }

    @PostMapping("/items/tool")
    public Item createTool(@RequestBody Tool tool){
        toolRepository.save(tool);
        return tool;
    }

    @GetMapping("/items/{id}")
    public Item getItem(@PathVariable Long id){
        Item item = null;
        if(toolRepository.findById(id).isPresent()){
            item = toolRepository.findById(id).get();
        }
        if(foodRepository.findById(id).isPresent()){
            item = foodRepository.findById(id).get();
        }
        return item;
    }
}
