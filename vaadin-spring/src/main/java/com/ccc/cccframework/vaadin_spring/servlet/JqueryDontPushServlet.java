package com.ccc.cccframework.vaadin_spring.servlet;
import java.io.BufferedWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.vaadin.dontpush.server.DontPushOzoneServlet;

import com.vaadin.Application;
import com.vaadin.ui.Window;
public class JqueryDontPushServlet extends DontPushOzoneServlet{
	  @Override
	    protected void writeAjaxPageHtmlVaadinScripts(Window window,
	            String themeName, Application application, BufferedWriter page,
	            String appUrl, String themeUri, String appId,
	            HttpServletRequest request) throws ServletException, IOException {

	        page.write("<script type=\"text/javascript\">\n");
	        page.write("//<![CDATA[\n");
	        page.write("document.write(\"<script language='javascript' src='https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js'><\\/script>\");\n");
	        page.write("//]]>\n</script>\n");

	        super.writeAjaxPageHtmlVaadinScripts(window, themeName, application,
	            page, appUrl, themeUri, appId, request);
	    }
}
