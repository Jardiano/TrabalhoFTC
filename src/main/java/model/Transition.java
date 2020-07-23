package model;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@XStreamAlias("transition")
public class Transition
{
    private String from;
    private String to;
    private String read;


    @Override
    public String toString()
    {
        return "Transition{" +
            "from='" + "q"+from + '\'' +
            ", to='" + "q"+to + '\'' +
            ", read='" + read + '\'' +
            '}';
    }
}
