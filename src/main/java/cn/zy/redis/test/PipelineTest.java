package cn.zy.redis.test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

/**
 * 流水线测试
 */
public class PipelineTest
{

    public static void main(String[] args)
    {
        Jedis jedis = new Jedis("localhost", 6379);

        Pipeline pipeline = jedis.pipelined();

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
                pipeline.set("test" + i, i + "");
            }
            pipeline.syncAndReturnAll();

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

}
