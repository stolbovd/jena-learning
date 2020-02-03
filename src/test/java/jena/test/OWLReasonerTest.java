package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class OWLReasonerTest extends ReasonerTest {

	@Before
	public void init() {
		schema = FileManager.get().loadModel("data/owlDemoSchema.n3","N3");
		data = FileManager.get().loadModel("data/owlDemoData.n3", "N3");
		reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(schema);
		infmodel = ModelFactory.createInfModel(reasoner, data);
	}

	@Test
	public void nForce() {
		Resource nForce = infmodel.getResource(NS + "nForce");
		System.out.println("nForce2 *:");
		printStatements(nForce, null, null);
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