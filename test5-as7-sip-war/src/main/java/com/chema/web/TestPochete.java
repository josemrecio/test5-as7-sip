package com.chema.web;

import java.io.IOException;

import javax.annotation.Resource;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.CompositeName;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

import javax.ejb.EJB;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletContextEvent;
import javax.servlet.sip.ServletTimer;
import javax.servlet.sip.TimerListener;
import javax.servlet.sip.TimerService;
import javax.servlet.sip.SipServletListener;

import com.chema.ejb.PocheteEjb;
import com.chema.ejb.PocheteEjbLocal;

/**
 * Servlet implementation class TestPochete
 */
public class TestPochete extends SipServlet implements TimerListener, SipServletListener {
    private static final long serialVersionUID = 1L;
    @EJB(name="TheEjb")
    private PocheteEjbLocal theEjb;

//    @Resource
//    private SipFactory sipFactoryResource;
    @Resource(mappedName="java:sip/test5/SipFactory")
    private SipFactory sipFactoryResource;

    public TestPochete() {
        super();
    }

    private boolean initialized = false;

    public void servletInitialized(SipServletContextEvent ev) {
        System.out.println("servlet " + ev.getSipServlet().getServletName() + ": servletInitialized()");
        if (!initialized) {
            initialized = true;
        }

        System.out.println("***\nsipFactoryResource: " + sipFactoryResource + "\n***");

        SipFactory sipFactoryAttr = (SipFactory) getServletContext().getAttribute(SIP_FACTORY);
        System.out.println("***\nsipFactoryAttr: " + sipFactoryAttr + "\n***");

        try {
            // Getting the Sip factory from the JNDI Context
            Properties jndiProps = new Properties();
            //Context initCtx = new InitialContext(jndiProps);
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:app/sip/");
            SipFactory sipFactoryJndi = (SipFactory) envCtx.lookup("test5/SipFactory");
            System.out.println("***\nsipFactoryJndi: " + sipFactoryJndi + "\n***");
        } catch (NamingException e) {
            System.err.println("Error looking up SipFactory from JNDI: " + e);
        }

        SipApplicationSession sas = sipFactoryAttr.createApplicationSession();
        TimerService timerService = (TimerService) getServletContext().getAttribute(TIMER_SERVICE);
        timerService.createTimer(sas, 10000, false, null);

        System.out.println("servlet " + ev.getSipServlet().getServletName() + " has been initialized");
    }


    @SuppressWarnings("unchecked")
    public void timeout(ServletTimer timer) {
        System.out.println("****** ServletTimer timeout event " + timer.getId() + " ********* ");
        try {
            theEjb.test();
        } catch (Exception e) {
            e.printStackTrace();
        }
       System.out.println("Dump Jndi view for SIP Servlet:");
        listJndi();
    }

    private static final String INDENT_SPACE = "  ";
    private static final String INDENT_UNIT = "\\__";

    private static final void listContext(Hashtable<String, String> env, String lookupString, Context ctx, String indent) {
        try {
            NamingEnumeration<NameClassPair> list = ctx.list("");
            while (list.hasMore()) {
                NameClassPair item = (NameClassPair) list.next();
                String name = item.getName();

                if (item.getClassName().contentEquals("javax.naming.Context")) {
                    System.out.println(indent + name);
                    if (!lookupString.endsWith("/")) {
                        lookupString = lookupString + "/";
                    }
                    Object o = new InitialContext(env).lookup(lookupString + name);
                    try {
                        ctx = (Context)o;
                        listContext(env, lookupString + name, ctx, indent + INDENT_SPACE);
                    } catch (ClassCastException e) {
                        System.out.println(indent + INDENT_UNIT + "ERROR: class can't be cast to context:" + o.getClass().getName());
                    }
                }
                else {
                    System.out.println(indent + INDENT_UNIT + item.getClassName() + " " + name);
                }
            }
        }
        catch (Exception ex) {
            try {
                System.out.print("context: " + ctx.getNameInNamespace() + ": ");
            } catch (NamingException e) {
                System.err.println(e);
            }
            System.out.print( ex + "\n");
        }
    }

    protected void listJndi() {
        
        Hashtable<String, String> env = new Hashtable<String, String>();
        env.clear();
        final String initialIndent = ""; 
        String lookupString = "/";
        Context ctx=null;
        try {
            ctx = (Context)new InitialContext(env).lookup(lookupString);
            System.out.println(initialIndent + lookupString + " context:");
            listContext(env,lookupString,ctx, initialIndent);
        } catch (NamingException e) {
            e.printStackTrace();
        }

        lookupString = "java:";
        try {
            ctx = (Context)new InitialContext(env).lookup(lookupString);
            System.out.println(initialIndent + lookupString + " context:");
            listContext(env,lookupString,ctx, initialIndent);
        } catch (NamingException e) {
            System.err.println(e);
        }

        lookupString = "java:app";
        try {
            ctx = (Context)new InitialContext(env).lookup(lookupString);
            System.out.println(initialIndent + lookupString + " context:");
            listContext(env,lookupString,ctx, initialIndent);
        } catch (NamingException e) {
            System.err.println(e);
        }

        lookupString = "java:global";
        try {
            ctx = (Context)new InitialContext(env).lookup(lookupString);
            System.out.println(initialIndent + lookupString + " context:");
            listContext(env,lookupString,ctx, initialIndent);
        } catch (NamingException e) {
            System.err.println(e);
        }

        lookupString = "java:comp";
        try {
            ctx = (Context)new InitialContext(env).lookup(lookupString);
            System.out.println(initialIndent + lookupString + " context:");
            listContext(env,lookupString,ctx, initialIndent);
        } catch (NamingException e) {
            System.err.println(e);
        }

        System.out.flush();

        System.out.println("Done...");

    }

}
