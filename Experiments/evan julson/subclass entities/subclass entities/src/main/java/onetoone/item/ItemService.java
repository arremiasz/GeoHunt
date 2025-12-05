package onetoone.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    FoodRepository foodRepository;
    @Autowired
    ToolRepository toolRepository;

    public Item getItem(long id){
        Item item = null;
        if(toolRepository.findById(id).isPresent()){
            item = toolRepository.findById(id).get();
        }
        if(foodRepository.findById(id).isPresent()){
            item = foodRepository.findById(id).get();
        }
        return item;
    }

    public List<Item> listItem(){
        List<Item> itemList = new ArrayList<>();
        itemList.addAll(listFood());
        itemList.addAll(listTool());
        return itemList;
    }

    public List<Food> listFood(){
        List<Food> foodList = foodRepository.findAll();
        return foodList;
    }

    public List<Tool> listTool(){
        List<Tool> toolList = toolRepository.findAll();
        return toolList;
    }

}
