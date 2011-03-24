package edu.cornell.mannlib.ingest.fieldHandlers;

import java.io.StringReader;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import edu.cornell.mannlib.ingest.fieldHandlers.XPath2FieldHandler;
import edu.cornell.mannlib.vitro.beans.Individual;

public class XPath2FieldHandlerTest extends TestCase {
    public XPath2FieldHandlerTest(){
        super();
    }
    
    Document dom4jDoc;
    private SAXReader xmlReader;    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();               
        xmlReader = new org.dom4j.io.SAXReader(); 
        StringReader sr = new StringReader( xmlForTest );
        dom4jDoc = xmlReader.read( sr );
    }
  
    public void testXPath(){
        XPath2FieldHandler xp2fh =null;
        try {
            xp2fh = new XPath2FieldHandler("");                
            xp2fh.addField2XPath("name", "/response/Name", "aadefault");
            xp2fh.addField2XPath("description", "/response/Dept", "bbdefault");
            
            Individual ent = new Individual();
            xp2fh.setFields( dom4jDoc, ent);
                        
            assertEquals(ent.getName(),"Caruso, Brian");
            assertEquals(ent.getDescription(),"Plant Pathology (GNVA)");            
            
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
    
    public void testDefaults(){
        XPath2FieldHandler xp2fh =null;
        try {
            xp2fh = new XPath2FieldHandler("");                
            xp2fh.addField2XPath("name", "/not/going/to/be/in/the/xml", "dumb default name");
            xp2fh.addField2XPath("description", "/also/not/in/the/xml", "other default desc");
            xp2fh.addField2XPath("blurb", null , "should get default if xpath query is null");
            
            Individual ent = new Individual();
            xp2fh.setFields( dom4jDoc, ent);
                        
            assertEquals(ent.getName(),"dumb default name");
            assertEquals(ent.getDescription(),"other default desc");            
            assertEquals(ent.getBlurb(),"should get default if xpath query is null");
            
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
    }
        
    public static void main(String[] args) {
        junit.textui.TestRunner.run( XPath2FieldHandlerTest.class );
    }

    public static String xmlForTest =
        "<?xml version='1.0' encoding='UTF-8'?>" +
        "<response>" +
        "        <Response_Id>d9f1aa8a-8c23-4e06-9d7e-155ca1b510c4</Response_Id>" +
        "        <NetId>tes2</NetId>" +
        "        <Dept>Plant Pathology (GNVA)</Dept>" +
        "        <Name>Caruso, Brian</Name>" +
        "        <First>Brian</First>" +
        "        <Middle>David</Middle>" +
        "        <Last>Caruso</Last>" +
        "        <Last_Modified/>" +
        "        <Editor_Comments/>" +
        "        <Edited_Status/>" +
        "        <Status>new</Status>" +
        "        <Acad_Priority>Land Grant Mission</Acad_Priority>" +
        "        <Keywords>Plant Pahology|Vegetable Diseases|Integratted Pest Management|Root Diseases|Soil Health|Internaional Agriculture</Keywords>" +
        "        <Collaborative_Research>plant pathology</Collaborative_Research>" +
        "        <Area_Concentration>Vegetable Pathology|Soil Health</Area_Concentration>" +
        "        <Web>http://www.nysaes.cornell.edu/pp|http://www.nysaes.cornell.edu/pp/faculty/abawi/index.html</Web>" +
        "        <Other/>" +
        "        <Comments/>" +
        "</response>" ;
}