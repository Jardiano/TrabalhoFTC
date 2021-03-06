package controller;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import model.Automaton;
import model.State;
import model.Structure;
import model.Transition;
import sun.rmi.runtime.Log;

public class GeradorXMLController
{

    public void geraXML(Automaton automaton, String expression, String path){
        StringBuilder header = new StringBuilder();
        //header.append("<?xml version=\"1.0\"?>\n");
        header.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
        header.append(" <!--".concat(expression).concat("-->\n"));
        //header.append("<structure xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">");

        Structure structure = new Structure("fa",automaton);


        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(new Class[] {Structure.class,Automaton.class});
        //xStream.aliasField("state test", Structure.class, "state");
        xStream.addImplicitCollection(Automaton.class, "states");
        xStream.addImplicitCollection(Automaton.class, "transitions");

        String xml = xStream.toXML(structure);

        xml = xml.replace("<read>λ</read>", "<read/>");

        xml = xml.replace("<final>false</final>","");
        xml = xml.replace("<initial>false</initial>","");

        xml = xml.replace("<final>true</final>","<final />");
        xml = xml.replace("<initial>true</initial>","<initial />");

        header.append(xml);

        geraArquivo(header.toString(), path);
    }

    private void geraArquivo(String content,String path)  {
        File file;
        if(path == null){
            file = new File("dfa.jff");
        }else{
            file = new File(path.concat(System.getProperty("file.separator")).concat("dfa.jff"));
        }


        FileWriter writer = null;
        try {
            if(file.exists()){
                file.delete();
            }
            writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        }
        catch (IOException e) {
            Log.getLog(String.valueOf(Level.SEVERE),"Erro ao gerar arquivo", false);
        }
    }


    /*public static void main(String[] args)
    {
        GeradorXMLController controller = new GeradorXMLController();
        controller.geraXML(new Automaton(),"", null);
    }*/
}
