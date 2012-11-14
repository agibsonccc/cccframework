/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.web.spider.unbounded;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

import com.ccc.util.datastructure.graph.implementations.DirectedGraph;
import com.ccc.util.datastructure.graph.implementations.GraphEdge;
import com.ccc.util.datastructure.graph.implementations.GraphVertex;
import com.ccc.util.datastructure.graph.interfaces.Edge;
import com.ccc.util.datastructure.graph.interfaces.Graph;
import com.ccc.util.datastructure.graph.interfaces.Vertex;




/**
 * This is a Nondeterministic State Machine for URLs.
 * This is able to tell whether a given url is valid or not.
 * @author Adam Gibson
 *
 */
public class URLMachine {
	public URLMachine(){
		machine = new DirectedGraph<CharacterState,String>();
		vertices=setupVertices();
		edges=setupEdges();

	}

	/**
	 *
	 * @param toCheck the string to check for url
	 * @param curr the current state the graph is on
	 * @param input the queue of input
	 * @return true if the url is valid, false otherwise
	 * @throws IllegalArgumentException if any of the arguments are null
	 */
	private List<Boolean> traverse( String toCheck,Vertex<CharacterState> curr, Queue<String> input) throws IllegalArgumentException {
		
		//Get access to the getElement() method.
		GraphVertex<CharacterState> v=(GraphVertex<CharacterState>) curr;

		//Ensure there haven't been any successful computations, if there are: stop.
		for(Boolean b : possibleValues)
			if(b)
				return possibleValues;

		//All input has been read.
		if(input.isEmpty())
			//Accepting state, url is valid
			if(v.getElement().isAccepting())
			{
				possibleValues.add(true);
				return possibleValues;
			}
		//System.out.println("Vertex Index: " + curr.get(key));
		//Loop till input is empty.
		while(!input.isEmpty()){
			//State character to compare.
			String currChar=input.remove();

			//Ensure current state's characters match the current character, if it doesn't the URL is invalid.
			if(!v.getElement().getC().contains(currChar))
			{
				possibleValues.add(false);
				return possibleValues;
			}

			//Input is done, check the current character with the current state. 
			if(input.isEmpty())
				if(v.getElement().isAccepting())
				{
					possibleValues.add(true);
					return possibleValues;
				}

			//Look for the next path to go to.
			List<Edge<String>> out=machine.outGoingEdges(v);

			//Ensure input wasn't empty.
			if(input.isEmpty())
				if(v.getElement().isAccepting())
				{
					possibleValues.add(true);
					return possibleValues;
				}

			//Edge character, look for an edge containing the given character, there may be multiple paths.
			String pathChar=input.remove();

			//Traverse each of the possible matching paths.
			for(Edge<String> e: out){
				GraphEdge<String> e1=(GraphEdge<String>) e;
				if(e1.getE().contains(pathChar)){
					//Make a copy of the current input for the other traversals.
					Queue<String> q = new ArrayDeque<String>();
					q.addAll(input);
					traverse(toCheck,machine.opposite(v, e1),q);
				}
			}

		}

		return possibleValues;
	}//end traverse	


	/**
	 * This returns whether a URL is valid or not.
	 * @param toCheck the string to check for an url
	 * @return true if toCheck is a vaild url, false otherwise
	 * @throws IllegalArgumentException if toCheck is empty, or toCheck is null
	 */
	public boolean isUrl(String toCheck) throws IllegalArgumentException {
		
		Queue<String> q = new ArrayDeque<String>();
		//Queue up all of the elements in the input.
		for(int i=0;i<toCheck.length();i++)
			q.add(toCheck.charAt(i)+"");
		List<Boolean> ret=traverse(toCheck,vertices[0],q);

		//Look at all possible computations.
		for(Boolean b : ret)
			if(b)
				return b;

		return false;
	}//end isUrl

	/* Util method for setting up a character state */
	private CharacterState createCharacterState(String c,boolean accepting){
		CharacterState ret = new CharacterState();
		ret.setAccepting(accepting);
		ret.setC(c);
		return ret;
	}//end createCharacterState

	/**
	 * This sets up the vertices for the graoh.
	 * @return the array of vertices for the graph.
	 */
	private Vertex<CharacterState>[] setupVertices(){
		@SuppressWarnings("unchecked")
		Vertex<CharacterState>[] ret = new Vertex[18];
		for(int i=0;i<ret.length;i++){
			//Not an accepting state
			if(notTrueIndex(i))
				ret[i]=machine.insertVertex(createCharacterState(vertChars[i],false));
			//Accepting state
			else ret[i]=machine.insertVertex(createCharacterState(vertChars[i],true));
			//Let the vertex know it's own index, mainly used for debugging purposes.
			ret[i].put(key,i);
		}
		return ret;
	}//end setupVertices

