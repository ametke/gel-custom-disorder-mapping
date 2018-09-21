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
 * @author Alejandro Metke
 *
 */
@Service
public class ConceptMapGenerator {
  
  public ConceptMap generateConceptMap(File csv) throws FileNotFoundException {
    try (Scanner sc = new Scanner(csv)) {
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
  
}
