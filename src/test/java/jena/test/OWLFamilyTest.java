package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.PrintUtil;
import org.apache.jena.vocabulary.RDF;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

@Slf4j
public class OWLFamilyTest extends ReasonerTest {

	final String family = "http://example.com/owl/families/";
	Model inferredData;

	@Before
	public void init() {
		data = FileManager.get().loadModel("data/family.ttl", "TTL");
		inferredData = FileManager.get().loadModel("data/family-inferred.ttl", "TTL");
		data.add(inferredData);
		reasoner = ReasonerRegistry.getOWLReasoner();
		infmodel = ModelFactory.createInfModel(reasoner, data);
		PrintUtil.registerPrefix("", family);
	}

	@Test
	public void infModelResource() {
		String explained = "Susan";
		Resource infModelResource = infmodel.getResource(family + explained);
		System.out.println(explained + " *:");
		printStatements(infModelResource, null, null);
	}

	@Test
	public void infModelContains() {
		Resource gamingComputer = infmodel.getResource("urn:x-hp:eg/GamingComputer");
		Resource whiteBox = infmodel.getResource("urn:x-hp:eg/whiteBoxZX");
		// White box recognized as gaming computer
		assertTrue(infmodel.contains(whiteBox, RDF.type, gamingComputer));
	}
}