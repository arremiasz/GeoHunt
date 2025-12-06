package onetoone.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    public Item getItem(long id){
        if(itemRepository.findById(id).isPresent()){
            return itemRepository.findById(id).get();
        }
        return null;
    }

    public List<Item> listItem(){
        List<Item> itemList = itemRepository.findAll();
        return itemList;
    }

    public List<Food> listFood(){
        List<Item> itemList = itemRepository.findAll();
        List<Food> foodList = new ArrayList<>();
        for(Item item : itemList){
            if(item instanceof Food){
                foodList.add((Food) item);
            }
        }
        return foodList;
    }

    public List<Tool> listTool(){
        List<Item> itemList = itemRepository.findAll();
        List<Tool> toolList = new ArrayList<>();
        for(Item item : itemList){
            if(item instanceof Tool){
                toolList.add((Tool) item);
            }
        }
        return toolList;
    }

}
