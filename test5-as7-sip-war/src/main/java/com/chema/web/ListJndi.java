package com.chema.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class listjndi
 */
public class ListJndi extends HttpServlet {

	private static final long serialVersionUID = 4429170068994125450L;

	private static final String INDENT_SPACE = "  ";
    private static final String INDENT_UNIT = "\\__";

    private static final void listContext(Hashtable<String, String> env, String lookupString, Context ctx, String indent, PrintWriter out) {
    	try {
    		NamingEnumeration<NameClassPair> list = ctx.list("");
    		while (list.hasMore()) {
    			NameClassPair item = (NameClassPair) list.next();
    			String name = item.getName();

    			if (item.getClassName().contentEquals("javax.naming.Context")) {
    				out.println(indent + name);
    				if (!lookupString.endsWith("/")) {
    					lookupString = lookupString + "/";
    				}
    				Object o = new InitialContext(env).lookup(lookupString + name);
    				try {
						ctx = (Context)o;
	    				listContext(env, lookupString + name, ctx, indent + INDENT_SPACE,out);
					} catch (ClassCastException e) {
	    				out.println(indent + INDENT_UNIT + "ERROR: class can't be cast to context:" + o.getClass().getName());
					}
    			}
    			else {
    				out.println(indent + INDENT_UNIT + item.getClassName() + " " + name);
    			}
    		}
    	}
    	catch (NamingException ex) {
    		ex.printStackTrace();
    	}
    	catch (Exception ex) {
    		try {
    			System.out.println("context: " + ctx.getNameInNamespace());
    		} catch (NamingException e) {
    			e.printStackTrace();
    		}
    		ex.printStackTrace();
    	}
    }

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListJndi() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Hashtable<String, String> env = new Hashtable<String, String>();
		
//		// AS 7.0.1
//		env.put(Context.PROVIDER_URL, "jnp://localhost:1099"); //"jnp://192.168.0.187:1099");
//		//env.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
//		env.put(InitialContext.URL_PKG_PREFIXES,"org.jboss.naming:org.jnp.interfaces");
		
		// AS 7.1.0Beta1b
		env.clear();
		
		Context ctx=null;
		PrintWriter out = response.getWriter();
		
		final String initialIndent = ""; 

		String lookupString = "/";
		try {
			ctx = (Context)new InitialContext(env).lookup(lookupString);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		out.println(initialIndent + lookupString + " context:");
		listContext(env,lookupString,ctx, initialIndent,out);

		lookupString = "java:";
		try {
			ctx = (Context)new InitialContext(env).lookup(lookupString);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		out.println(initialIndent + lookupString + " context:");
		listContext(env,lookupString,ctx, initialIndent,out);

		lookupString = "java:app";
		try {
			ctx = (Context)new InitialContext(env).lookup(lookupString);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		out.println(initialIndent + lookupString + " context:");
		listContext(env,lookupString,ctx, initialIndent,out);

		lookupString = "java:global";
		try {
			ctx = (Context)new InitialContext(env).lookup(lookupString);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		out.println(initialIndent + lookupString + " context:");
		listContext(env,lookupString,ctx, initialIndent,out);

                lookupString = "java:app/sip";
                try {
                        ctx = (Context)new InitialContext(env).lookup(lookupString);
                } catch (NamingException e) {
                        e.printStackTrace();
                }
                out.println(initialIndent + lookupString + " context:");
                listContext(env,lookupString,ctx, initialIndent,out);



		out.flush();

		System.out.println("Hecho...");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
