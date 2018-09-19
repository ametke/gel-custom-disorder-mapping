package au.csiro.gelcustomdisordermapping;

import ca.uhn.fhir.context.FhirContext;

import java.io.File;
import java.io.FileNotFoundException;

import org.hl7.fhir.dstu3.model.CodeSystem;
import org.junit.Test;

/**
 * Unit tests for the code system generator.
 * 
 * @author Alejandro Metke
 *
 */
public class CodeSystemGeneratorTest {
  
  @Test
  public void testGenerateFromCsv() throws FileNotFoundException {
    CodeSystemGenerator csg = new CodeSystemGenerator();
    CodeSystem cs = csg.generateFromCsv(new File("src/test/rare_diseases.csv"));
    String json = FhirContext
        .forDstu3()
        .newJsonParser()
          .setPrettyPrint(true)
          .encodeResourceToString(cs);
    System.out.println(json);
  }
  
}
