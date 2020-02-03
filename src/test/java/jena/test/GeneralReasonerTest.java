package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.rulesys.GenericRuleReasonerFactory;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.ReasonerVocabulary;
import org.junit.Before;
import org.junit.Test;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class GeneralReasonerTest extends ReasonerTest {

	String demoURI = "http://jena.hpl.hp.com/demo#";

	@Before
	public void init() {
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

		// Load test data
		data = FileManager.get().loadModel("data/generalDemoData.n3", "N3");
		infmodel = ModelFactory.createInfModel(reasoner, data);
	}

	@Test
	public void statements() {
		// Query for all things related to "a" by "p"
		Resource a = data.getResource(demoURI + "a");
		Property p = data.getProperty(demoURI, "p");
		printStatements(a, p, null);
	}
}