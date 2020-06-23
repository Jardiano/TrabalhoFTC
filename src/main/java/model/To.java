package model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class To
{
    private String value;

    @Override
    public String toString()
    {
        return value;
    }
}
