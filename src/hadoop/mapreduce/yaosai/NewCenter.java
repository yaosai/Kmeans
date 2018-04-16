package hadoop.mapreduce.yaosai;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;


public class NewCenter {  
    
    int k = 8;  
    float shold=Integer.MIN_VALUE;  
    String[] line;  
    String newcenter = new String("");  
      
    public float run(String[] args) throws IOException,InterruptedException  
    {  
        Configuration conf = new Configuration();  
        conf.set("hadoop.job.ugi", "hadoop,hadoop");   
        FileSystem fs = FileSystem.get(URI.create(args[2]+"/part-r-00000"),conf);  
        FSDataInputStream in = null;  
        ByteArrayOutputStream out = new ByteArrayOutputStream();  
        try{   
               
            in = fs.open( new Path(args[2]+"/part-r-00000"));   
            IOUtils.copyBytes(in,out,50,false);  
            line = out.toString().split("\n");  
            } finally {   
                IOUtils.closeStream(in);  
            }  
        System.out.println(out.toString());  
        for(int i=0;i<k;i++)  
        {  
            String[] l = line[i].replace("\t", " ").split(" ");  
            String[] startCenter = l[0].replace("(", "").replace(")", "").split(",");  
            String[] finalCenter = l[l.length-1].replace("(", "").replace(")", "").split(",");  
            float tmp = 0;  
            for(int j=0;j<startCenter.length;j++)  
                tmp += Math.pow(Float.parseFloat(startCenter[j])-Float.parseFloat(finalCenter[j]), 2);  
            newcenter = newcenter + l[l.length - 1].replace("\t", "") + " ";  
            if(shold <= tmp)  
                shold = tmp;      
        }  
        OutputStream out2 = fs.create(new Path(args[1]+"/center") );   
        IOUtils.copyBytes(new ByteArrayInputStream(newcenter.getBytes()), out2, 4096,true);  
        System.out.println(newcenter);  
        return shold;  
    }  
  
}  