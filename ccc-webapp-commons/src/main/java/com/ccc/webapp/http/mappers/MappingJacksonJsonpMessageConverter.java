package com.ccc.webapp.http.mappers;

import java.io.IOException;

import org.codehaus.jackson.JsonEncoding;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;

public class MappingJacksonJsonpMessageConverter extends
		MappingJacksonHttpMessageConverter {

	  @Override
	    protected void writeInternal(Object object, HttpOutputMessage outputMessage)
	        throws IOException, HttpMessageNotWritableException {
	        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());
	       
	        JsonGenerator jsonGenerator = this.getObjectMapper().getJsonFactory().createJsonGenerator(outputMessage.getBody(), encoding);        
	 
	        try {
	            String jsonPadding = "callback";            
	 
	            // If the callback doesnt provide, use the default callback
	            if (object instanceof JsonObject) {
	                String jsonCallback = ((JsonObject)object).getJsonCallback();
	                if (jsonCallback != null) {
	                    jsonPadding = jsonCallback;
	                }
	            }            
	 
	            jsonGenerator.writeRaw(jsonPadding);
	            jsonGenerator.writeNull();
	            this.getObjectMapper().writeValue(jsonGenerator, object);
	            jsonGenerator.writeNull();
	            jsonGenerator.flush();
	        } catch (JsonProcessingException ex) {
	            throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getMessage(), ex);
	        }
	    }

}
