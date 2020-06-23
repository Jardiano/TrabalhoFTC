package converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import model.Automaton;
import model.State;
import model.Structure;

public class AutomatonConverter implements Converter
{
    public boolean canConvert(Class clazz)
    {
        return clazz.equals(State.class);
    }


    public void marshal(
        Object value, HierarchicalStreamWriter writer,
        MarshallingContext context)
    {
    }


    public Object unmarshal(
        HierarchicalStreamReader reader, UnmarshallingContext context)
    {
        Automaton automaton = new Automaton();
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            if ("state".equals(reader.getNodeName()))
            {
                String state = (String) context.convertAnother(automaton, String.class);
            }
            reader.moveUp();
        }
        return automaton;
    }
}
