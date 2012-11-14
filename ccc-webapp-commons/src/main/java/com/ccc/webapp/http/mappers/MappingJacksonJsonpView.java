package com.ccc.webapp.http.mappers;
/*
 *    Copyright 2012  Vivin Paliath

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;
/**
 * http://vivin.net/2011/07/01/implementing-jsonp-in-spring-mvc-3-0-x/
 * <bean class="org.springframework.web.servlet.view.ContentNegotiatingViewResolver">
    <property name="favorPathExtension" value="true"/>
    <property name="mediaTypes">
        <map>
            <entry key="json" value="application/json"/>
            <entry key="jsonp" value="application/javascript"/>
        </map>
    </property>
    <property name="defaultViews">
        <list>
            <bean class="org.springframework.web.servlet.view.json.MappingJacksonJsonView"/>
            <bean class="net.vivin.mvc.spring.view.jsonp.MappingJacksonJsonpView"/>
        </list>
    </property>
</bean>
 * @author Vivin Paliath
 *
 */
public class MappingJacksonJsonpView extends MappingJacksonJsonView {

	/**
	 * Default content type. Overridable as bean property.
	 */
	public static final String DEFAULT_CONTENT_TYPE = "application/javascript";

    @Override
    public String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }

 	/**
	 * Prepares the view given the specified model, merging it with static
	 * attributes and a RequestContext attribute, if necessary.
	 * Delegates to renderMergedOutputModel for the actual rendering.
	 * @see #renderMergedOutputModel
	 */
    @Override
	public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        if("GET".equals(request.getMethod().toUpperCase())) {
            @SuppressWarnings("unchecked")
            Map<String, String[]> params = request.getParameterMap();

            if(params.containsKey("callback")) {
                response.getOutputStream().write(new String(params.get("callback")[0] + "(").getBytes());
                super.render(model, request, response);
                response.getOutputStream().write(new String(");").getBytes());
                response.setContentType("application/javascript");
            }

            else {
                super.render(model, request, response);
            }
        }

        else {
            super.render(model, request, response);
        }
    }
    
  
}