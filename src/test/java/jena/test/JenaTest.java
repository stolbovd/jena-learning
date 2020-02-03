package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class JenaTest {

	Model data;
	Model schema;

	String NS = "urn:x-hp:eg/";

	public void printTurtle(Model model) {
		model.write(System.out, "N3");
	}
}