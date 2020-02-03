package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Derivation;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.vocabulary.RDFS;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Author is Joshua Taylor
 * https://stackoverflow.com/a/24786478/1396003
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class GeneralReasonerTest extends ReasonerTest {

	Resource A;
	Resource B;
	Resource C;
	Resource D;

	Property p;
	Property q;

	@Before
	public void init() {

		// Build a trivial example data set
		data = ModelFactory.createDefaultModel();

		A = data.createResource(NS + "A");
		B = data.createResource(NS + "B");
		C = data.createResource(NS + "C");
		D = data.createResource(NS + "D");

		p = data.createProperty(NS, "p");
		q = data.createProperty(NS, "q");

		// Some small examples (subProperty)
		data.add(p, RDFS.subPropertyOf, q);
		A.addProperty(p, "foo" );

		// Get an RDFS reasoner
		GenericRuleReasoner rdfsReasoner = (GenericRuleReasoner) ReasonerRegistry.getRDFSReasoner();
		// Steal its rules, and add one of our own, and create a
		// reasoner with these rules
		List<Rule> customRules = new ArrayList<>( rdfsReasoner.getRules() );
		String customRule = "[rule1: (?a eg:p ?b) (?b eg:p ?c) -> (?a eg:p ?c)]";
		customRules.add( Rule.parseRule( customRule ));

		Reasoner reasoner = new GenericRuleReasoner( customRules );
		reasoner.setDerivationLogging(true);
		infmodel = ModelFactory.createInfModel(reasoner, data);

		// Derivations
		A.addProperty(p, B);
		B.addProperty(p, C);
		C.addProperty(p, D);
	}

	@Test
	public void subProperty() {
		printStatements(A, q, null);
	}

	@Test
	public void listStatements() {
		printStatements(infmodel.listStatements());
	}
	@Test
	public void derivations() {
		String trace = null;
		PrintWriter out = new PrintWriter(System.out);
		for (StmtIterator i = infmodel.listStatements(A, q, D); i.hasNext(); ) {
			Statement s = i.nextStatement();
			System.out.println("Statement is " + s);
			for (Iterator<Derivation> id = infmodel.getDerivation(s); id.hasNext(); ) {
				Derivation derivation = (Derivation) id.next();
				derivation.printTrace(out, true);
				trace += derivation.toString();
			}
		}
		out.flush();
		assertNotNull(trace);
	}
}