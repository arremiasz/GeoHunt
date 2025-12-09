package com.geohunt.backend.Shop;

import com.geohunt.backend.Shop.DTOs.PowerupShopDTO;
import com.geohunt.backend.Shop.DTOs.ShopResponseDTO;
import com.geohunt.backend.Shop.DTOs.PowerupResponseDTO;
import com.geohunt.backend.database.AccountRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(
        name = "Shop",
        description = """
                Operations related to the GeoHunt in-game shop.  
                Includes purchasing items, equipping/unequipping cosmetics, 
                consuming powerups, and retrieving transactions.
                """
)
@RestController
@RequestMapping("/shop")
public class shopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private TransactionService transactionService;

    // ========================================================================
    // RETRIEVAL ENDPOINTS
    // ========================================================================

    @Operation(
            summary = "Retrieve a shop item by name",
            description = "Looks up a shop item using its unique name."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shop item found",
                    content = @Content(schema = @Schema(implementation = Shop.class))),
            @ApiResponse(responseCode = "404", description = "Shop item not found")
    })
    @GetMapping("/getItem")
    public ResponseEntity<Shop> getItem(
            @Parameter(description = "Name of the shop item to retrieve", example = "Cool Hat")
            @RequestParam String name) {

        return shopService.getItem(name);
    }


    @Operation(
            summary = "Retrieve a shop item by ID",
            description = "Queries the shop database for an item using its internal ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Shop item found",
                    content = @Content(schema = @Schema(implementation = Shop.class))),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @GetMapping("/getItemId")
    public ResponseEntity<Shop> getItemId(
            @Parameter(description = "Shop item ID", example = "12")
            @RequestParam long id) {

        return shopService.getItem(id);
    }


    @Operation(
            summary = "Retrieve all shop items of a given type",
            description = "Returns all items belonging to a desired category (decorations, customizations, powerups, etc.)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Matching items returned",
                    content = @Content(schema = @Schema(implementation = Shop.class))),
            @ApiResponse(responseCode = "404", description = "No items of this type exist")
    })
    @GetMapping("/getType")
    public ResponseEntity<List<Shop>> getItemType(
            @Parameter(description = "Type of item", example = "DECORATION")
            @RequestParam SHOP_ITEM_TYPE itemType) {

        return shopService.getOfType(itemType);
    }


    // ========================================================================
    // SHOP ADMINISTRATION
    // ========================================================================

    @Operation(
            summary = "Delete a shop item by name",
            description = """
                    Removes a shop item entirely.  
                    All user inventory entries referencing this item are deleted as well.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Item not found")
    })
    @DeleteMapping("/deleteItem")
    public ResponseEntity<String> deleteItem(
            @Parameter(description = "Name of the shop item to delete") @RequestParam String name) {
        return shopService.deleteItem(name);
    }


    @Operation(
            summary = "Add a non-powerup shop item",
            description = """
                    Creates a normal shop item such as a decoration or profile customization.  
                    Cannot be used to add powerups (use /addPowerup instead).
                    """,
            requestBody = @RequestBody(
                    required = true,
                    description = "Shop object to add",
                    content = @Content(
                            schema = @Schema(implementation = Shop.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "name": "Cool Hat",
                                      "description": "A stylish cosmetic hat.",
                                      "image": "image_base64_here",
                                      "itemType": "DECORATION",
                                      "price": 150.0
                                    }
                                    """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item created"),
            @ApiResponse(responseCode = "409", description = "Item name already exists")
    })
    @PostMapping("/addItem")
    public ResponseEntity<Shop> addItem(@RequestBody Shop shop) {
        return shopService.addItem(shop);
    }


    @Operation(
            summary = "Add a powerup shop item",
            description = """
                    Creates a shop entry that grants a powerup when purchased.  
                    The powerup must already exist in the Powerup database.
                    """,
            requestBody = @RequestBody(
                    required = true,
                    description = "DTO linking a powerup to a shop item",
                    content = @Content(
                            schema = @Schema(implementation = PowerupShopDTO.class),
                            examples = @ExampleObject(
                                    value = """
                                    {
                                      "shopName": "General Hint Pack",
                                      "description": "Unlocks a general hint powerup",
                                      "image": "base64_here",
                                      "price": 50.0,
                                      "powerupName": "General Hint"
                                    }
                                    """
                            )
                    )
            )
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Powerup shop item created"),
            @ApiResponse(responseCode = "409", description = "Shop item already exists or powerup already linked"),
            @ApiResponse(responseCode = "404", description = "Powerup not found")
    })
    @PostMapping("/addPowerup")
    public ResponseEntity<Shop> addPowerup(@RequestBody PowerupShopDTO powerupDTO) {
        return shopService.addPowerupItem(powerupDTO);
    }


    // ========================================================================
    // INVENTORY ACTIONS (EQUIP, UNEQUIP, CONSUME POWERUPS)
    // ========================================================================

    @Operation(
            summary = "Equip an item or consume a powerup",
            description = """
                    - Decorations / cosmetics → marked as equipped  
                    - Powerups → consumed and removed from inventory  
                    
                    Returns either:
                    • ShopResponseDTO (for equippable items)  
                    • PowerupResponseDTO (for consumed powerups)
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item equipped or powerup consumed",
                    content = @Content(schema = @Schema(oneOf = {
                            PowerupResponseDTO.class,
                            ShopResponseDTO.class
                    }))
            ),
            @ApiResponse(responseCode = "404", description = "User or item not found"),
            @ApiResponse(responseCode = "409", description = "Item already equipped or invalid state")
    })
    @PutMapping("/equip")
    public ResponseEntity equip(
            @Parameter(description = "Name of the shop item") @RequestParam String itemName,
            @Parameter(description = "User ID") @RequestParam long uid) {
        return shopService.equip(itemName, uid);
    }


    @Operation(
            summary = "Unequip an item",
            description = """
                    Only applies to decorations/profile customizations.  
                    Powerups **cannot** be unequipped.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item unequipped"),
            @ApiResponse(responseCode = "404", description = "Item or user not found"),
            @ApiResponse(responseCode = "409", description = "Item already unequipped or is a powerup")
    })
    @PutMapping("/unequip")
    public ResponseEntity unequip(
            @Parameter(description = "Item name") @RequestParam String itemName,
            @Parameter(description = "User ID") @RequestParam long uid) {

        return shopService.unequip(itemName, uid);
    }


    @Operation(
            summary = "Consume a powerup directly",
            description = """
                    Removes one copy of a powerup from the user's inventory  
                    and grants the user the corresponding Powerup object.
                    """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Powerup consumed",
                    content = @Content(schema = @Schema(implementation = PowerupResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "User or powerup not found"),
            @ApiResponse(responseCode = "400", description = "Item is not a powerup")
    })
    @GetMapping("/usePowerup")
    public ResponseEntity usePowerup(
            @Parameter(description = "Powerup shop item name") @RequestParam String itemName,
            @Parameter(description = "User ID") @RequestParam long uid) {

        return shopService.userPowerup(itemName, uid);
    }


    // ========================================================================
    // TRANSACTIONS
    // ========================================================================


    @Operation(
            summary = "Get a specific transaction by ID",
            description = "Returns full transaction details including price, timestamp, user, and item."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction found",
                    content = @Content(schema = @Schema(implementation = Transactions.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @GetMapping("/transactions/by-id")
    public ResponseEntity getTransaction(
            @Parameter(description = "Transaction ID") @RequestParam long tid) {
        return transactionService.getTransactionById(tid);
    }


    @Operation(
            summary = "Get all transactions for a specific user",
            description = "Returns a list of all purchases made by a user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions returned",
                    content = @Content(schema = @Schema(implementation = Transactions.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/transactions/by-user")
    public ResponseEntity getTransactions(
            @Parameter(description = "User ID") @RequestParam long uid) {
        return transactionService.getUsersTransactions(uid);
    }


    @Operation(
            summary = "Delete a transaction (DEV ONLY)",
            description = "Removes a transaction from the system. For debugging only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction deleted")
    })
    @DeleteMapping("/transactions")
    public void deleteTransaction(
            @Parameter(description = "Transaction ID (DEV)") @RequestParam long tid) {
        transactionService.deleteTransaction(tid);
    }

    @Operation(
            summary = "Get all shop items grouped by type",
            description = """
            Returns a categorized list of all shop items, grouped by:
            
            • DECORATION  
            • PROFILE_CUSTOMIZATION  
            • POWERUP  
            • OTHER  
            
            Only types that have at least one item will appear in the response.
            Returns 404 if no shop items exist at all.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved all shop items grouped by type",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Shop.class),
                            examples = @ExampleObject(
                                    value = """
                                {
                                  "DECORATION": [
                                    {
                                      "id": 1,
                                      "name": "Blue Trail Banner",
                                      "description": "A decorative banner",
                                      "image": "base64string",
                                      "itemType": "DECORATION",
                                      "price": 150
                                    }
                                  ],
                                  "POWERUP": [
                                    {
                                      "id": 3,
                                      "name": "General Hint Powerup",
                                      "description": "Reveals the city of the challenge",
                                      "image": "base64string",
                                      "itemType": "POWERUP",
                                      "price": 500
                                    }
                                  ]
                                }
                                """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No shop items exist in the database",
                    content = @Content
            )
    })
    @GetMapping("/all")
    public ResponseEntity<Map<SHOP_ITEM_TYPE, List<Shop>>> getAllItems() {
        return shopService.getAllItems();
    }

    @Operation(
            summary = "Purchase an item from the shop",
            description = """
                Allows a user to purchase a shop item using in-game points.
                
                Rules:
                • User must have enough points  
                • User cannot repurchase non-powerup items 
                • A transaction entry is automatically created  
                • Powerups are immediately added to the user’s account  
                
                Returns a success message with the transaction ID.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Purchase successful",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "\"Successfully purchased! Item transaction id: 42\""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account or shop item not found",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User already owns this item (non-powerups cannot be duplicated)"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "User does not have enough points"
            )
    })
    @GetMapping("/purchase")
    public ResponseEntity purchase(
            @Parameter(description = "User ID of the buyer", example = "10", required = true)
            @RequestParam long uid,

            @Parameter(description = "Shop item ID being purchased", example = "5", required = true)
            @RequestParam long shopId
    ) {
        return shopService.purchase(uid, shopId);
    }


}
