package onetoone.person;

import onetoone.item.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PersonController {
    @Autowired
    ItemService itemService;

    @Autowired
    PersonRepository personRepository;
}
