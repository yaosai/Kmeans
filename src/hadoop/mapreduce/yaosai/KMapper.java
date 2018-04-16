package hadoop.mapreduce.yaosai;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;


public class KMapper extends Mapper<LongWritable, Text, Text, Text> {  
    
    private String[] center;  
      
    protected void setup(Context context) throws IOException,InterruptedException  //read centerlist, and save to center[]  
    {  
        String centerlist = "hdfs://192.168.32.128:9000/center/center"; //center文件  
        Configuration conf1 = new Configuration();   
        conf1.set("hadoop.job.ugi", "hadoop-user,hadoop-user");   
       FileSystem fs = FileSystem.get(URI.create(centerlist),conf1);   
       FSDataInputStream in = null;   
       ByteArrayOutputStream out = new ByteArrayOutputStream();  
       try{   
               
           in = fs.open( new Path(centerlist) );   
           IOUtils.copyBytes(in,out,100,false);    
           center = out.toString().split(" ");  
           }finally{   
                IOUtils.closeStream(in);  
            }  
    }  
      
    public void map(LongWritable key,Text value,Context context) throws IOException,InterruptedException  
    {  
        StringTokenizer itr = new StringTokenizer(value.toString());  
        while(itr.hasMoreTokens())  
        {  
            String outValue = new String(itr.nextToken());  
            String[] list = outValue.replace("(", "").replace(")", "").split(",");  
            String[] c = center[0].replace("(", "").replace(")", "").split(",");  
            float min = 0;  
            int pos = 0;  
            for(int i=0;i<list.length;i++)  
            {  
                min += (float) Math.pow((Float.parseFloat(list[i]) - Float.parseFloat(c[i])),2);  
            }  
            for(int i=0;i<center.length;i++)  
            {  
                String[] centerStrings = center[i].replace("(", "").replace(")", "").split(",");  
                float distance = 0;  
                for(int j=0;j<list.length;j++)  
                    distance += (float) Math.pow((Float.parseFloat(list[j]) - Float.parseFloat(centerStrings[j])),2);  
                if(min>distance)  
                {  
                    min=distance;  
                    pos=i;  
                }  
            }  
            context.write(new Text(center[pos]), new Text(outValue));  
        }  
    }  
  
}  