package converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import model.State;

public class StateConverter implements Converter
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
        State state = new State();
        while (reader.hasMoreChildren())
        {
            reader.moveDown();
            if ("isbn".equals(reader.getNodeName()))
            {
                String isbn = (String) context.convertAnother(state, String.class);
                //livro.setIsbn(isbn);
            }
            reader.moveUp();
        }
        return state;
    }


}
