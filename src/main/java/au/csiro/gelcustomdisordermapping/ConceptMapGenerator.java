package au.csiro.gelcustomdisordermapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.hl7.fhir.dstu3.model.ConceptMap;
import org.hl7.fhir.dstu3.model.ConceptMap.ConceptMapGroupComponent;
import org.hl7.fhir.dstu3.model.ConceptMap.SourceElementComponent;
import org.hl7.fhir.dstu3.model.Enumerations.ConceptMapEquivalence;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.springframework.stereotype.Service;

/**
 * Generates several concept maps.
 * 
 * @author Alejandro Metke
 *
 */
@Service
public class ConceptMapGenerator {
  
  /**
   * Generates a GEL disease to HPO map.
   * 
   * @return The concept map.
   * @throws FileNotFoundException If the source file is not found.
   */
  public ConceptMap generateDiseaseToHpoMap() throws FileNotFoundException {
    try (Scanner sc = new Scanner(new File("src/main/rare_diseases.csv"))) {
      final ConceptMap cm = new ConceptMap();
      cm.setName("Recruited disorders to HPO terms map.");
      cm.setUrl(DisordersGenerator.RECRUITED_DISORDERS_TO_HPO_URL);
      cm.setStatus(PublicationStatus.DRAFT);
      boolean foundHeader = false;
      
      ConceptMapGroupComponent group = cm.addGroup();
      group.setSource(DisordersGenerator.RECRUITED_DISORDERS_URL);
      group.setTarget(DisordersGenerator.HPO_URL);
      
      final Map<String, Set<String>> map = new HashMap<>();
      
      while (sc.hasNextLine()) {

        if (!foundHeader) {
          foundHeader = true;
          continue;
        }

        // Read the structure into a FHIR code system
        String line = sc.nextLine(); 
        String[] parts = line.split(",", 10);
        
        //String level4Display = parts[3];
        String level4Code = parts[4];
        //String hpoDisplay = parts[7];
        String hpoCode = parts[8];
        
        if (!hpoCode.isEmpty()) {
          Set<String> tgts = map.get(level4Code);
          if (tgts == null) {
            tgts = new HashSet<>();
            map.put(level4Code, tgts);
          }
          tgts.add(hpoCode);
        }
      }
      
      for (String key : map.keySet()) {
        SourceElementComponent source = group.addElement();
        source.setCode(key);
        Set<String> tgts = map.get(key);
        for (String tgt : tgts) {
          source.addTarget().setCode(tgt).setEquivalence(ConceptMapEquivalence.RELATEDTO);
        }
      }
      
      return cm;
    }
  }
  
  private Map<String, String> getNamesToIdsMap() throws FileNotFoundException {
    final Map<String, String> namesToIds = new HashMap<>();
    try (Scanner sc = new Scanner(new File("src/main/resources/rare_diseases.csv"))) {
      boolean foundHeader = false;
      
      while (sc.hasNextLine()) {
        if (!foundHeader) {
          foundHeader = true;
          sc.nextLine();
          continue;
        }
        String line = sc.nextLine(); 
        String[] parts = line.split("[,]");
        String level2Code = parts[0];
        String level2Display = parts[1];
        String level3Code = parts[2];
        String level3Display = parts[3];
        String level4Code = parts[4];
        String level4Display = parts[5];
        namesToIds.put(level2Display, level2Code);
        namesToIds.put(level3Display, level3Code);
        namesToIds.put(level4Display, level4Code);
      }
    }
    
    return namesToIds;
  }
  
  /**
   * Generates a GEL disorder to panel map.
   * 
   * @return The concept map.
   * @throws FileNotFoundException If the source file is not found.
   */
  public ConceptMap generateDisorderToPanelMap() throws FileNotFoundException {
    // First we need to load a map with the disease names to their ids
    final Map<String, String> namesToIds = getNamesToIdsMap();
    
    // Now we read the TSV file and use the map to get the disease code
    try (Scanner sc = new Scanner(new File("src/main/resources/disease2panel.tsv"))) {

      final Map<String, Set<String>> map = new HashMap<>();
      while (sc.hasNextLine()) {
        // Read the structure into a FHIR code system
        String line = sc.nextLine(); 
        String[] parts = line.split("\t");
        
        String panelCode = namesToIds.get(parts[0]);
        if (panelCode == null) {
          throw new RuntimeException("Unable to find id for disease " + parts[0]);
        }
        
        Set<String> tgts = map.get(panelCode);
        if (tgts == null) {
          tgts = new HashSet<>();
          map.put(panelCode, tgts);
        }
        tgts.add(parts[1]);
      }
      
      final ConceptMap cm = new ConceptMap();
      cm.setName("Recruited disorders to panels map.");
      cm.setUrl(DisordersGenerator.RECRUITED_DISORDERS_TO_PANELS_URL);
      cm.setStatus(PublicationStatus.DRAFT);
      
      ConceptMapGroupComponent group = cm.addGroup();
      group.setSource(DisordersGenerator.RECRUITED_DISORDERS_URL);
      group.setTarget(PanelAppGenerator.PANELAPP_URL);
      
      for (String key : map.keySet()) {
        SourceElementComponent source = group.addElement();
        source.setCode(key);
        Set<String> tgts = map.get(key);
        for (String tgt : tgts) {
          source.addTarget().setCode(tgt).setEquivalence(ConceptMapEquivalence.RELATEDTO);
        }
      }
      
      return cm;
    }
  }
  
}
