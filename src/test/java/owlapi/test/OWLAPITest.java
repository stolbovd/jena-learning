/*
 * Copyright (c) 2020.
 */

package owlapi.test;

import com.github.owlcs.ontapi.OntManagers;
import org.junit.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.io.StreamDocumentTarget;
import org.semanticweb.owlapi.io.StringDocumentSource;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asSet;
import static org.semanticweb.owlapi.util.OWLAPIStreamUtils.asUnorderedSet;

/**
 * @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics Group
 * This sourse is from com.github.owlcs.owlapi.tests.examples.OWLAPIExamples.java
 * Thanks for Authors
 */
public class OWLAPITest {

	final String family = "http://example.com/owl/families/";

	@Test
	public void owl2Resonate() throws Exception {
		//open Ontology
		OWLOntologyManager manager = OntManagers.createONT();
		InputStream inputStream = this.getClass().getResourceAsStream("/data/family.ttl");
		OWLOntology ontology = manager.loadOntologyFromOntologyDocument(inputStream);
		OWLDataFactory dataFactory = manager.getOWLDataFactory();

		//reasoner
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();
		OWLReasoner reasoner = reasonerFactory.createReasoner(ontology);
		//isConsistent
		reasoner.precomputeInferences();
		System.out.println("Consistent: " + reasoner.isConsistent());

		//classExplored
		String explored = "HappyPerson";
		OWLClass classExplored = dataFactory.getOWLClass(family,
				explored);
//				"Teenager");
		//subClasses
		Set<OWLClass> subClasses = asSet(reasoner.subClasses(classExplored, true));
		if (!subClasses.isEmpty()) {
			System.out.println("There are subclasses of " + explored + ":");
			for (OWLClass subClass : subClasses) {
				System.out.println(" " + subClass);
			}
		}
		//individuals
		NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(classExplored, false);
		Set<OWLNamedIndividual> individuals = asSet(individualsNodeSet.entities());
		if (!individuals.isEmpty()) {
			System.out.println("There are individuals of " + explored + ":");
			for (OWLNamedIndividual ind : individuals) {
				System.out.println(" " + ind);
			}
		}
		//save inferred model
		OWLOntology exportedOntology = manager.createOntology(IRI.create(family));
		InferredOntologyGenerator generator = new InferredOntologyGenerator(reasoner);
		generator.fillOntology(dataFactory, exportedOntology);
		TurtleDocumentFormat format = new TurtleDocumentFormat();
		format.setPrefix("", family);
		OutputStream outputStream = new FileOutputStream("src/test/resources/data/family-inferred.ttl");
		manager.saveOntology(exportedOntology, format, new StreamDocumentTarget(outputStream));
		outputStream = new FileOutputStream("src/test/resources/data/family-base.ttl");
		manager.saveOntology(ontology, format, new StreamDocumentTarget(outputStream));

		//merge
		OWLOntologyMerger merger = new OWLOntologyMerger(manager);
		OWLOntology merged = merger.createMergedOntology(manager, IRI.create(family+"merged"));
		outputStream = new FileOutputStream("src/test/resources/data/family-merged.ttl");
		manager.saveOntology(merged, format, new StreamDocumentTarget(outputStream));
	}

