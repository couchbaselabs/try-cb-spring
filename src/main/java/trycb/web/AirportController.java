package trycb.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import trycb.model.Error;
import trycb.model.IValue;
import trycb.service.Airport;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AirportController.class);

  private Airport airportService;

  public AirportController(Airport airportService) {
    this.airportService = airportService;
  }

  @RequestMapping
  public ResponseEntity<? extends IValue> airports(@RequestParam("search") String search) {
    try {
      return ResponseEntity.ok(airportService.findAll(search));
    } catch (Exception e) {
      e.printStackTrace();
      LOGGER.error("Failed with exception ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new Error(e.getMessage()));
    }
  }

}
