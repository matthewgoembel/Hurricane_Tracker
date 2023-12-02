import java.util.*;

/*
 * Author: Matthew Goembel, mgoembel2022@my.fit.edu
 * Course: CSE 2010
 * Section: 02, Fall 2023
 * Description: Tree.java is responsible for
 * creating tree nodes that will be stored in a
 * tree structure.
 * Contains methods to get the name
 * of the node, get all the children of a node,
 * add children to a node and sort.
 */

public class Tree implements Comparable<Tree> {
   private String name;         // Name of the node
   private List<Tree> children; // List holding all children nodes

   // Create a new Tree node
   public Tree(String name) {
      this.name = name;
      this.children = new LinkedList<>();
   }

   /**
    * get string name of node
    * @return name
    */
   public String getName() {
      return name;
   }

   /**
    * Adds the node as a child of the parent called
    * @param childNode node to be added
    */
   public void addChild(Tree childNode) {
      children.add(childNode);
   }

   /**
    * returns the list of children or sub_nodes
    * that belong to the parent node
    * @return List of children of called parent node
    */
   public List<Tree> getChildren() {
      return children;
   }

   /**
    * Overridden to implement .sort() with all tree nodes
    * @param otherTree the object to be compared.
    * @return correct lexicographical order
    */
   @Override
   public int compareTo(Tree otherTree) {
      // Compare Tree objects based on their names
      return this.name.compareTo(otherTree.getName());
   }
}