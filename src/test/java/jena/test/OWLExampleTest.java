package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.junit.Before;
import org.junit.Test;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class OWLExampleTest {

	Model schema;
	Model data;
	InfModel infmodel;
	Reasoner reasoner;

	String NS = "urn:x-hp:eg/";

	@Before
	public void init() {
		schema = FileManager.get().loadModel("data/owlDemoSchema.owl");
		data = FileManager.get().loadModel("data/owlDemoData.rdf");
		reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(schema);
		infmodel = ModelFactory.createInfModel(reasoner, data);
	}

	@Test
	public void derivations() {
		Resource nForce = infmodel.getResource(NS + "nForce");
		System.out.println("nForce *:");
		printStatements(infmodel, nForce, null, null);
	}

	public void printStatements(Model m, Resource s, Property p, Resource o) {
		for (StmtIterator i = m.listStatements(s,p,o); i.hasNext(); ) {
			Statement stmt = i.nextStatement();
			System.out.println(" - " + PrintUtil.print(stmt));
		}
	}
}