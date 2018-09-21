package au.csiro.gelcustomdisordermapping;

import ca.uhn.fhir.context.FhirContext;

import java.io.FileNotFoundException;

import org.hl7.fhir.dstu3.model.ConceptMap;
import org.junit.Test;


/**
 * Unit tests for {@link ConceptMapGenerator}.
 * 
 * @author Alejandro Metke
 *
 */
public class ConceptMapGeneratorTest {
  
  @Test
  public void generateDiseaseToHpoMapTest() throws FileNotFoundException {
    ConceptMapGenerator gen = new ConceptMapGenerator();
    ConceptMap map = gen.generateDiseaseToHpoMap();
    String json = FhirContext
        .forDstu3()
        .newJsonParser()
          .setPrettyPrint(true)
          .encodeResourceToString(map);
    System.out.println(json);
  }
  
  @Test
  public void generateDisorderToPanelMapTest() throws FileNotFoundException {
    ConceptMapGenerator gen = new ConceptMapGenerator();
    ConceptMap map = gen.generateDisorderToPanelMap();
    String json = FhirContext
        .forDstu3()
        .newJsonParser()
          .setPrettyPrint(true)
          .encodeResourceToString(map);
    System.out.println(json);
  }
  
}
