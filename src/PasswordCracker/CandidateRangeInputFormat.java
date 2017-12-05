/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package PasswordCracker;

import static PasswordCracker.PasswordCrackerUtil.TOTAL_PASSWORD_RANGE_SIZE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;

public class CandidateRangeInputFormat extends InputFormat<Text, Text> {
    private List<InputSplit> splits;

    @Override
    public RecordReader<Text, Text> createRecordReader(InputSplit split, TaskAttemptContext context)
            throws IOException, InterruptedException {
        return new CandidateRangeRecordReader();
    }


    // It generate the splits which are consist of string (or solution space range) and return to JobClient.
    @Override
    public List<InputSplit> getSplits(JobContext job) throws IOException, InterruptedException {
       splits = new ArrayList<>();

        int numberOfSplit = job.getConfiguration().getInt("numberOfSplit", 1);    //get map_count
        long subRangeSize = (TOTAL_PASSWORD_RANGE_SIZE + numberOfSplit - 1) / numberOfSplit;

        String[] hosts = new String[]{"10.20.13.124",
        							  "10.20.13.125",
        							  "10.20.13.126",
        							  "10.20.13.127",
        							  "10.20.13.128",
        							  "10.20.13.129",
        							  "10.20.13.130",
        							  "10.20.13.131"}; // TODO(aibeksmagulov): maybe unnecessary
        /** COMPLETE **/
        for(long i = 0; i < numberOfSplit; i++) {
        	long rangeBegin = i * subRangeSize;
        	long rangeEnd = (i + 1) * subRangeSize; // TODO(aibeksmagulov): should we consider case when rangeEnd > totalRangeSize?
        	String inputRange = new String(Long.toString(rangeBegin) + " " + Long.toString(rangeEnd));
        	
        	splits.add(new CandidateRangeInputSplit(inputRange, rangeEnd, null));
        }

        return splits;
    }
}
