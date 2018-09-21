package au.csiro.gelcustomdisordermapping;

import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.CodeSystem.CodeSystemContentMode;
import org.hl7.fhir.dstu3.model.CodeSystem.CodeSystemHierarchyMeaning;
import org.hl7.fhir.dstu3.model.CodeSystem.ConceptDefinitionComponent;
import org.hl7.fhir.dstu3.model.ConceptMap.ConceptMapGroupComponent;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Generates a code system from a CSV file with the following format: TODO.
 * 
 * @author Alejandro Metke
 *
 */
@Service
public class PanelAppGenerator {

  public static final String VERSION = "version";
  public static final String ID = "id";
  public final String PANELAPP_URL = "http://genomicsengland.co.uk/panels";
  public final String RECRUITED_DISORDERS_URL = "http://genomicsengland.co.uk/recruited-disorders";
  
  /**
   * Reads a CSV file and generates a FHIR code system.
   * 
   * @param csv The CSV file.
   * @throws FileNotFoundException Thrown if the file is not found.
   */
  public CodeSystem generateFromCsv(File csv) throws FileNotFoundException {
    try (Scanner sc = new Scanner(csv)) {
      
      final CodeSystem cs = new CodeSystem();
      cs.setName("GEL PanelApp panel List");
      cs.setUrl(PANELAPP_URL);
      cs.setHierarchyMeaning(CodeSystemHierarchyMeaning.GROUPEDBY);
      cs.setStatus(PublicationStatus.DRAFT);
      cs.setContent(CodeSystemContentMode.COMPLETE);
      cs.setValueSet(PANELAPP_URL);
      cs.setId("panelapp");
      CodeSystem.PropertyComponent versionPropertyComponent = cs.addProperty();
      versionPropertyComponent.setType(CodeSystem.PropertyType.STRING);
      versionPropertyComponent.setCode(VERSION);
      CodeSystem.PropertyComponent idPropertyComponent = cs.addProperty();
      idPropertyComponent.setType(CodeSystem.PropertyType.STRING);
      idPropertyComponent.setCode(ID);
      
      while (sc.hasNextLine()) {

        // Read the structure into a FHIR code system
        String line = sc.nextLine(); 
        String[] parts = line.split("\t");
        String id = parts[0];
        String version = parts[1];
        String name = parts[2];
        if (parts.length > 3) {
          String unparsedDiseases = parts[3];
          List<String> diseases = new ArrayList<>();
          if (!StringUtils.isEmpty(unparsedDiseases)) {
            diseases = Arrays.asList(unparsedDiseases.split(";"));
          }
        }

        // NOTE: unique code is concatenating id and version to support panel versioning
        ConceptDefinitionComponent panel = createConcept(cs, String.format("%s-%s", id, version), name);
        CodeSystem.ConceptPropertyComponent propertyComponent1 = panel.addProperty();
        propertyComponent1.setCode(VERSION);
        propertyComponent1.setValue(new StringType(version));
        CodeSystem.ConceptPropertyComponent propertyComponent2 = panel.addProperty();
        propertyComponent2.setCode(ID);
        propertyComponent2.setValue(new StringType(id));
        if (!containsCode(cs, panel)) {
          cs.addConcept(panel);
        }
        else {
          System.out.println("Hey repeated panel id!");
        }
      }
      return cs;
    }
  }
  
  public ConceptMap generateConceptMap(File csv) throws FileNotFoundException {
    try (Scanner sc = new Scanner(csv)) {
      final ConceptMap cm = new ConceptMap();
      cm.setName("");
      cm.setUrl("");
      cm.setStatus(PublicationStatus.DRAFT);
      boolean foundHeader = false;

      ConceptMapGroupComponent group = cm.addGroup();
      group.setSource(PANELAPP_URL);


      while (sc.hasNextLine()) {

        if (!foundHeader) {
          foundHeader = true;
          continue;
        }

        // Read the structure into a FHIR code system
        String line = sc.nextLine();
        String[] parts = line.split("[,]");

        String level4Code = parts[4];
        String hpoCode = parts[8];


      }

      return cm;
    }
  }

  private boolean containsCode(CodeSystem cs, ConceptDefinitionComponent concept) {
    return containsCode(cs.getConcept(), concept);
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
}
