package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.PrintUtil;
import org.junit.Test;

import java.util.Iterator;
import java.util.Optional;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class ReasonerTest extends JenaTest {

	InfModel infmodel;
	Reasoner reasoner;

	public void printStatements(Resource s, Property p, Resource o) {
		printStatements(infmodel.listStatements(s, p, o));
	}

	public void printStatements(StmtIterator stmtIterator) {
		while(stmtIterator.hasNext()) {
			System.out.println(" - " + PrintUtil.print(stmtIterator.nextStatement()));
		}
	}

	public void printValidityReport(InfModel infModel) {
		ValidityReport validity = infModel.validate();
		if (validity.isValid()) {
			System.out.println("OK");
		} else {
			System.out.println("Conflicts");
			Iterator<ValidityReport.Report> i = validity.getReports();
			while (i.hasNext()) {
				System.out.println(" - " + i.next());
			}
		}
	}

	@Test
	public void infModelValidity() {
		Optional.ofNullable(infmodel).ifPresent(this::printValidityReport);
	}
}