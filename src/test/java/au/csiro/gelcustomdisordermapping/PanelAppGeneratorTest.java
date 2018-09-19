package au.csiro.gelcustomdisordermapping;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.CodeSystem;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Unit tests for the code system generator.
 * 
 * @author Alejandro Metke
 *
 */
public class PanelAppGeneratorTest {
  
  @Test
  public void testGenerateFromCsv() throws FileNotFoundException {
    PanelAppGenerator csg = new PanelAppGenerator();
    CodeSystem cs = csg.generateFromCsv(new File("src/test/panelapp_panels.tsv"));
    String json = FhirContext
        .forDstu3()
        .newJsonParser()
          .setPrettyPrint(true)
          .encodeResourceToString(cs);
    System.out.println(json);
  }
  
}
