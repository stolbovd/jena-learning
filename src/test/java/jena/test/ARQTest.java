package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class ARQTest extends JenaTest {

	final String prefix = "PREFIX vcard: <http://www.w3.org/2001/vcard-rdf/3.0#>\n" +
			"PREFIX info: <http://somewhere/peopleInfo#>\n" +
			"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	String selectQuery = prefix + "SELECT ?s\n" +
			"WHERE { ?s  vcard:FN  \"John Smith\" }";
	String constructQuery = prefix + "CONSTRUCT { ?s vcard:FN \"Hello Construct\" }\n" +
			"WHERE { ?s  vcard:FN  \"John Smith\" }";
	String describeQuery = prefix + "DESCRIBE ?s ?p ?o \n" +
			"WHERE { ?s  vcard:FN  \"John Smith\" }";

	@Before
	public void init() {
		data = FileManager.get().loadModel("data/vc-db-2.n3", "N3");
	}

	@Test
	public void queryTest() {
		Query query = QueryFactory.create(selectQuery);
		try (QueryExecution qexec = QueryExecutionFactory.create(query, data)) {
			ResultSet results = qexec.execSelect();
			printResulSet(results);
		}
	}

	@Test
	public void runQueryTest() {
		printResulSet(runQuery(selectQuery));
	}

	private void printResulSet(ResultSet results) {
		for (; results.hasNext(); ) {
			QuerySolution soln = results.nextSolution();
			RDFNode x = soln.get("?s");       // Get a result variable by name.
			Resource r = soln.getResource("VarR"); // Get a result variable - must be a resource
			Literal l = soln.getLiteral("VarL");   // Get a result variable - must be a literal
			System.out.println(" - " + soln + " (RDFNode:" + x + ", Resource:" + r +
					", Literal" + ":" + l + ")");
		}
	}

	@Test
	public void formattingResultsTest() {
		ResultSet results = runQuery(selectQuery);
		ResultSetFormatter.out(System.out, results);
	}

	@Test
	public void processingResultsTest() {
		ResultSet results = runQuery(selectQuery);
		for (; results.hasNext(); ) {
			QuerySolution soln = results.nextSolution();
			// Access variables: soln.get("x") ;
			RDFNode n = soln.get("s"); // "x" is a variable in the query
			// If you need to test the thing returned
			if (n.isLiteral())
				((Literal) n).getLexicalForm();
			if (n.isResource()) {
				Resource r = (Resource) n;
				if (!r.isAnon()) {
					System.out.println("s:" + r + " URI:" + r.getURI());
				}
			}
		}
	}

	private ResultSet runQuery(String queryString) {
		try (QueryExecution qexec = QueryExecutionFactory.create(queryString, data)) {
			ResultSet results = qexec.execSelect();
			results = ResultSetFactory.copyResults(results);
			return results;    // Passes the result set out of the try-resources
		}
	}

	@Test
	public void constructQueryTest() {
		System.out.println(constructQuery);
		Query query = QueryFactory.create(constructQuery);
		System.out.println(query.isSelectType() ? "execSelect, deal with results" :
				(query.isConstructType() ? "execConstruct, deal with result model" :
						"do you deal with DESCRIBE?"));
		QueryExecution qexec = QueryExecutionFactory.create(query, data);
		Model resultModel = null;
		if (query.isSelectType())
			printResulSet(qexec.execSelect());
		else if (query.isConstructType())
			resultModel = qexec.execConstruct();
		else
			resultModel = qexec.execDescribe();
		qexec.close();
		Optional.ofNullable(resultModel).ifPresent(System.out::println);
	}

	@Test
	public void solutionsQueryTest() {
		runAndFormattingQuery("SELECT ?s ?fname\n" +
				"WHERE {?s vcard:FN ?fname}");
	}

	@Test
	public void basicPatternsQueryTest() {
		runAndFormattingQuery("SELECT ?s ?givenName\n" +
				"WHERE\n" +
				" { ?s vcard:Family \"Smith\" .\n" +
				"   ?s vcard:Given  ?givenName .\n" +
				" }");
	}

	@Test
	public void stringMatchingTest() {
		runAndFormattingQuery("SELECT ?g\n" +
				"WHERE\n" +
				"{ ?s vcard:Given ?g .\n" +
				"  FILTER regex(?g, \"r\", \"i\") }");
	}

	@Test
	public void testingValuesTest() {
		runAndFormattingQuery("SELECT ?resource\n" +
				"WHERE\n" +
				"  {\n" +
				"    ?resource info:age ?age .\n" +
				"    FILTER (?age >= 24)\n" +
				"  }");
	}

	@Test
	public void optionalsTest() {
		runAndFormattingQuery("SELECT ?name ?age\n" +
				"WHERE\n" +
				"{\n" +
				"    ?person vcard:FN  ?name .\n" +
				"    ?person info:age ?age .\n" +
				"}");
		runAndFormattingQuery("SELECT ?name ?age\n" +
				"WHERE\n" +
				"{\n" +
				"    ?person vcard:FN  ?name .\n" +
				"    OPTIONAL { ?person info:age ?age }\n" +
				"}");
	}

	@Test
	public void optionalsWithFiltersTest() {
		runAndFormattingQuery("SELECT ?name ?age\n" +
				"WHERE\n" +
				"{\n" +
				"    ?person vcard:FN ?name .\n" +
				"    OPTIONAL { ?person info:age ?age . FILTER ( ?age > 24 ) }\n" +
				"}");
		runAndFormattingQuery("SELECT ?name ?age\n" +
				"WHERE\n" +
				"{\n" +
				"    ?person vcard:FN ?name .\n" +
				"    OPTIONAL { ?person info:age ?age . }\n" +
				"    FILTER ( !bound(?age) || ?age > 24 )\n" +
				"}");
	}

	@Test
	public void optionalsAndOrderDependentTest() {
		runAndFormattingQuery("SELECT ?name\n" +
				"WHERE\n" +
				"{\n" +
				"  ?x a foaf:Person .\n" +
				"  OPTIONAL { ?x foaf:name ?name }\n" +
				"  OPTIONAL { ?x vcard:FN  ?name }\n" +
				"}");
	}

	//ToDo https://jena.apache.org/tutorials/sparql_union.html
	//ToDo https://jena.apache.org/tutorials/sparql_datasets.html
	//ToDo https://jena.apache.org/tutorials/sparql_results.html
	//ToDo https://jena.apache.org/documentation/query/extension.html

	public void runAndFormattingQuery(String query) {
		System.out.println(query);
		ResultSet results = runQuery(prefix + query);
		ResultSetFormatter.out(System.out, results);
	}
}