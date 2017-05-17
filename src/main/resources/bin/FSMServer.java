package com.estafet.scribble.easyfsm.example;
import com.estafet.scribble.easyfsm.Action.FSMAction;
import com.estafet.scribble.easyfsm.States.FSMState;
import com.estafet.scribble.easyfsm.States.FSMStateAction;
import com.estafet.scribble.easyfsm.States.FSMStates;
import com.estafet.scribble.easyfsm.States.FSMTransitionInfo;
import com.estafet.scribble.easyfsm.FSM.FSM;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

public class FSMServer {
	
	static FSM f = null;
	static String myrole = "generic";
	static String portNumberString = "4040";
	static String urlString = "/fsmserver/api";
	static String location = "/Users/stalbot";
	static String payload = urlString + " port " + portNumberString + " response.";

    @SuppressWarnings("restriction")
	public static void main(String[] args) throws Exception
    {    	
    	if (args.length >= 1)
    		portNumberString = args[0];
    	int port = new Integer(portNumberString);
    	if (args.length >= 2)
    		urlString = args[1];
    	if (args.length >= 3)
    		location = args[2];
        System.out.println("Bringing up http server on port " + port + " for url " + urlString);
        System.out.println("Using " + location + " for scripts ");
        payload = "";

		HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        HttpContext context = server.createContext(urlString);
        System.out.println(context.getPath());
        context.setHandler((he) -> {
//System.out.println("\n------- GOT REQUEST METHOD: " + he.getRequestMethod() + "-------");
//System.out.println("context path: " + context.getPath());
                InputStreamReader isr =  new InputStreamReader(he.getRequestBody(),"utf-8");
                BufferedReader br = new BufferedReader(isr);                
                Map<String,List<String>> map = he.getRequestHeaders();
                Set<String> strings = map.keySet();
                Iterator<String> iter = strings.iterator();
                int j = 0;
                String contentLengthString = null;
                while (iter.hasNext())
                {
                	String key = iter.next();
                	List<String> values = map.get(key);
                	int cl = key.compareTo("Content-length");
                	for (int i = 0; (i < values.size()); i++)
                	{
                		if (cl == 0)
                			contentLengthString = values.get(i);
                	}
                }
                int contentLength = 0;
                if (contentLengthString != null)
                {
                	Integer i = new Integer(contentLengthString);
                	contentLength = i.intValue();
                }
                URI uri = he.getRequestURI();
//System.out.println("URI is <" + uri + ">");
                String[] p = extractParametersFrom(uri, "____");
                String event = p[1].substring(p[1].indexOf("=")+1);
//System.out.println("Event: " + event);
                String data = br.readLine();
//System.out.println("data is <<<<<<<<<<<<\n" + data + "\n<<<<<<<<<<<<");
		if (data != null)
			data = data.replaceAll("xxxx","\n");
//System.out.println("data is >>>>>>>>>>>>\n" + data + "\n>>>>>>>>>>>>");
                
                if (event.startsWith("eppLoad"))
                {
                    	String protocolName = p[2].substring(p[2].indexOf("=")+1);
                	String roleName = p[3].substring(p[3].indexOf("=")+1);
                	String startState = null;
                	myrole = roleName;
                	// Extract the file name and load the behavior into the FSM
System.out.println("Loading easyFSM role from scribble");
                	if (p.length >= 5)
                	{
                		startState = p[4].substring(p[4].indexOf("=")+1);
System.out.println("Instantiating FSM for role '" + roleName + "' based on '" + protocolName + "' starting at '" + startState + "'");
                	} else
System.out.println("Instantiating FSM for role '" + roleName + "' based on '" + protocolName + "'");
                	
                	eppLoad(data,protocolName,roleName);
            		f = eppInstantiate(roleName);
                	String currentState = f.getCurrentState();
                    String nextStates[] = null;
                    nextStates=f.getValidCommands();
                    String availableToDo = "";
                    for (int i=0; (i < nextStates.length); i++)
                    {
                    	availableToDo = availableToDo + "    <" + nextStates[i] + ">\n";
                    }
System.out.println("Accepting:\n" + availableToDo);
            		payload = "Instantiated FSM for role '" + roleName + "' based on '" + protocolName + "' for " + urlString + " at port " + portNumberString + "\nAccepting:\n" + availableToDo;
                	// Should be two/three parameters, one is the scribble and the other is the role name to play
                	// and the third (optional) is the starting state
                } else {
                	// Handle the event as an FSM event
System.out.println("Trying to execute as an FSM in the role of " + myrole + " with the message <" + uri + ">");
                    String message = uri.toString();
                    String m = message.substring(message.indexOf("____") + "____".length());
                	if (data != null)
                		payload = FSMExecute(f, m, data);
                	else
                		payload = FSMExecute(f,m);
                }
                he.sendResponseHeaders(200, payload.getBytes().length);
                final OutputStream output = he.getResponseBody();
                output.write(payload.getBytes());
                output.flush();
                he.close();
System.out.println("------- END REQUEST METHOD: " + he.getRequestMethod() + "-------");
        });
        server.start();
    }
    
