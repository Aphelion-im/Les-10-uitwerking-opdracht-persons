// RequestBody te gebruiken bij @PutMapping en @PostMapping
// Delete person by name & Search probleem: - Uitwerking met @RequestBody i.p.v. RequestParam, maar dat lukt niet in Postman
// Uitwerkingen: https://github.com/hogeschoolnovi/backend-spring-boot-lesopdracht-les10/blob/uitwerkingen/les10/src/main/java/com/example/les10/controller/PersonController.java
package nl.eliascc.les10.controller;

import nl.eliascc.les10.model.Person;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/persons")
// Alle paden (In Postman) beginnen met /persons - Change base URL to: http://localhost:8080/persons - Optioneel: Onder @RestController plaatsen
public class PersonController {
    private final List<Person> persons = new ArrayList<>();

    public PersonController() { // Constructor. Maak een Person object aan en voeg deze toe aan de ArrayList persons
        Person p = new Person();
        p.name = "Pietje";
        p.dob = LocalDate.of(1995, Month.AUGUST, 8);
        p.gender = 'm';
        this.persons.add(p);
    }

    // Waarschijnlijk kan deze notatie ook: @GetMapping("")
    @GetMapping // Omdat de BaseURL al op /persons staat via de bovenstaande @RequestMapping, laat je deze leeg.
    // Vraag de persons lijst op
    public ResponseEntity<List<Person>> getPersons() { // ResponseEntity: Robert-Jan: "Een response entity is zeg maar een hulpmiddel om vanuit jouw code, jouw endpoint, iets te gaan retourneren naar de client. Het is een handige klasse/object om zowel een body terug te geven als ook een http status."
        return new ResponseEntity<>(persons, HttpStatus.OK); // ResponseEntity<>(Body, HttpStatus.OK). HttpStatus.OK = 200 OK
    }

    @PostMapping
    public ResponseEntity<Person> createPerson(@RequestBody Person p) { // @RequestBody - Wat er in Body JSON komt te staan: Person p. Wordt gebruikt voor POST-requests en PUT-requests om daadwerkelijk data mee te sturen. Deze annotatie stelt je dus in staat om complete objecten mee te sturen in een HTTP request. In Postman: Body > Raw > Json
        persons.add(p);
        return new ResponseEntity<>(p, HttpStatus.CREATED); // HttpStatus.CREATED. Geeft: 201 Created
    }

    // Update a person
    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePerson(@RequestBody Person p, @PathVariable int id) { // @PathVariable - of long id - Om een specifiek item, met een specifiek id, ophalen uit de database. Postman: Zowel een param in de url als een Body
        if (id >= 0 && id < persons.size()) {
            persons.set(id, p); // ArrayList.set() --> Replaces value at given index with given value....
            return new ResponseEntity<>(p, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid ID", HttpStatus.NOT_FOUND); // HttpStatus.BAD_REQUEST. Postman geeft: 400 Bad request.
        }
    }

    // Opdrachten Persons:
    // GET method die de zoveelste persoon uit de lijst returned
    @GetMapping("/{index}")
    public ResponseEntity<Person> getPerson(@PathVariable int index) {
        // Gebruik de ArrayList.get() methode om de Person uit de lijst te halen
        if (index >= 0 && index < persons.size()) {
            Person person = persons.get(index);
            //  return ResponseEntity.ok(person); // HTTPStatus 200 OK
            return new ResponseEntity<>(person, HttpStatus.FOUND); // HTTPStatus 302 Found
        } else {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404 Not found zonder Body bericht (null)
        }
    }

    // Delete person with index#
    @DeleteMapping("/{index}")
    public ResponseEntity<Person> deletePerson(@PathVariable int index) {
        if (index > 0 && index < persons.size()) {
            persons.remove(index);
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT); // 204 No content
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND); // 404 Not found
    }

    // Delete person by name - Uitwerking met @RequestBody ipv RequestParam, maar dat lukt niet in Postman
    // Kan een NullPointerException geven als een entry waarden met null heeft
    @DeleteMapping()
    public ResponseEntity<?> deletePerson(@RequestBody String name) { // <?> Waarschijnlijk een Optional: An Optional is a container that either has something in it or doesn't.
        // Loop door de persons List heen.
        for (Person p : persons) {
            // Kijk of er een Person in de lijst staat met de gegeven naam
            if (p.name.equals(name)) {
                // Zo ja, verwijder deze persoon en return met HttpStatus 204
                persons.remove(p);
                return ResponseEntity.noContent().build();
                // return ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            }
        }
        // Staat er geen Persoon met de gegeven naam in de List, return dan HttpStatus 404
        // return ResponseEntity.notFound().build(); // Oude manier van noteren?
        return new ResponseEntity<>("Person not found", HttpStatus.NOT_FOUND); // 404 Not found
    }


    //Extra. Hetzelfde probleem met RequestBody & RequestParam in Postman
    @GetMapping("/search")
    public ResponseEntity<List<Person>> getPersonsContaining(@RequestBody String name) { // @RequestBody --> @RequestParam
        // Maak een Lijst waarin je de gevonden Persons kunt verzamelen
        List<Person> aggregator = new ArrayList<>();
        // Loop door de lijst om Persons waarvan de naam, of een gedeelte van de naam, overeenkomt met de parameter
        for (Person p : persons) {
            if (p.name.contains(name)) {
                // Voeg de gevonden persoon toe aan de List
                aggregator.add(p);
            }
        }
        if (aggregator.isEmpty()) {   // Arraylist.isEmpty(); True/False.
        // return ResponseEntity.notFound().build();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        // return ResponseEntity.ok(aggregator);
        return new ResponseEntity<>(aggregator, HttpStatus.FOUND);
    }


}
