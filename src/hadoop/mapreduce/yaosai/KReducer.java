package hadoop.mapreduce.yaosai;
import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


public class KReducer extends Reducer<Text, Text, Text, Text> {  
    
    
    public void reduce(Text key,Iterable<Text> value,Context context) throws IOException,InterruptedException  
    {  
        String outVal = "";  
        int count=0;  
        String center="";  
        int length = key.toString().replace("(", "").replace(")", "").replace(":", "").split(",").length;  
        float[] ave = new float[Float.SIZE*length];  
        for(int i=0;i<length;i++)  
            ave[i]=0;   
        for(Text val:value)  
        {  
            outVal += val.toString()+" ";  
            String[] tmp = val.toString().replace("(", "").replace(")", "").split(",");  
            for(int i=0;i<tmp.length;i++)  
                ave[i] += Float.parseFloat(tmp[i]);  
            count ++;  
        }  
        for(int i=0;i<length;i++)  
        {  
            ave[i]=ave[i]/count;  
            if(i==0)  
                center += "("+ave[i]+",";  
            else {  
                if(i==length-1)  
                    center += ave[i]+")";  
                else {  
                    center += ave[i]+",";  
                }  
            }  
        }  
        System.out.println(center);  
        context.write(key, new Text(outVal+center));  
    }  
  
}  