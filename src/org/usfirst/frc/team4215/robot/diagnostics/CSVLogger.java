package org.usfirst.frc.team4215.robot.diagnostics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class CSVLogger 
{
	// USB drive is mounted to /U on roboRIO
    public static final String output_dir = "/U/data_captures/";
    BufferedWriter file = null;
    boolean allowHeaders = true;
    boolean openRecord = false;
    String filename;
    
    private CSVLogger(BufferedWriter file)
    {
    	this.file = file;	
    	return;
    }
	
    public String getFilename()
    {
    	return this.filename;
    }
    
    public static CSVLogger create(String[] data_fields, String[] units_fields)
    {
    	CSVLogger logger = CSVLogger.create(false);
        logger.writeHeader(data_fields, units_fields);
        
        return logger;
    }
    
    
    public static CSVLogger create(boolean writeDefaultHeaders)
    {
    	try
    	{
    		// DateFormat
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
            df.setTimeZone(TimeZone.getTimeZone("US/Central"));
            String log_name = output_dir + "log_" + df.format(new Date()) + ".csv";	

            // Open File
            FileWriter fstream = new FileWriter(log_name, false);
            BufferedWriter file = new BufferedWriter(fstream);

            CSVLogger logger = new CSVLogger(file);
            
            if (writeDefaultHeaders) {
            	logger.writeHeader(new String[] { "data" }, new String[] { "units"});
            }
            
            return logger;
    	}
        catch (Exception e) {
            System.err.println("CSVLogger Exception: " + e.getMessage());
            return null;
        }
    }

    public void writeHeader(String[] data_fields, String[] units_fields)
    {
    	try
    	{
        	if (!allowHeaders)
        	{
        		return;
        	}

	        if (openRecord)
	        {
        		return;
	        }

        	if (data_fields != null)
        	{
        		file.write(data_fields[0]);
                for (int i=1; i<data_fields.length; i++) {
                    file.write(", "  + data_fields[i]);
                }
                file.newLine();
        	}

        	if (units_fields != null)
        	{
        		file.write(units_fields[0]);
                for (int i=1; i<units_fields.length; i++) {
                    file.write(", ");
                    file.write(units_fields[i]);
                }
                file.newLine();
        	}
            
        	allowHeaders = false;    		
    	}
        catch (Exception e) {
            System.err.println("CSVLogger Exception: " + e.getMessage());
        }
    }
    
    public void append(double... data_elements) 
    {
        try {        	
	        if (file == null) {
	            System.err.println("Error - Log is not opened, cannot write!");
	            return;
	        }
	        
	        if (data_elements == null)
	    	{
	            System.err.println("Error - Record open, cannot write header!");
	            return;
	    	}

	        if (openRecord)
	        {
	        	file.write(", ");
	        }
    		file.write(Double.toString(data_elements[0]));
    		
            for (int i=1; i<data_elements.length; i++) {
                file.write(", ");
                file.write(Double.toString(data_elements[0]));
            }
            openRecord = true;
        }
        catch (Exception e) {
            System.err.println("CSVLogger Exception: " + e.getMessage());
        }
	}

    public void newline() 
    {
        try {        	
	        if (file == null) {
	            System.err.println("Error - Log is not opened, cannot write!");
	            return;
	        }

	    	if(openRecord)
	    	{
	    		file.newLine();
	            openRecord = false;
	    	}
	    	else
	    	{
	            System.err.println("Error - Record not open, cannot write newline!");
	            return;
	        }
        }
        catch (Exception e) {
            System.err.println("CSVLogger Exception: " + e.getMessage());
        }
	}

    public void flush() {
		try {
	    	if (file == null) {
	            return;
	        }
	    	
	    	if(openRecord)
	    	{
	    		file.newLine();
	            openRecord = false;
	    	}
	    	
            file.flush();
        }
        catch (Exception e) {
            System.err.println("CSVLogger Exception: " + e.getMessage());
        }
    }

    public void close() {
        try {
	    	if (file == null) {
	            return;
	        }

	    	if(openRecord)
	    	{
	    		file.newLine();
	            openRecord = false;
	    	}

            file.flush();
            file.close();
            file = null;
        }
        catch (Exception e) {
            System.err.println("CSVLogger Exception: " + e.getMessage());
        }
    }
}
