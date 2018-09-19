package au.csiro.gelcustomdisordermapping;

import ca.uhn.fhir.context.FhirContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.*;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.CodeSystem.CodeSystemHierarchyMeaning;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.springframework.stereotype.Service;

/**
 * Generates a code system from a CSV file with the following format: TODO.
 * 
 * @author Alejandro Metke
 *
 */
@Service
public class CodeSystemGenerator {
  
  /**
   * Reads a CSV file and generates a FHIR code system.
   * 
   * @param csv The CSV file.
   * @throws FileNotFoundException Thrown if the file is not found.
   */
  public CodeSystem generateFromCsv(File csv) throws FileNotFoundException {
    try (Scanner sc = new Scanner(csv)) {
      
      final CodeSystem cs = new CodeSystem();
      cs.setName("GEL Recruited Disorders List");
      cs.setUrl("http://genomicsengland.co.uk/recruited-disorders");
      cs.setHierarchyMeaning(CodeSystemHierarchyMeaning.GROUPEDBY);
      
      while (sc.hasNextLine()) {
        // Build the internal structure
        Map<String, Map<String, Map<String, Set<String>>>> categoryMap = new HashMap<>();
        
        // Read the structure into a FHIR code system
        String line = sc.nextLine(); 
        String[] parts = line.split("[,]");
        String level2Code = parts[0];
        String level2Display = parts[1];
        String level3Code = parts[2];
        String level3Display = parts[3];
        String level4Code = parts[4];
        String level4Display = parts[5];
        
        ConceptDefinitionComponent level2 = getConcept(null, level2Code, level2Display);
        if (!containsCode(cs, level2)) {
          cs.getConcept().add(level2);
        }

        ConceptDefinitionComponent level3 = getConcept(level2, level3Code, level3Display);
        getConcept(level3, level4Code, level4Display);
      }

      return cs;
    }
  }

  private boolean containsCode(CodeSystem cs, ConceptDefinitionComponent concept) {
    for (ConceptDefinitionComponent cdc : cs.getConcept()) {
      if (cdc.getCode().equals(concept.getCode())) {
        return true;
      }
    }
    return false;
  }
  
  private ConceptDefinitionComponent getConcept(
          ConceptDefinitionComponent parent, String code, String display) {

    if (parent == null) {
      return new ConceptDefinitionComponent().setCode(code).setDisplay(display);
    }
    List<ConceptDefinitionComponent> children = parent.getConcept();
    for (ConceptDefinitionComponent c : children) {
      if (c.getCode().equals(code)) {
        return c;
      }
    }
    return parent.addConcept().setCode(code).setDisplay(display);
  }
  
}
