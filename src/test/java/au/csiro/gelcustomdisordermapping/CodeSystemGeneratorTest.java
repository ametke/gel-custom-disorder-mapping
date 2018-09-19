package au.csiro.gelcustomdisordermapping;

import java.io.File;
import java.io.FileNotFoundException;

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
    csg.generateFromCsv(new File("src/test/rare_diseases.csv"));
  }
  
}
