package model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class From
{
    private String value;

    @Override
    public String toString()
    {
        return value;
    }
}
