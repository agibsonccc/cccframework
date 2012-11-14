	Java Naming and Directory Interface(TM) (JNDI)
	  File System Service Provider Release Notes
                          Beta 3
		      	Mar 29, 2000

This is the 1.2 beta 3 version of the JNDI file system service provider.
Please send your feedback on the file system service provider to us at
jndi@java.sun.com, or to the public mailing list at
jndi-interest@java.sun.com.


CHANGES SINCE 1.2 BETA 2

Here are the highlights:

- Environment methods clone input and output as per JNDI spec

- Support correct handling of file URLs on Windows platform 

- Do not throw NullPointerException when invalid URL supplied

- Do not remove other bindings with same name prefix when 
  unbinding/rebinding

- Allow a RefAddr's contents to be null

RELEASE INFORMATION

This release contains:

lib/fscontext.jar
	Archive of class files for the service provider.

lib/providerutil.jar
	Utilities used by service providers developed by Sun Microsystems.
	The file system service provider uses some of the classes in this
	archive.  This archive file is interchangeable with the
	providerutil.jar file that you might have downloaded with one of
	the other service providers currently available from Sun Microsystems.

doc/providers/jndi-fs-ext.html
doc/providers/jndi-fs.html
	Documentation of the service provider.

The classes in this release have been generated using the Java(TM) 2 SDK,
Standard Edition, v1.2.1.


ADDITIONAL INFORMATION

examples/api (available as part of the general JNDI1.2 distribution)
	Generic examples for accessing any naming and
	directory service. You can use the naming examples (List,
	Lookup, and Rename) with this service provider. 
	See examples/api/README and doc/providers/jndi-fs.html.

examples/browser (available as a separate download)
	A JNDI browser for browsing any naming and directory
	service, including the file system. 
        See examples/browser/README-DEMO.txt.

http://java.sun.com/products/jndi/1.2/javadoc
	JNDI 1.2 javadoc.
