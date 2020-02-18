package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class OWL2ReasonerTest extends ReasonerTest {

	final String URI = "http://example.com/owl/families/";

	@Before
	public void init() {
		PrintUtil.registerPrefix("", URI);
		PrintUtil.registerPrefix("otherOnt", "http://example.org/otherOntologies/families/");

		data = FileManager.get().loadModel("data/primer.ttl", "TTL");
		reasoner = ReasonerRegistry.getOWLReasoner();
		infmodel = ModelFactory.createInfModel(reasoner, data);
	}

	@Test
	public void printStatementsByResource() {
		Resource subject = infmodel.getResource(URI + "Susan");
		Property predicate = infmodel.getProperty(URI + "atTeenager");
		Resource object = infmodel.getResource(URI + "Teenager");
//		printStatements(subject, predicate, object);
		printStatements(subject, null, null);
	}

	@Test
	public void printTurtle() {
		printTurtle(schema);
	}

	@Test
	public void infModelContains() {
		Resource gamingComputer = infmodel.getResource("urn:x-hp:eg/GamingComputer");
		Resource whiteBox = infmodel.getResource("urn:x-hp:eg/whiteBoxZX");
		// White box recognized as gaming computer
		assertTrue(infmodel.contains(whiteBox, RDF.type, gamingComputer));
	}
}