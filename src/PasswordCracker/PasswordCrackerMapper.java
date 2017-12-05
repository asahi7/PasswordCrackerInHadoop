package PasswordCracker;

import static PasswordCracker.PasswordCrackerUtil.findPasswordInRange;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PasswordCrackerMapper
        extends Mapper<Text, Text, Text, Text> {

    //  After reading a key/value, it compute the password by using a function of PasswordCrackerUtil class
    //  If it receive the original password, pass the original password to reducer. Otherwise is not.
    //  FileSystem class : refer to https://hadoop.apache.org/docs/r2.7.3/api/org/apache/hadoop/fs/FileSystem.html

    public void map(Text key, Text value, Context context)
            throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        String flagFilename = conf.get("terminationFlagFilename");
        FileSystem hdfs = FileSystem.get(conf);

        TerminationChecker terminationChecker = new TerminationChecker(hdfs, flagFilename);

        /** COMPLETE **/
        
        long rangeBegin = Long.parseLong(key.toString());
        long rangeEnd = Long.parseLong(value.toString());

        String encryptedPassword = conf.get("encryptedPassword");
        System.out.println("Is terminated : " + terminationChecker.isTerminated());
        System.out.println("Key : " + key.toString() + " Value : " + value.toString());
        String password = findPasswordInRange(rangeBegin, rangeEnd, encryptedPassword, terminationChecker);
        System.out.println("Is terminated : " + terminationChecker.isTerminated());
        
        
        /* ASSUMPTION */
        if(password != null) { // Maybe this block should be inside the Reducer block
        	terminationChecker.setTerminated();
        	context.write(new Text(encryptedPassword), new Text(password));
        }
    }
}

//  It is class for early termination.
//  In this assignment, a particular file becomes an ealry termination signal.
//  So, If a task find the original password, then the task creates a file using a function in this class.
//  Therefore, tasks will determine whether the quit or not by checking presence of file.
//  FileSystem class : refer to https://hadoop.apache.org/docs/r2.7.3/api/org/apache/hadoop/fs/FileSystem.html

class TerminationChecker {
    FileSystem fs;  
    Path flagPath;

    TerminationChecker(FileSystem fs, String flagFilename) {
        this.fs = fs;
        this.flagPath = new Path(flagFilename);
    }

    public boolean isTerminated() throws IOException {
	/** COMPLETE **/
    	return fs.exists(flagPath);
    }

    public void setTerminated() throws IOException {
	/** COMPLETE **/
    	fs.create(flagPath);
    }
}
