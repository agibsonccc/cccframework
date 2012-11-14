package com.ccc.camelcomponents.ical;

import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;

public class ICalHttpEntity extends MultipartEntity {
	public ICalHttpEntity(HttpMultipartMode browserCompatible) {
		super(browserCompatible);
	}

	protected String generateContentType(
            final String boundary,
            final Charset charset) {
        StringBuilder buffer = new StringBuilder();
        buffer.append("text/calendar; boundary=");
        buffer.append(boundary);
        if (charset != null) {
            buffer.append("; charset=");
            buffer.append(charset.name());
        }
        return buffer.toString();
    }
}
