package hadoop.mapreduce.yaosai;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class KMeans {  
    
    public static void main(String[] args) throws Exception  
    {  
        CenterInitial centerInitial = new CenterInitial();  
        centerInitial.run(args); 
        int times=0;  
        double s = 0,shold = 0.0001;  
        do {  
            Configuration conf = new Configuration();  
            conf.set("fs.default.name", "hdfs://192.168.32.128:9000");  
            Job job = new Job(conf,"KMeans");  
            job.setJarByClass(KMeans.class);  
            job.setOutputKeyClass(Text.class);  
            job.setOutputValueClass(Text.class);  
            job.setMapperClass(KMapper.class);  
            job.setMapOutputKeyClass(Text.class);  
            job.setMapOutputValueClass(Text.class);  
            job.setReducerClass(KReducer.class);  
            FileSystem fs = FileSystem.get(conf);  
            fs.delete(new Path(args[2]),true);  
            FileInputFormat.addInputPath(job, new Path(args[0]));  
            FileOutputFormat.setOutputPath(job, new Path(args[2]));  
            job.waitForCompletion(true);  
            if(job.waitForCompletion(true))  
            {  
                NewCenter newCenter = new NewCenter();  
                s = newCenter.run(args);  
                times++;  
            }  
        } while(s > shold);  
        System.out.println("Iterator: " + times);         
    }  
  
}  
