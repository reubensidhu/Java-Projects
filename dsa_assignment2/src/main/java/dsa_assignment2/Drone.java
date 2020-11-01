package dsa_assignment2;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * A Drone class to simulate the decisions and information collected by a drone
 * on exploring an underground maze.
 * 
 */
public class Drone implements DroneInterface
{
	private static final Logger logger     = Logger.getLogger(Drone.class);
	
	public String getStudentID()
	{
		//change this return value to return your student id number
		return "1993805";
	}

	public String getStudentName()
	{
		//change this return value to return your name
		return "Reuben Sidhu";
	}

	/**
	 * The Maze that the Drone is in
	 */
	private Maze               maze;

	/**
	 * The stack containing the portals to backtrack through when all other
	 * doorways of the current chamber have been explored (see assignment
	 * handout). Note that in Java, the standard collection class for both
	 * Stacks and Queues are Deques
	 */
	private Deque<Portal>       visitStack = new ArrayDeque<>();

	/**
	 * The set of portals that have been explored so far.
	 */
	private Set<Portal>         visited    = new HashSet<>();

	/**
	 * The Queue that contains the sequence of portals that the Drone has
	 * followed from the start
	 */
	private Deque<Portal>       visitQueue = new ArrayDeque<>();

	/**
	 * This constructor should never be used. It is private to make it
	 * uncallable by any other class and has the assert(false) to ensure that if
	 * it is ever called it will throw an exception.
	 */
	@SuppressWarnings("unused")
	private Drone()
	{
		assert (false);
	}

	/**
	 * Create a new Drone object and place it in chamber 0 of the given Maze
	 * 
	 * @param maze
	 *            the maze to put the Drone in.
	 */
	public Drone(Maze maze)
	{
		this.maze = maze;
	}

	/* 
	 * @see dsa_assignment2.DroneInterface#searchStep()
	 */
	@Override
	public Portal searchStep()
	{
		/* WRITE YOUR CODE HERE */
		int NumDoors =  maze.getNumDoors();
		int chamber = maze.getCurrentChamber();
		for (int i=0; i < NumDoors; i++) {
			Portal entry = new Portal(chamber,i);
			if (!(visited.contains(entry))) {
				visited.add(entry);
				visitQueue.add(entry);
				Portal exit=maze.traverse(i);
				visitStack.addFirst(exit);
				visited.add(exit);
				visitQueue.add(exit);
				return exit;
			}
		}
		//backtrack
		
		if(visitStack.isEmpty()) {
			return null;
		}
		else {
			int btdoor = visitStack.removeFirst().getDoor();
			Portal entry = new Portal(maze.getCurrentChamber(), btdoor);
			visitQueue.add(entry);
			Portal exit = maze.traverse(btdoor);
			visitQueue.add(exit);
			return exit;
		}
	}

	/* 
	 * @see dsa_assignment2.DroneInterface#getVisitOrder()
	 */
	@Override
	public Portal[] getVisitOrder()
	{
		/* WRITE YOUR CODE HERE */
		Portal[] arr1 = new Portal[(visitQueue.size())];
		Object[] arr = visitQueue.toArray();
		for (int i=0; i<visitQueue.size(); i++) {
			arr1[i] = (Portal) arr[i];
			
		}
		return arr1;
	}

	/*
	 * @see dsa_assignment2.DroneInterface#findPathBack()
	 */
	@Override
	public Portal[] findPathBack()
	{
		/* WRITE YOUR CODE HERE */
		//for i in visit queue if chamber= currentchamber traverse thorugh this portal and repeat
		//the portal you end up at is the one next to this one in the queue.use while true
		
		int chamber = maze.getCurrentChamber();
		Portal[] queue = getVisitOrder();
		ArrayList<Portal> arr = new ArrayList<>();
		while (!(chamber==0)) {
			for(int i=0; i<queue.length; i++) {
				if (queue[i].getChamber()==chamber) {
					arr.add(queue[i]);
					chamber = queue[i-1].getChamber();
					break;
				}
			}
		}
		Portal[] arr1 = new Portal[(arr.size())];
		Object[] pathback = arr.toArray();
		for (int i=0; i<arr.size(); i++) {
			arr1[i] = (Portal) pathback[i];
		}
		return arr1;
	}
}

//}
