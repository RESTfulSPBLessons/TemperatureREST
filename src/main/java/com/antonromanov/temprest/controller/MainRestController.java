package com.antonromanov.temprest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Основной REST-контроллер приложения.
 */
@RestController
@RequestMapping("/rest/users")
public class MainRestController {

	@GetMapping("/today")
	public ResponseEntity<String> getTodayMeasures() {
		return ResponseEntity.ok("Ok!");
	}


}