	public static final String KOALA = "<?xml version=\"1.0\"?>\n"
			+ "<rdf:RDF xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" xmlns=\"http://protege.stanford.edu/plugins/owl/owl-library/koala.owl#\" xml:base=\"http://protege.stanford.edu/plugins/owl/owl-library/koala.owl\">\n"
			+ "  <owl:Ontology rdf:about=\"\"/>\n"
			+ "  <owl:Class rdf:ID=\"Female\"><owl:equivalentClass><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty><owl:hasValue><Gender rdf:ID=\"female\"/></owl:hasValue></owl:Restriction></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Marsupials\"><owl:disjointWith><owl:Class rdf:about=\"#Person\"/></owl:disjointWith><rdfs:subClassOf><owl:Class rdf:about=\"#Animal\"/></rdfs:subClassOf></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Student\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Person\"/><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasValue></owl:Restriction><owl:Restriction><owl:someValuesFrom><owl:Class rdf:about=\"#University\"/></owl:someValuesFrom><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"KoalaWithPhD\"><owl:versionInfo>1.2</owl:versionInfo><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Restriction><owl:hasValue><Degree rdf:ID=\"PhD\"/></owl:hasValue><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasDegree\"/></owl:onProperty></owl:Restriction><owl:Class rdf:about=\"#Koala\"/></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"University\"><rdfs:subClassOf><owl:Class rdf:ID=\"Habitat\"/></rdfs:subClassOf></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Koala\"><rdfs:subClassOf><owl:Restriction><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">false</owl:hasValue><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:someValuesFrom><owl:Class rdf:about=\"#DryEucalyptForest\"/></owl:someValuesFrom><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Animal\"><rdfs:seeAlso>Male</rdfs:seeAlso><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasHabitat\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf><owl:Restriction><owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:cardinality><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><owl:versionInfo>1.1</owl:versionInfo></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Forest\"><rdfs:subClassOf rdf:resource=\"#Habitat\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Rainforest\"><rdfs:subClassOf rdf:resource=\"#Forest\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"GraduateStudent\"><rdfs:subClassOf><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasDegree\"/></owl:onProperty><owl:someValuesFrom><owl:Class><owl:oneOf rdf:parseType=\"Collection\"><Degree rdf:ID=\"BA\"/><Degree rdf:ID=\"BS\"/></owl:oneOf></owl:Class></owl:someValuesFrom></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Student\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Parent\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Animal\"/><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty><owl:minCardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">1</owl:minCardinality></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass><rdfs:subClassOf rdf:resource=\"#Animal\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"DryEucalyptForest\"><rdfs:subClassOf rdf:resource=\"#Forest\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Quokka\"><rdfs:subClassOf><owl:Restriction><owl:hasValue rdf:datatype=\"http://www.w3.org/2001/XMLSchema#boolean\">true</owl:hasValue><owl:onProperty><owl:FunctionalProperty rdf:about=\"#isHardWorking\"/></owl:onProperty></owl:Restriction></rdfs:subClassOf><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"TasmanianDevil\"><rdfs:subClassOf rdf:resource=\"#Marsupials\"/></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"MaleStudentWith3Daughters\"><owl:equivalentClass><owl:Class><owl:intersectionOf rdf:parseType=\"Collection\"><owl:Class rdf:about=\"#Student\"/><owl:Restriction><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty><owl:hasValue><Gender rdf:ID=\"male\"/></owl:hasValue></owl:Restriction><owl:Restriction><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty><owl:cardinality rdf:datatype=\"http://www.w3.org/2001/XMLSchema#int\">3</owl:cardinality></owl:Restriction><owl:Restriction><owl:allValuesFrom rdf:resource=\"#Female\"/><owl:onProperty><owl:ObjectProperty rdf:about=\"#hasChildren\"/></owl:onProperty></owl:Restriction></owl:intersectionOf></owl:Class></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Degree\"/>\n  <owl:Class rdf:ID=\"Gender\"/>\n"
			+ "  <owl:Class rdf:ID=\"Male\"><owl:equivalentClass><owl:Restriction><owl:hasValue rdf:resource=\"#male\"/><owl:onProperty><owl:FunctionalProperty rdf:about=\"#hasGender\"/></owl:onProperty></owl:Restriction></owl:equivalentClass></owl:Class>\n"
			+ "  <owl:Class rdf:ID=\"Person\"><rdfs:subClassOf rdf:resource=\"#Animal\"/><owl:disjointWith rdf:resource=\"#Marsupials\"/></owl:Class>\n"
			+ "  <owl:ObjectProperty rdf:ID=\"hasHabitat\"><rdfs:range rdf:resource=\"#Habitat\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:ObjectProperty>\n"
			+ "  <owl:ObjectProperty rdf:ID=\"hasDegree\"><rdfs:domain rdf:resource=\"#Person\"/><rdfs:range rdf:resource=\"#Degree\"/></owl:ObjectProperty>\n"
			+ "  <owl:ObjectProperty rdf:ID=\"hasChildren\"><rdfs:range rdf:resource=\"#Animal\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:ObjectProperty>\n"
			+ "  <owl:FunctionalProperty rdf:ID=\"hasGender\"><rdfs:range rdf:resource=\"#Gender\"/><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#ObjectProperty\"/><rdfs:domain rdf:resource=\"#Animal\"/></owl:FunctionalProperty>\n"
			+ "  <owl:FunctionalProperty rdf:ID=\"isHardWorking\"><rdfs:range rdf:resource=\"http://www.w3.org/2001/XMLSchema#boolean\"/><rdfs:domain rdf:resource=\"#Person\"/><rdf:type rdf:resource=\"http://www.w3.org/2002/07/owl#DatatypeProperty\"/></owl:FunctionalProperty>\n"
			+ "  <Degree rdf:ID=\"MA\"/>\n</rdf:RDF>";

