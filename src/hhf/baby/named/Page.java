package hhf.baby.named;

import java.util.List;

/**
 * Created by Administrator on 2016/11/1.
 */
public class Page<T>
{
    public int start;
    public int limit;
    public int total;
    public List<T> data;
}
