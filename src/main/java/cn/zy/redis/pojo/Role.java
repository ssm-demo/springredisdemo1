package cn.zy.redis.pojo;

import java.io.Serializable;

public class Role implements Serializable
{
    private static final long serialVersionUID = 5210419364723125294L;

    private String id;

    private String name;

    private String sth;

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getSth()
    {
        return sth;
    }

    public void setSth(String sth)
    {
        this.sth = sth;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("Role{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", sth='").append(sth).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