	/**
	 * An example which shows how to interact with a reasoner. In this example
	 * Pellet is used as the reasoner. You must get hold of the pellet libraries
	 * from pellet.owldl.com.
	 */
	@Test
	public void shouldUseReasoner() throws Exception {
		// Create our ontology manager in the usual way.
		OWLOntologyManager manager = OntManagers.createONT();
		OWLOntology ont = load(manager);
		// We need to create an instance of OWLReasoner. An OWLReasoner provides
		// the basic query functionality that we need, for example the ability
		// obtain the subclasses of a class etc. To do this we use a reasoner
		// factory. Create a reasoner factory. In this case, we will use HermiT,
		// but we could also use FaCT++ (http://code.google.com/p/factplusplus/)
		// or Pellet(http://clarkparsia.com/pellet) Note that (as of 03 Feb
		// 2010) FaCT++ and Pellet OWL API 3.0.0 compatible libraries are
		// expected to be available in the near future). For now, we'll use
		// HermiT HermiT can be downloaded from http://hermit-reasoner.com Make
		// sure you get the HermiT library and add it to your class path. You
		// can then instantiate the HermiT reasoner factory: Comment out the
		// first line below and uncomment the second line below to instantiate
		// the HermiT reasoner factory. You'll also need to import the
		// org.semanticweb.HermiT.Reasoner package.
//		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();
		// We'll now create an instance of an OWLReasoner (the implementation
		// being provided by HermiT as we're using the HermiT reasoner factory).
		// The are two categories of reasoner, Buffering and NonBuffering. In
		// our case, we'll create the buffering reasoner, which is the default
		// kind of reasoner. We'll also attach a progress monitor to the
		// reasoner. To do this we set up a configuration that knows about a
		// progress monitor. Create a console progress monitor. This will print
		// the reasoner progress out to the console.
		ConsoleProgressMonitor progressMonitor = new ConsoleProgressMonitor();
		// Specify the progress monitor via a configuration. We could also
		// specify other setup parameters in the configuration, and different
		// reasoners may accept their own defined parameters this way.
		OWLReasonerConfiguration config = new SimpleConfiguration(progressMonitor);
		// Create a reasoner that will reason over our ontology and its imports
		// closure. Pass in the configuration.
		OWLReasoner reasoner = reasonerFactory.createReasoner(ont, config);
//		OWLReasoner reasoner = reasonerFactory.createReasoner(ont);
		// Ask the reasoner to do all the necessary work now
		reasoner.precomputeInferences();
		// We can determine if the ontology is actually consistent (in this
		// case, it should be).
		boolean consistent = reasoner.isConsistent();
		System.out.println("Consistent: " + consistent);
		// We can easily get a list of unsatisfiable classes. (A class is
		// unsatisfiable if it can't possibly have any instances). Note that the
		// getUnsatisfiableClasses method is really just a convenience method
		// for obtaining the classes that are equivalent to owl:Nothing.
		Node<OWLClass> bottomNode = reasoner.getUnsatisfiableClasses();
		// This node contains owl:Nothing and all the classes that are
		// equivalent to owl:Nothing - i.e. the unsatisfiable classes. We just
		// want to print out the unsatisfiable classes excluding owl:Nothing,
		// and we can used a convenience method on the node to get these
		Set<OWLClass> unsatisfiable = bottomNode.getEntitiesMinusBottom();
		if (!unsatisfiable.isEmpty()) {
			System.out.println("The following classes are unsatisfiable: ");
			for (OWLClass cls : unsatisfiable) {
				System.out.println(" " + cls);
			}
		} else {
			System.out.println("There are no unsatisfiable classes");
		}
		// Now we want to query the reasoner for all descendants of Marsupial.
		// Vegetarians are defined in the ontology to be animals that don't eat
		// animals or parts of animals.
		OWLDataFactory fac = manager.getOWLDataFactory();
		// Get a reference to the vegetarian class so that we can as the
		// reasoner about it. The full IRI of this class happens to be:
		// <http://protege.stanford.edu/plugins/owl/owl-library/koala.owl#Marsupials>
		OWLClass marsupials = fac.getOWLClass("http://protege.stanford.edu/plugins/owl/owl-library/koala.owl#",
				"Marsupials");
		// Now use the reasoner to obtain the subclasses of Marsupials. We can
		// ask for the direct subclasses or all of the (proper)
		// subclasses. In this case we just want the direct ones
		// (which we specify by the "true" flag).
		NodeSet<OWLClass> subClses = reasoner.getSubClasses(marsupials, true);
		// The reasoner returns a NodeSet, which represents a set of Nodes. Each
		// node in the set represents a subclass of Marsupial. A node of
		// classes contains classes, where each class in the node is equivalent.
		// For example, if we asked for the subclasses of some class A and got
		// back a NodeSet containing two nodes {B, C} and {D}, then A would have
		// two proper subclasses. One of these subclasses would be equivalent to
		// the class D, and the other would be the class that is equivalent to
		// class B and class C. In this case, we don't particularly care about
		// the equivalences, so we will flatten this set of sets and print the
		// result
		Set<OWLClass> clses = asSet(subClses.entities());
		for (OWLClass cls : clses) {
			System.out.println(" " + cls);
		}
		// We can easily
		// retrieve the instances of a class. In this example we'll obtain the
		// instances of the class Marsupials.
		NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(marsupials, false);
		// The reasoner returns a NodeSet again. This time the NodeSet contains
		// individuals. Again, we just want the individuals, so get a flattened
		// set.
		Set<OWLNamedIndividual> individuals = asSet(individualsNodeSet.entities());
		for (OWLNamedIndividual ind : individuals) {
			System.out.println(" " + ind);
		}
		// Again, it's worth noting that not all of the individuals that are
		// returned were explicitly stated to be marsupials. Finally, we can ask
		// for
		// the property values (property assertions in OWL speak) for a given
		// individual and property.
		// Let's get all properties for all individuals
		ont.individualsInSignature().forEach(i -> ont.objectPropertiesInSignature().forEach(p -> {
			NodeSet<OWLNamedIndividual> individualValues = reasoner.getObjectPropertyValues(i, p);
			Set<OWLNamedIndividual> values = asUnorderedSet(individualValues.entities());
			System.out.println("The property values for " + p + " for individual " + i + " are: ");
			for (OWLNamedIndividual ind : values) {
				System.out.println(" " + ind);
			}
		}));
		// Finally, let's print out the class hierarchy.
		Node<OWLClass> topNode = reasoner.getTopClassNode();
		print(topNode, reasoner, 0);
	}

