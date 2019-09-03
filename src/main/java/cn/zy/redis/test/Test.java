package cn.zy.redis.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 粗暴测试Redis链接和处理
 */
public class Test
{

    public static void main(String[] args)
    {
        // 连接redis---简单连接
        Jedis jedis = connectDirect();
        // 连接--- 从连接池获取连接
        //Jedis jedis = getConnectionByPool();
        //jedis.auth(password)   // 如果需要密码
        int i = 0;
        try
        {
            Long start = System.currentTimeMillis();
            while(true)
            {
                long end = System.currentTimeMillis();
                if(end -start > 1000)
                {
                    break;
                }
                i++;
                jedis.set("test" + i, i + "");
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            jedis.close();
        }

        System.out.println("redis每秒操作: " + i + "次");
    }

    // 简单直连
    private static  Jedis connectDirect()
    {
        return new Jedis("localhost", 6379);
    }


    private static Jedis getConnectionByPool()
    {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        // 最大空闲数
        poolConfig.setMaxIdle(50);
        // 最大连接数
        poolConfig.setMaxTotal(100);
        // 最大毫秒等待数
        poolConfig.setMaxWaitMillis(20000);
        // 使用配置创建连接池
        JedisPool pool = new JedisPool(poolConfig,"localhost");
        // 从连接池中获取单个连接
       return pool.getResource();
    }

}
