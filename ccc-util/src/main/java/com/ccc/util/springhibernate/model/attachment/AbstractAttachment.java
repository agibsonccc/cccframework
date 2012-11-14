package com.ccc.util.springhibernate.model.attachment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.apache.commons.io.IOUtils;
import org.springframework.util.Assert;

import com.ccc.util.springhibernate.model.AbstractModel;


/**
 * This is a base class for handling attachments related to blobs among other things
 * in jdbc/hibernate
 * @author Adam Gibson
 *
 */
@MappedSuperclass
public abstract class AbstractAttachment  extends AbstractModel implements Serializable {

	/**
	 * 
	 */
	protected static final long serialVersionUID = -299782850280239575L;

	/**
	 * This will set the given file as a file and take care of converting to a byte array.
	 * This method will only return when passed null input
	 * @param input the file to input
	 * @throws IOException if there's a problem with conversion
	 */
	public void setfileAsFile(File input) throws IOException {
		file=IOUtils.toByteArray(new FileInputStream(input));
		Assert.notNull(file,"Failed to convert input");
	}//end setfileAsFile
	/**
	 * This will return the byte file array in this message
	 * as a buffered file
	 * @return the file as a buffered file
	 * @throws IOException if a problem occurs translating the file
	 */
	public File fileFromBytefile() throws IOException {
		if(file==null || file.length <1)
			return null;
		File ret = new File(fileName);
		BufferedOutputStream fos = new BufferedOutputStream(new FileOutputStream(ret));
		BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(file));
		IOUtils.copy(bis,fos);
		return ret;
	}//end fileFromBytefile


	/**
	 * This will convert a blob to a byte array
	 * @param fromBlob the blob to convert
	 * @return if one occurs during conversion
	 */
	protected byte[] toByteArray(Blob fromBlob) {
		if(fromBlob==null)
			return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			return toByteArrayImpl(fromBlob, baos);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (IOException ex) {
				}
			}
		}
	}//endtoByteArray 





	public byte[] getfile() {
		return file;
	}

	public void setfile(byte[] file) {
		this.file = file;
	}

	/** Don't invoke this.  Used by Hibernate only. */
	public void setfileBlob(Blob fileBlob) {
		this.file = this.toByteArray(fileBlob);
	}

	/**
	 * This will copy the given blob to a byte array using the given output 
	 * stream
	 * @param fromBlob the blob to copy from
	 * @param baos the output stream to write to
	 * @return a byte array from the given blob and outputstream
	 * @throws SQLException
	 * @throws IOException
	 */
	protected byte[] toByteArrayImpl(Blob fromBlob, ByteArrayOutputStream baos)
			throws SQLException, IOException {
		byte[] buf = new byte[4000];
		InputStream is = fromBlob.getBinaryStream();
		try {
			for (;;) {
				int dataSize = is.read(buf);

				if (dataSize == -1)
					break;
				baos.write(buf, 0, dataSize);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ex) {
				}
			}
		}
		return baos.toByteArray();
	}//end toByteArrayImpl

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getFile() {
		return file;
	}
	public void setFile(byte[] file) {
		this.file = file;
	}

	
	
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}


	@Column(name="content_type")
	protected String contentType;
	@Column(name="file_name")
	protected String fileName;
	@Column(name="file")
	protected byte[] file;
	
	
}
