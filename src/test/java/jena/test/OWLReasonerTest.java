package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;

import static org.junit.Assert.assertTrue;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class OWLReasonerTest {

	Model schema;
	Model data;
	InfModel infmodel;
	Reasoner reasoner;

	String NS = "urn:x-hp:eg/";

	@Before
	public void init() {
		schema = FileManager.get().loadModel("data/owlDemoSchema.n3","N3");
		data = FileManager.get().loadModel("data/owlDemoData.n3", "N3");
		reasoner = ReasonerRegistry.getOWLReasoner();
		reasoner = reasoner.bindSchema(schema);
		infmodel = ModelFactory.createInfModel(reasoner, data);
	}

	@Test
	public void derivations() {
		Resource nForce = infmodel.getResource(NS + "nForce");
		System.out.println("nForce2 *:");
		printStatements(infmodel, nForce, null, null);
	}

	public void printStatements(Model m, Resource s, Property p, Resource o) {
		for (StmtIterator i = m.listStatements(s,p,o); i.hasNext(); ) {
			Statement stmt = i.nextStatement();
			System.out.println(" - " + PrintUtil.print(stmt));
		}
	}

	@Test
	public void printTurtle() {
		schema.write(System.out, "N3");
	}

	@Test
	public void infModelContains() {
		Resource gamingComputer = infmodel.getResource("urn:x-hp:eg/GamingComputer");
		Resource whiteBox = infmodel.getResource("urn:x-hp:eg/whiteBoxZX");
		// White box recognized as gaming computer
		assertTrue(infmodel.contains(whiteBox, RDF.type, gamingComputer));
	}

	@Test
	public void infModelValidity() {
		printValidityReport(infmodel);
	}

	public void printValidityReport(InfModel infModel) {
		ValidityReport validity = infModel.validate();
		if (validity.isValid()) {
			System.out.println("OK");
		} else {
			System.out.println("Conflicts");
			for (Iterator i = validity.getReports(); i.hasNext(); ) {
				ValidityReport.Report report = (ValidityReport.Report)i.next();
				System.out.println(" - " + report);
			}
		}
	}
}