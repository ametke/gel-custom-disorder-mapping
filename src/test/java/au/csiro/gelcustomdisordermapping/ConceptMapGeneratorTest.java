package au.csiro.gelcustomdisordermapping;

import java.io.File;
import java.io.FileNotFoundException;

import org.hl7.fhir.dstu3.model.ConceptMap;
import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;

/**
 * @author Alejandro Metke
 *
 */
public class ConceptMapGeneratorTest {
  
  @Test
  public void generateConceptMapTest() throws FileNotFoundException {
    ConceptMapGenerator gen = new ConceptMapGenerator();
    ConceptMap map = gen.generateConceptMap(new File("src/test/rare_diseases.csv"));
    String json = FhirContext
        .forDstu3()
        .newJsonParser()
          .setPrettyPrint(true)
          .encodeResourceToString(map);
    System.out.println(json);
  }
  
}
