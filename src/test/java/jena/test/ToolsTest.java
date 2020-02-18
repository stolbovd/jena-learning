package jena.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.jena.util.FileManager;

/**
 * Code has based on https://jena.apache.org/documentation/inference/
 */
@Slf4j
public class ToolsTest extends JenaTest {

//	@Test
	public void xmlToN3Test() {
		data = FileManager.get().loadModel("data/temp.xml");
		printTurtle(data);
	}

//	@Test
	public void n3Test() {
		data = FileManager.get().loadModel("data/temp.n3", "N3");
		printTurtle(data);
	}
}