    //
    // Of the form;
    //	api/event="eventstring", scribble="somescribble", role="somerole"[, start="startstate"]
    //

    public static void prettyPrint(String s)
    {
    	String a = s.replaceAll(";", ";\n");
    	System.out.println(a);
    }
    
    public static String eppLoad(String scribble, String protocol, String role)
    {
    	System.out.println("eppLoad ...");
//System.out.println("<<<<\n" + scribble + "\n<<<<<");
    	// 1. Save scribble to a file.
    	// 2. build command
    	//		${ROOT}/bin/eppLoad scribble(as a file) protocol role
    	// 3. exec the command
    	// 4. 	find and open the resultant <role>_config.txt in ${ROOT}/generated
    	String[] lines = scribble.split(";");
    	String moduleName = "";
    	//System.out.println(lines.length + " number of lines in scribble");
    	for (int i=0; (i<lines.length); i++)
    	{
    		//System.out.println("lines[" + i + "]<" + lines[i] + ">");
    		if (lines[i].contains("module"))
    		{
    			moduleName = lines[i].substring(lines[i].lastIndexOf(".")+1);
    			break;
    		}
    	}
    	String scribbleFile = location + "/bin/generated/" + moduleName + ".scr";
	// Save the scribble to icribb eFile
	BufferedWriter writer = null;
	PrintWriter out = null;
	try {
	    out = new PrintWriter( new FileWriter(scribbleFile));
	    out.println(scribble);
	    out.close( );
	}
	catch ( IOException e)
	{
		e.printStackTrace();
	}
	// Construct epp command
    	String command = location + "/bin/epp.sh " + scribbleFile + " " + protocol + " " + role;
    	
    	System.out.println("command <" + command + ">");

    	Shell sh = new Shell();
		String output = sh.executeCommand(command);

		System.out.println("***\n" + output + "\n***");
		output = sh.executeCommand("ls -ls " + location + "/bin/generated");
		System.out.println("***\n" + output + "\n***");
		
		return location + "/bin/generated" + role + "_config.txt";
    }
    
