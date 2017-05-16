package com.estafet;

import java.nio.file.Paths;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/fsmserver")
public class FSMService {
	
	@GET
	@Path("/api")
	public String sayHello() {
		java.nio.file.Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		Shell shell = new Shell();
		String result = shell.executeCommand("sh ./src/main/resources/bin/abc.sh");
		return "<h1>Gello World of the GET ::: " + s + "</h1>" + "<br/>" + result;
	}
	
	@POST
	@Path("/api/{command}")
	public Response postString(@PathParam("command") final String command, String body) {
		System.out.println(command);
		System.out.println(body);
		
		java.nio.file.Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + s);
		
		Shell shell = new Shell();
		System.out.println(shell.executeCommand("ping -n 3 google.com"));
		
		System.out.println(shell.executeCommand("C:\\JEES\\wildfly-10.1.0.Final\\standalone\\deployments\\FSMDemo.war\\WEB-INF\\classes\\com\\estafet\\abc.bat"));
		
		System.out.println(shell.executeCommand("../../abc.bat"));
		
		
		
		return Response.ok("<h1>Hello World of the POST</h1>", MediaType.TEXT_PLAIN).build();
	}
}
