
package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.ReasonerVocabulary;
import org.junit.Before;
import org.junit.Test;

/**
 * Code based on examples from the documentation: https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class GenericReasonerTest extends ReasonerTest {

	final String demoURI = "http://jena.hpl.hp.com/demo#";
	final String eg = "urn:x-hp:eg/";

	@Before
	public void init() {
		// Load test data
		data = FileManager.get().loadModel("data/genericDemoData.n3", "N3");
	}

	@Test
	public void statements() {
		// Register a namespace for use in the demo
		PrintUtil.registerPrefix("demo", demoURI);
		// Create an (RDF) specification of a hybrid reasoner which
		// loads its data from an external file.
		schema = ModelFactory.createDefaultModel();
		Resource configuration =  schema.createResource();
		configuration.addProperty(ReasonerVocabulary.PROPruleMode, "hybrid");
		configuration.addProperty(ReasonerVocabulary.PROPruleSet,  "data/demo.rules");

		// Create an instance of such a reasoner
		reasoner = GenericRuleReasonerFactory.theInstance().create(configuration);
		infmodel = ModelFactory.createInfModel(reasoner, data);

		// Query for all things related to "a" by "p"
		Resource a = data.getResource(demoURI + "a");
		Property p = data.getProperty(demoURI, "p");
		printStatements(a, p, null);
	}

	@Test
	public void concatenation() {
		// Register a namespace for use in the demo
		PrintUtil.registerPrefix("eg", eg);
		String rules =
				"[r1: (?c eg:concatFirst ?p), (?c eg:concatSecond ?q) -> " +
						"     [r1b: (?x ?c ?y) <- (?x ?p ?z) (?z ?q ?y)] ]";
		reasoner = new GenericRuleReasoner(Rule.parseRules(rules));
		infmodel = ModelFactory.createInfModel(reasoner, data);
		System.out.println("A *  =>");
		Resource A = data.getResource(eg + "A");
		printStatements(A, null, null);
	}
}