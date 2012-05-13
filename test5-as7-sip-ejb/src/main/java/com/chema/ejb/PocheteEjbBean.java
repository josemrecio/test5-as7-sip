package com.chema.ejb;

import javax.ejb.*;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.CompositeName;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.servlet.sip.SipFactory;

@Stateless(name="TheEjb")
public class PocheteEjbBean implements PocheteEjbLocal {

    // OK @Resource
    // OK @Resource(mappedName="java:app/sip/test5/SipFactory")
    // NOK @Resource(mappedName="java:comp/env/sip/test5/SipFactory")
    @Resource(mappedName="java:sip/test5/SipFactory")
    private SipFactory sipFactoryResource;

    public void test() {

        System.out.println("***\nTheEjb - sipFactoryResource: " + sipFactoryResource + "\n***");
        try {
            // Getting the Sip factory from the JNDI Context
            Properties jndiProps = new Properties();
            //Context initCtx = new InitialContext(jndiProps);
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:app/sip/");
            SipFactory sipFactoryJndi = (SipFactory) envCtx.lookup("test5/SipFactory");
            System.out.println("***\nTheEjb - sipFactoryJndi: " + sipFactoryJndi + "\n***");
        } catch (NamingException e) {
            System.err.println("TheEjb - Error looking up SipFactory from JNDI: " + e);
        }
    }

}
