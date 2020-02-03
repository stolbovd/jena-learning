package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class RDFSReasonerTest extends ReasonerTest {

	@Before
	public void init() {
		schema = FileManager.get().loadModel("data/rdfsDemoSchema.n3", "N3");
		data = FileManager.get().loadModel("data/rdfsDemoData.n3", "N3");
		infmodel = ModelFactory.createRDFSModel(schema, data);
	}

	@Test
	public void objectTypes() {
		Resource colin = infmodel.getResource(NS + "colin");
		System.out.println("colin has types:");
		printStatements(colin, RDF.type, null);

		Resource person = infmodel.getResource(NS + "Person");
		System.out.println("\nPerson has types:");
		printStatements(person, RDF.type, null);
	}
}