	/**
	 * @param manager manager
	 * @return loaded ontology
	 * @throws OWLOntologyCreationException if a problem pops up
	 */
	OWLOntology load(OWLOntologyManager manager) throws OWLOntologyCreationException {
		// in this test, the ontology is loaded from a string
		return manager.loadOntologyFromOntologyDocument(new StringDocumentSource(KOALA));
	}

	private static void print(Node<OWLClass> parent, OWLReasoner reasoner, int depth) {
		// We don't want to print out the bottom node (containing owl:Nothing
		// and unsatisfiable classes) because this would appear as a leaf node
		// everywhere
		if (parent.isBottomNode()) {
			return;
		}
		// Print an indent to denote parent-child relationships
		printIndent(depth);
		// Now print the node (containing the child classes)
		printNode(parent);
		for (Node<OWLClass> child : reasoner.getSubClasses(parent.getRepresentativeElement(), true)) {
			// Recurse to do the children. Note that we don't have to worry
			// about cycles as there are non in the inferred class hierarchy
			// graph - a cycle gets collapsed into a single node since each
			// class in the cycle is equivalent.
			print(child, reasoner, depth + 1);
		}
	}

	private static void printIndent(int depth) {
		for (int i = 0; i < depth; i++) {
			// System.out.print(" ");
		}
	}

	private static void printNode(Node<OWLClass> node) {
		// The default prefix used here is only an example.
		// For real ontologies, choose a meaningful prefix - the best
		// choice depends on the actual ontology.
		DefaultPrefixManager pm = new DefaultPrefixManager(null, null, "http://owl.man.ac.uk/2005/07/sssw/people#");
		// Print out a node as a list of class names in curly brackets
		for (Iterator<OWLClass> it = node.entities().iterator(); it.hasNext(); ) {
			OWLClass cls = it.next();
			// User a prefix manager to provide a slightly nicer shorter name
			String shortForm = pm.getShortForm(cls);
			assertNotNull(shortForm);
		}
	}
}