    public static void executeScript(String s) 
    {
    	  try 
    	  {
	    	    ProcessBuilder pb = new ProcessBuilder(s);
	    	    Process p = pb.start();     // Start the process.
	    	    p.waitFor();                // Wait for the process to finish.
	    	    System.out.println("Script executed successfully");
    	  } catch (Exception e) {
    	    e.printStackTrace();
    	  }
    }
    public static FSM eppInstantiate(String roleName)
    {
    	String config = location + "/bin/generated/" + roleName + "_config.txt";
    	// 1. Open file <role>_config.txt
    	// 2. conduct start up of FSM
    	// 3. run FSM in modified runloop of httpserver
    	InputStream inputS = null;
    	FileInputStream m_fileReader = null;
		try 
		{

			m_fileReader = new FileInputStream(config);
			inputS = m_fileReader;
			if (m_fileReader != null)
				System.out.println("Got file " + config);
    		FSM f = new FSM(inputS, null);
        	String currentState = f.getCurrentState();
        	System.out.println("FSM Instantiating for role " + roleName + ": current state is: <" + currentState + ">");
            String nextStates[] = null;
            nextStates=f.getValidCommands();
            for (int i=0; (i < nextStates.length); i++)
            {
            	System.out.println("nextstate[" + i +"]: <" + nextStates[i] + ">");
            }
    		return f;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
    }
    
    // 
    // Data is a JSON object of the following form:
    // {
    //		"function":"login",
    // 		"from":"requestor",
    //		"parameters":
    //			{
    //				"param1":{"username":"steve"},
    //				"param2":{"password":"xyz"}
    //			}
    // }
    // Alas JSON does not support ordinal preservation and so we need to 
    // capture the order in the JSON itself.
    // It would be better if the FSM had these encoded properly but for now ...
    //		HACK
    //
    static String FSMExecute(FSM f, String msg, String data)
    {
    	String retval = "";
    	String availableToDo = "";
    	String tmpmsg = msg;

    	System.out.println("FSMExecute(" + f + "," + tmpmsg + "," + data + ")");
    	try {
	    	JSONObject object = new JSONObject(data);
	    	System.out.println("data is a JSON object <" + object +">");
	    	//
	    	// Build FSM message from tmpmsg and data
	    	//
	    	tmpmsg = tmpmsg + "(" + object.getString("function") + "(____))__";
	    	try {
	    		tmpmsg = tmpmsg + "from_" + object.getString("from");
	    	} catch (org.json.JSONException e1) {
	    		try  {
	    			tmpmsg = tmpmsg + "to_" + object.getString("to");
	    		} catch (org.json.JSONException e2) {
	    			
	    		}
	    	}
	    	
	    	String tmp = "";
	    	try {
	    		JSONObject params = object.getJSONObject("parameters");
	    	
		    	//System.out.println("params are: <" + params + ">");
		    	for (int i=1; (i <= params.length()); i++)
		    	{
		    		JSONObject o = params.getJSONObject("param" + i);
		    		tmp = tmp + o.names().get(0) + ", ";
		    	}
		    	tmp = tmp.substring(0,tmp.length()-2);
	    	} catch (org.json.JSONException e3) {
	    		//System.out.println("no parameters");
	    	}
	    	tmpmsg = tmpmsg.replaceAll("____", tmp);
	    	System.out.println("xsposed msg is <" + tmpmsg + ">");

	    	String currentState = f.getCurrentState();
System.out.println("Current state before execution is: <" + currentState + ">");
	        String nextStates[] = null;
	        nextStates=f.getValidCommands();
System.out.println("Valid next states before execution are:");
	        for (int i=0; (i < nextStates.length); i++)
	        {
	        	System.out.println("    nextstate[" + i +"]: <" + nextStates[i] + ">");
	        	availableToDo = availableToDo + "    <" + nextStates[i] + ">\n";
	        }
	        if (f.ProcessFSM(tmpmsg) == null)
	        {
	        	System.out.println("*** ERROR ***");
	        	System.out.println("    " + tmpmsg + " has no matching state transition");
	        	return "*** ERROR ***\n" + tmpmsg + " has no matching state transition. Current state is " + currentState + ".\n" + availableToDo;
	        }	
	    	currentState = f.getCurrentState();
System.out.println("Current state after execution is: <" + currentState + ">");
	        nextStates=f.getValidCommands();
	        availableToDo = "";
System.out.println("Valid next states after execution are:");

	        for (int i=0; (i < nextStates.length); i++)
	        {
System.out.println("    nextstate[" + i +"]: <" + nextStates[i] + ">");
	        	availableToDo = availableToDo + "    <" + nextStates[i] + ">\n";
	        }
        	return "Valid next states from " + currentState + " are:\n" + availableToDo;
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return retval;
    }
    
    static String FSMExecute(FSM f, String msg)
    {
    	String retval = "";
    	String availableToDo = "";
    	String tmpmsg = msg;

//System.out.println("FSMExecute(" + f + "," + tmpmsg + ")");
    	try {
	    	String currentState = f.getCurrentState();
System.out.println("Current state before execution is: <" + currentState + ">");
	        String nextStates[] = null;
	        nextStates=f.getValidCommands();
System.out.println("Valid next states before execution are:");
	        for (int i=0; (i < nextStates.length); i++)
	        {
System.out.println("    nextstate[" + i +"]: <" + nextStates[i] + ">");
	        	availableToDo = availableToDo + "    <" + nextStates[i] + ">\n";
	        }
	        if (f.ProcessFSM(tmpmsg) == null)
	        {
	        	System.out.println("*** ERROR ***");
	        	System.out.println("    " + tmpmsg + " has no matching state transition");
	        	return "*** ERROR ***\n" + tmpmsg + " has no matching state transition. Current state is " + currentState + ".\n" + availableToDo;
	        }	
	    	currentState = f.getCurrentState();
System.out.println("Current state after execution is: <" + currentState + ">");
	        nextStates=f.getValidCommands();
	        availableToDo = "";
System.out.println("Valid next states after execution are:");
	        for (int i=0; (i < nextStates.length); i++)
	        {
System.out.println("    nextstate[" + i +"]: <" + nextStates[i] + ">");
	        	availableToDo = availableToDo + "    <" + nextStates[i] + ">\n";
	        }
        	return "Valid next states from " + currentState + " are:\n" + availableToDo;
    	} 
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    	return retval;
    }
    
    public static String[] extractParametersFrom(URI uri, String delim)
    {
    	String[] parameters = null;
    	String input = uri.toString();
    	StringTokenizer st = new StringTokenizer(input,  delim);
        parameters = new String[st.countTokens()];

    	int i = 0;
        while (st.hasMoreTokens()) {
            parameters[i++] = st.nextToken();
        }
    	return parameters;
    }
}
