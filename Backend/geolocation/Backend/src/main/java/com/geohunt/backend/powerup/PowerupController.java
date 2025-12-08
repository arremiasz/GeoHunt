package com.geohunt.backend.powerup;

import com.geohunt.backend.util.Location;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Powerups",
        description = "Operations related to creating, deleting, generating, and applying powerups in the game."
)
@RestController
@RequestMapping("/powerup")
public class PowerupController {

    @Autowired
    private PowerupService service;

    // ========================================================================
    // DEVELOPMENT-ONLY ENDPOINTS (DIRECT DB INTERACTION)
    // ========================================================================

    @Operation(
            summary = "Add a new powerup (DEV ONLY)",
            description = "Creates a new powerup entry in the database. Intended for development/testing only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Powerup created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "400", description = "Invalid powerup data", content = @Content)
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Powerup object with name, affect text, and type. Some already implemented. Please ask before adding your own.",
            required = true,
            content = @Content(
                    schema = @Schema(implementation = Powerup.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "name": "Give a hint of the general location",
                              "affect": "General hint for the challenge",
                              "type": "LOCATION_HINT_GENERAL"
                            }
                            """
                    )
            )
    )
    @PostMapping
    public ResponseEntity addPowerup(@RequestBody Powerup powerup) {
        return service.add(powerup);
    }

    @Operation(
            summary = "Delete a powerup by ID (DEV ONLY)",
            description = "Removes a powerup entry from the database. Intended for development/testing only."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Powerup deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Powerup not found")
    })
    @DeleteMapping
    public ResponseEntity removePowerup(
            @Parameter(description = "Powerup ID") @RequestParam long id) {
        return service.delete(id);
    }

    @Operation(
            summary = "Find a powerup by ID",
            description = "Fetches information about a specific powerup."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Powerup found",
                    content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "404", description = "Powerup not found")
    })
    @GetMapping("/id")
    public ResponseEntity findById(
            @Parameter(description = "Powerup ID") @RequestParam long id) {
        return service.get(id);
    }

    @Operation(
            summary = "Find a powerup by name",
            description = "Search the database for a powerup with the given name."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Powerup found",
                    content = @Content(schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "404", description = "Powerup not found")
    })
    @GetMapping("/name")
    public ResponseEntity findByName(
            @Parameter(description = "Powerup name") @RequestParam String name) {
        return service.get(name);
    }

    // ========================================================================
    // IN-GAME ENDPOINTS (USED BY THE MOBILE CLIENT)
    // ========================================================================

    @Operation(
            summary = "Generate a random powerup near the player's route",
            description = """
                    Generates a powerup between the user's starting location and the challenge's end location,
                    with a perpendicular offset determined by distance-based math. Returns the powerup type 
                    and coordinates where the powerup should spawn.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Powerup successfully generated",
                    content = @Content(schema = @Schema(implementation = RandomGenerationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Challenge not found")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "The player's current GPS location.",
            content = @Content(
                    schema = @Schema(implementation = Location.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "latitude": 42.03386,
                              "longitude": -93.642763
                            }
                            """
                    )
            )
    )
    @PostMapping("/generate")
    public ResponseEntity challenge(
            @Parameter(description = "Challenge ID") @RequestParam long chalid,
            @RequestBody Location loc) {
        return service.generate(chalid, loc);
    }

    @Operation(
            summary = "Generate a deterministic powerup location",
            description = "Creates a powerup spawn point between the player's current location and the challenge goal. " +
                    "This endpoint uses a specific powerup ID instead of picking one randomly.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Player's current GPS location",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Location.class),
                            examples = @ExampleObject(
                                    name = "PlayerLocationExample",
                                    description = "Example player location",
                                    value = "{\"latitude\": 42.03386, \"longitude\": -93.642763}\""
                            )
                    )
            )
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deterministic powerup generated",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RandomGenerationDTO.class))),
            @ApiResponse(responseCode = "404", description = "Powerup or Challenge not found", content = @Content)
    })
    @PostMapping("/DeterministicGenerate")
    public ResponseEntity challengeDeterministic(
            @Parameter(description = "Challenge ID", required = true) @RequestParam long chalid,
            @Parameter(description = "Powerup ID to use (non-random)", required = true) @RequestParam long powerupId,
            @RequestBody Location loc
    ) {
        return service.generate(chalid, loc, powerupId);
    }


    @Operation(
            summary = "Apply the effect of a collected powerup",
            description = """
                    Determines how the selected powerup affects the player:
                    
                    • Time-based powerups: calculates seconds to subtract based on distance  
                    • General hints: resolves city/county/state  
                    • Specific hints: resolves street name / neighborhood / ZIP  
                    • Other powerups: returns their custom affect text
                    
                    Returns a PowerupEffectDTO containing the effect results.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Powerup effect successfully applied",
                    content = @Content(schema = @Schema(implementation = PowerupEffectDTO.class))),
            @ApiResponse(responseCode = "404", description = "Powerup or account or challenge not found"),
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "The player's current GPS location when picking up the powerup.",
            content = @Content(
                    schema = @Schema(implementation = Location.class),
                    examples = @ExampleObject(
                            value = """
                            {
                              "latitude": 42.03411,
                              "longitude": -93.64055
                            }
                            """
                    )
            )
    )
    @PostMapping("/getAffect")
    public ResponseEntity powerupAffect(
            @Parameter(description = "Powerup ID") @RequestParam long powerupId,
            @Parameter(description = "Player account ID") @RequestParam long uid,
            @Parameter(description = "Challenge ID") @RequestParam long chalid,
            @Parameter(description = "Latitude of the powerup spawned") @RequestParam double powerupLat,
            @Parameter(description = "Longitude of the powerup spawned") @RequestParam double powerupLon,
            @RequestBody Location loc) {

        return service.deduce(
                powerupId,
                uid,
                chalid,
                loc,
                new Location(powerupLat, powerupLon)
        );
    }

    @Operation(
            summary = "Add a powerup to a user's inventory",
            description = """
                Associates a powerup with a specific user account.
                
                This creates a many-to-many relationship entry between:
                • The selected Powerup  
                • The target Account  
                
                Returns 201 if successfully added, or 404 if the powerup or account does not exist.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Powerup successfully added to user"),
            @ApiResponse(responseCode = "404", description = "Account or powerup not found", content = @Content),
    })
    @PutMapping("/add")
    public ResponseEntity addPowerup(
            @Parameter(description = "ID of the powerup to add", required = true)
            @RequestParam long powerupId,

            @Parameter(description = "ID of the user who will receive the powerup", required = true)
            @RequestParam long uid
    ) {
        return service.addToAcc(powerupId, uid);
    }


    @Operation(
            summary = "Retrieve all powerups owned by a user",
            description = """
                Returns the full set of powerups associated with a specific user.
                
                Useful for mobile clients to display the player's inventory or
                determine which powerups they can activate.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user's powerups",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Powerup.class))),
            @ApiResponse(responseCode = "500", description = "Account not found or cannot load powerups")
    })
    @GetMapping("/user")
    public ResponseEntity user(
            @Parameter(description = "ID of the user whose powerups to fetch", required = true)
            @RequestParam long uid
    ) {
        return service.getPowerupsForAccount(uid);
    }

    @Operation(
            summary = "Remove a powerup from a user's account",
            description = """
                Removes a specific powerup from the given user's inventory.
                
                This modifies the many-to-many relationship between Accounts and Powerups.
                The user must already own the powerup — otherwise a 404 is returned.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Powerup successfully removed from user",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "\"Successfully removed powerup from user\""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Account not found, powerup not found, or user does not own the powerup",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = "\"User does not own this powerup\""
                            )
                    )
            )
    })

    @DeleteMapping("/remove")
    public ResponseEntity removePowerupForAccount(
            @Parameter(
                    description = "The ID of the user whose inventory should be modified",
                    required = true
            )
            @RequestParam long uid,

            @Parameter(
                    description = "The ID of the powerup to be removed from the user",
                    required = true
            )
            @RequestParam long powerupId
    ) {
        return service.removePowerupForAccount(uid, powerupId);
    }


}
