package ru.nntu.vst.standcommunity.server;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.tools.ant.util.OutputStreamFunneler;

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/** Tutorial 4 - create a model and write it in XML form to standard out
 */
public class GeneratorRDF extends Object {
    
    // some definitions
    static String tutorialURI  = "http://hostname/rdf/tutorial/";
    static String briansName   = "Brian McBride";
    static String briansEmail1 = "brian_mcbride@hp.com";
    static String briansEmail2 = "brian_mcbride@hpl.hp.com";
    static String title        = "An Introduction to RDF and the Jena API";
    static String date         = "23/01/2001";
    
    public static String gen(String URI) throws FileNotFoundException {
    
        // some definitions
        //String personURI    = "http://somewhere/JohnSmith";
        String givenName    = "John";
        String familyName   = "Smith";
        String fullName     = givenName + " " + familyName;
        // create an empty model
        Model model = ModelFactory.createDefaultModel();

        // create the resource
        //   and add the properties cascading style
        Resource johnSmith
          = model.createResource(URI)
                 .addProperty(VCARD.FN, fullName)
                 .addProperty(VCARD.N, 
                              model.createResource()
                                   .addProperty(VCARD.Given, givenName)
                                   .addProperty(VCARD.Family, familyName));
        
        // now write the model in XML form to a file
        String s="";
        
        StringWriter sw = new StringWriter();
        		
        //model.write(System.out);
        model.write(sw);
        System.out.println(sw.toString());
        //out.flush();
        return sw.toString();
        
    }
}
