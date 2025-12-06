package onetoone.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ItemController {
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemService itemService;

    @PostMapping("/items/food")
    public Item createFood(@RequestBody Food food){
        itemRepository.save(food);
        return food;
    }

    @PostMapping("/items/tool")
    public Item createTool(@RequestBody Tool tool){
        itemRepository.save(tool);
        return tool;
    }

    @GetMapping("/items/{id}")
    public Item getItem(@PathVariable Long id){
        return itemService.getItem(id);
    }

    @GetMapping("/items")
    public List<Item> listItems(){
        return itemService.listItem();
    }

    @GetMapping("/items/food")
    public List<Food> listFood(){
        return itemService.listFood();
    }

    @GetMapping("/items/tool")
    public List<Tool> listTools(){
        return itemService.listTool();
    }
}
