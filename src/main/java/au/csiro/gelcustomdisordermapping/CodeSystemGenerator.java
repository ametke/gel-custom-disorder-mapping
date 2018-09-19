package au.csiro.gelcustomdisordermapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Scanner;

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
  public void generateFromCsv(File csv) throws FileNotFoundException {
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
        
        
        
      }
    }
  }
  
  private ConceptDefinitionComponent getConcept(String code) {
    
  }
  
}