	/* Accepting state */
	private boolean notTrueIndex(int num){
		return num!=11 && num!=8 && num!=7 && num!=9 && num!=6 && num!=17;
	}

	/**
	 * This sets up the vertices for the graph.
	 * @return the edges for the vertices.
	 */
	private Edge<String>[] setupEdges(){
		@SuppressWarnings("unchecked")
		Edge<String>[] ret =  new Edge[31];

		/* Set up each edge connecting them to their proper vertices. */
		for(int i=0;i<4;i++)
			ret[i]=machine.insertEdge(vertices[i], vertices[i+1], edgeChars[i]);

		ret[4]=machine.insertEdge(vertices[3], vertices[6], "/");
		ret[5]=machine.insertEdge(vertices[4], vertices[5], "w");
		ret[6]=machine.insertEdge(vertices[5], vertices[6], ".");
		ret[7]=machine.insertEdge(vertices[6], vertices[6], ALPHA_NUMERIC+"_");
		ret[8]=machine.insertEdge(vertices[6], vertices[7], ".");
		ret[9]=machine.insertEdge(vertices[7], vertices[7],ALPHA_NUMERIC);
		ret[10]=machine.insertEdge(vertices[7], vertices[8], "/");
		ret[11]=machine.insertEdge(vertices[8], vertices[8],ALPHA_NUMERIC);
		ret[12]=machine.insertEdge(vertices[8], vertices[9], ".");
		ret[13]=machine.insertEdge(vertices[9], vertices[9],ALPHA_NUMERIC);
		ret[14]=machine.insertEdge(vertices[9], vertices[10], "?");
		ret[15]=machine.insertEdge(vertices[10], vertices[10], ALPHA_NUMERIC);
		ret[16]=machine.insertEdge(vertices[10], vertices[11], "=");
		ret[17]=machine.insertEdge(vertices[11], vertices[11], ALPHA_NUMERIC);
		ret[18]=machine.insertEdge(vertices[11], vertices[10], "&");
		ret[19]=machine.insertEdge(vertices[8], vertices[7], "/");
		ret[20]=machine.insertEdge(vertices[7], vertices[6],".");

		ret[21]=machine.insertEdge(vertices[12], vertices[6], ALPHA_NUMERIC);
		ret[22]=machine.insertEdge(vertices[14], vertices[6], ALPHA_NUMERIC);
		ret[23]=machine.insertEdge(vertices[13], vertices[12], "w");
		ret[24]=machine.insertEdge(vertices[14], vertices[13], "w");
		ret[25]=machine.insertEdge(vertices[15], vertices[14], "/");
		ret[26]=machine.insertEdge(vertices[1], vertices[15], "p");
		ret[27]=machine.insertEdge(vertices[9], vertices[16], ALPHA_NUMERIC);
		ret[28]=machine.insertEdge(vertices[16],vertices[10],"?");
		ret[29]=machine.insertEdge(vertices[8],vertices[17],ALPHA_NUMERIC);
		ret[30]=machine.insertEdge(vertices[17],vertices[9],ALPHA_NUMERIC);

		/* Let each edge know it's index, mainly for debugging purposes. */
		for(int i=0;i<31;i++)
			ret[i].put(i, i);
		return ret;


	}
	/**
	 * @return the vertices
	 */
	public Vertex<CharacterState>[] getVertices() {
		return vertices;
	}

	/**
	 * @return the edges
	 */
	public Edge<String>[] getEdges() {
		return edges;
	}



	private Vertex<CharacterState>[] vertices;
	private Edge<String>[] edges;
	private Graph<CharacterState,String> machine;
	private final String ALPHA_NUMERIC="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789";
	List<Boolean> possibleValues = new CopyOnWriteArrayList<Boolean>();
	private String key="";	   /*0    1   2   3   4  5      6               7          8                           9                10                 11       12 13  14  15  16  17     */
	private String[] vertChars={"h","t","s","/","w","w",ALPHA_NUMERIC+"-=?&./",ALPHA_NUMERIC+"-./",ALPHA_NUMERIC+"!#"+"/.?=&-",ALPHA_NUMERIC+"!#-",ALPHA_NUMERIC,ALPHA_NUMERIC,".","w","/",":",".","/"};
	private String[] edgeChars={"t","p",":","/","/","w",".",ALPHA_NUMERIC,".",ALPHA_NUMERIC,"/",ALPHA_NUMERIC,".",ALPHA_NUMERIC,"?",ALPHA_NUMERIC,ALPHA_NUMERIC,"&","/",".",ALPHA_NUMERIC,ALPHA_NUMERIC,"w","w","/","p","."};
}


