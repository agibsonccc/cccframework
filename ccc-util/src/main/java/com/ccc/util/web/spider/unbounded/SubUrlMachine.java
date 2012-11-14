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
 * This is a machine to check for urls in that don't have http in them.
 * 
 * @author adam GIbson
 *
 */
public class SubUrlMachine {
	public SubUrlMachine(){
		vertices=setupVertices();
		edges=setupEdges();
	}
	private Vertex<CharacterState>[] setupVertices(){
		vertices = new  Vertex[6];
		CharacterState c = new CharacterState();
		c.setAccepting(true);
		CharacterState c1 = new CharacterState();
		c.setAccepting(false);

		//Add the accepting states, and non acccepting states to the graph
		vertices[2]=states.insertVertex(c);
		vertices[2].put(accepting,true);
		vertices[3]=states.insertVertex(c);
		vertices[3].put(accepting, true);
		vertices[5]=states.insertVertex(c);
		vertices[5].put(accepting, true);
		vertices[4]=states.insertVertex(c1);
		vertices[4].put(accepting, false);
		vertices[0]=states.insertVertex(c1);
		vertices[0].put(accepting, false);
		vertices[1]=states.insertVertex(c1);
		vertices[1].put(accepting,false);
		//Debugging purposes
		for(int i=0;i<vertices.length;i++)
			vertices[i].put(key, i);
		return vertices;
	}
	private Edge<String>[] setupEdges(){
		edges = new Edge[13];

		//Add the character edges to connecting th evarious states.
		edges[0]=states.insertEdge(vertices[0], vertices[0], ALPHA_NUMERIC);
		edges[1]=states.insertEdge(vertices[0],vertices[1],".");
		edges[2]=states.insertEdge(vertices[1], vertices[1], ALPHA_NUMERIC);
		edges[3]=states.insertEdge(vertices[1],vertices[2],".");
		edges[4]=states.insertEdge(vertices[2],vertices[2],ALPHA_NUMERIC);
		edges[5]=states.insertEdge(vertices[2],vertices[3],"/");
		edges[6]=states.insertEdge(vertices[3], vertices[3], ALPHA_NUMERIC);
		edges[7]=states.insertEdge(vertices[3], vertices[2], "/");
		edges[8]=states.insertEdge(vertices[3],vertices[4],"?");
		edges[9]=states.insertEdge(vertices[4],vertices[4],ALPHA_NUMERIC);
		edges[10]=states.insertEdge(vertices[4],vertices[5],"=");
		edges[11]=states.insertEdge(vertices[5],vertices[4],"&");
		edges[12]=states.insertEdge(vertices[5],vertices[5],ALPHA_NUMERIC);


		return edges;
	}//end setupEdges
	/**
	 * This will traverse the graph, nondeterministically trying various paths
	 * to see if the input ends in an acceptings tate or not
	 * @param toCheck the string to check for url
	 * @param curr the current state the graph is on
	 * @param input the queue of input
	 * @return true if the url is valid, false otherwise
	 * @throws IllegalArgumentException if any of the arguments are null
	 */
	private List<Boolean> traverse( String toCheck,Vertex<CharacterState> curr, Queue<String> input) throws IllegalArgumentException {

		//Get access to the getElement() method.
		GraphVertex<CharacterState> v=(GraphVertex<CharacterState>) curr;
		if(trueValue)
			return possibleValues;


		//All input has been read.
		if(input.isEmpty()){
			//System.out.println("Key: " + v.get(key));
			//Accepting state, url is valid
			if((Boolean) v.get(accepting))
			{
				trueValue=true;
				possibleValues.add(true);
				return possibleValues;
			}
			return possibleValues;
		}
		//Loop till input is empty.
		while(!input.isEmpty()){
			//State character to compare.
			String currChar=input.remove();


			//Input is done, check the current character with the current state. 
			if(input.isEmpty()){
				//System.out.println("Key: " + v.get(key) + "Accepting: " + v.get(accepting));
				if((Boolean) v.get(accepting))
				{

					possibleValues.add(true);
					trueValue=true;
					return possibleValues;
				}
			}
			//Look for the next path to go to.
			List<Edge<String>> out=states.outGoingEdges(v);

			//Ensure input wasn't empty.
			if(input.isEmpty()){
				//System.out.println("Key: " + v.get(key) + "Accepting: " + v.get(accepting));

				if((Boolean) v.get(accepting))
				{
					possibleValues.add(true);
					trueValue=true;
					return possibleValues;
				}
			}


			//Traverse each of the possible matching paths.
			for(Edge<String> e: out){
				GraphEdge<String> e1=(GraphEdge<String>) e;
				if(e1.getE().contains(currChar)){
					if(trueValue)
						break;
					//Make a copy of the current input for the other traversals.
					Queue<String> q = new ArrayDeque<String>();
					q.addAll(input);
					traverse(toCheck,states.opposite(v, e1),q);
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
		return trueValue;

	}//end isUrl
	private final String ALPHA_NUMERIC="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789";
	private Vertex<CharacterState>[] vertices;
	private Edge<String>[] edges;
	private Graph<CharacterState,String> states = new DirectedGraph<CharacterState,String>();//Graph of states and paths
	List<Boolean> possibleValues = new CopyOnWriteArrayList<Boolean>();//Possible values of nondeterminism
	private Object key= new Object();
	private boolean trueValue;
	private Boolean accepting = new Boolean(true);
}
