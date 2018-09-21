package au.csiro.gelcustomdisordermapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.CodeSystem;
import org.hl7.fhir.dstu3.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.dstu3.model.CodeSystem.CodeSystemHierarchyMeaning;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Generates a code system from a CSV file with the following format: TODO.
 * 
 * @author Alejandro Metke
 *
 */
@Service
public class DisordersGenerator {
  
  public static final String RECRUITED_DISORDERS_URL = 
      "http://genomicsengland.co.uk/recruited-disorders";
  public static final String RECRUITED_DISORDERS_TO_HPO_URL = 
      "http://genomicsengland.co.uk/recruited-disorders-to-hpo";
  public static final String RECRUITED_DISORDERS_TO_PANELS_URL = 
      "http://genomicsengland.co.uk/recruited-disorders-to-panels";
  public static final String HPO_URL = "http://purl.obolibrary.org/obo/hp.owl";
  
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
      cs.setUrl(RECRUITED_DISORDERS_URL);
      cs.setHierarchyMeaning(CodeSystemHierarchyMeaning.GROUPEDBY);
      cs.setStatus(PublicationStatus.DRAFT);
      cs.setContent(CodeSystemContentMode.COMPLETE);
      cs.setValueSet(RECRUITED_DISORDERS_URL);
      cs.setId("disorders");
      boolean foundHeader = false;
      
      while (sc.hasNextLine()) {

        if (!foundHeader) {
          foundHeader = true;
          sc.nextLine();
          continue;
        }
        // Read the structure into a FHIR code system
        String line = sc.nextLine(); 
        String[] parts = line.split("[,]");
        String level2Code = parts[0];
        String level2Display = parts[1];
        String level3Code = parts[2];
        String level3Display = parts[3];
        String level4Code = parts[4];
        String level4Display = parts[5];
        
        ConceptDefinitionComponent level2 = createConcept(cs, level2Code, level2Display);
        if (!containsCode(cs, level2)) {
          cs.addConcept(level2);
        }
        ConceptDefinitionComponent level3 = createConcept(level2, level3Code, level3Display);
        if (!containsCode(level2, level3)) {
          level2.addConcept(level3);
        }
        ConceptDefinitionComponent level4 = createConcept(level3, level4Code, level4Display);
        if (!containsCode(level3, level4)) {
          level3.addConcept(level4);
        }
      }
      return cs;
    }
  }
  
  

  private boolean containsCode(CodeSystem cs, ConceptDefinitionComponent concept) {
    return containsCode(cs.getConcept(), concept);
  }

  private boolean containsCode(ConceptDefinitionComponent parent, ConceptDefinitionComponent concept) {
    return containsCode(parent.getConcept(), concept);
  }

  private boolean containsCode(List<ConceptDefinitionComponent> children, ConceptDefinitionComponent concept) {
    return children.stream().filter(c -> c.getCode().equals(concept.getCode())).collect(Collectors.toList()).size() > 0;
  }

  private ConceptDefinitionComponent createConcept(CodeSystem cs, String code, String display) {
    List<ConceptDefinitionComponent> concepts = cs.getConcept().stream().filter(c -> c.getCode().equals(code)).collect(Collectors.toList());
    ConceptDefinitionComponent concept = null;
    if (CollectionUtils.isEmpty(concepts)) {
      concept = new ConceptDefinitionComponent().setCode(code).setDisplay(display);
      cs.addConcept(concept);
    }
    else {
      concept = concepts.get(0);
    }
    return concept;
  }

  private ConceptDefinitionComponent createConcept(ConceptDefinitionComponent cdc, String code, String display) {
    List<ConceptDefinitionComponent> concepts = cdc.getConcept().stream().filter(c -> c.getCode().equals(code)).collect(Collectors.toList());
    ConceptDefinitionComponent concept = null;
    if (CollectionUtils.isEmpty(concepts)) {
      concept = new ConceptDefinitionComponent().setCode(code).setDisplay(display);
      cdc.addConcept(concept);
    }
    else {
      concept = concepts.get(0);
    }
    return concept;
  }
}
