package util;

public class Pair<T1, T2>
{
    private final T1 first;
    private final T2 second;
    public Pair(T1 string, T2 i)
    {
        this.first =string;
        this.second =i;
    }
    public T2 getValue()
    {
        return second;
    }
    public T1 getKey()
    {
        return first;
    }
}