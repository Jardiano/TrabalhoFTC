package model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Read
{
    private String value;

    @Override
    public String toString()
    {
        return value;
    }
}
