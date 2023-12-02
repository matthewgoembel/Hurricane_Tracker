import java.util.*;
import java.io.*;

/*
 * Author: Matthew Goembel, mgoembel2022@my.fit.edu
 * Course: CSE 2010
 * Section: 02, Fall 2023
 * Description: HW3 Main class uses the Tree class to
 * build a tree of hurricanes > category > state > name.
 * The main method reads in the datafile and builds a
 * corresponding tree using methods from Tree.java to
 * make roots, parents, and children.
 * The program then reads the queries file, and uses the
 * tree created to get such information to handle and
 * print out each case.
 */

public class Hurricane {
   public static void main (final String[] args) {
      // Data and query files
      final String data = args[0];
      final String queries = args[1];

      // Create root node to start tree
      Tree hurricaneTree = null;
      // Read in the data file and create the tree
      try (final BufferedReader line = new BufferedReader(new FileReader(data))) {
         String current;
         while ((current = line.readLine()) != null) {
            final String[] categories = current.split(" "); // Split into query array
            // For first line only (initialize root and categories)
            if (hurricaneTree == null) {
               hurricaneTree = new Tree(categories[0]);          // Add root node
               for (int i = 1; i < categories.length; i++) {
                  final Tree child = new Tree(categories[i]);
                  hurricaneTree.addChild(child);                 // Add category to root
               }
            // Other queries
            } else {
               // Find matching category node
               Tree parent;
               if (categories[0].startsWith("c")) {
                  parent = findNode(categories[0], hurricaneTree);
                  // Add states to category
                  for (int i = 1; i < categories.length; i++) {
                     final String stateName = categories[i].substring(0, 2);
                     final Tree state = new Tree(stateName);
                     assert parent != null;
                     parent.addChild(state);
                  }
               } else {
                  // if hurricane name query
                  parent = findNode(categories[0].substring(3,7), hurricaneTree);
                  for (Tree t : parent.getChildren()) {
                     if (t.getName().equals(categories[0].substring(0,2))) {
                        parent = t;
                     }
                  }
                  // Add states to category
                  for (int i = 1; i < categories.length; i++) {
                     final String hurricaneName = categories[i];
                     final Tree name = new Tree(hurricaneName);
                     parent.addChild(name);
                  }
               }
            }
         }
      } catch (IOException e) {
         e.printStackTrace();
      }

      // Create Cases to handle each possible query
      try (BufferedReader line = new BufferedReader(new FileReader(queries))) {
         String curr;
         while ((curr = line.readLine()) != null) {
            final String[] queryParts = curr.split(" "); // Split into a str array
            final String queryType = queryParts[0];            // What the query is asking
            Set<String> uniqueNames = new HashSet<>();         // Set to avoid non duplicates
            switch (queryType) {
               case "GetNamesByCategory" -> {
                  final String category = queryParts[1];
                  // Find category node
                  Tree categoryNode = findNode(category, hurricaneTree);
                  // Add all children to names list
                  List<Tree> names = new LinkedList<>();
                  for (final Tree state : categoryNode.getChildren()) {
                     names.addAll(state.getChildren());
                  }
                  // Sort names alphabetically
                  names.sort(null);
                  // Print out all children in List
                  System.out.printf("%s %s ", queryType, category);
                  for (final Tree t : names) {
                     System.out.printf("%s ", t.getName());
                  }
                  System.out.println();
               }
               case "GetNamesByState" -> {
                  final String state = queryParts[1];
                  // Collect all given states by category
                  List<Tree> allStates = new LinkedList<>();
                  for (Tree t : hurricaneTree.getChildren()) {
                     Tree stateNode = findNode(state, t);
                     if (stateNode != null) {
                        allStates.add(stateNode);

                     }
                  }
                  // Get children from each state
                  List<Tree> stateNames = new LinkedList<>();
                  for (Tree t : allStates) {
                     stateNames.addAll(t.getChildren());
                  }
                  // Remove duplicates and sort names alphabetically
                  for (Tree t : stateNames) {
                     uniqueNames.add(t.getName());
                  }
                  // Print out names format
                  System.out.printf("%s %s ", queryType, state);
                  printFormat(uniqueNames);
               }
               case "GetNamesByCategoryAndState" -> {
                  final String category1 = queryParts[1];
                  final String state1 = queryParts[2];
                  // Find names from given category_state
                  Tree catNode = findNode(category1, hurricaneTree);
                  Tree catStateNode = findNode(state1, catNode);
                  List<Tree> catStateNames = new LinkedList<>();
                  catStateNames.addAll(catStateNode.getChildren());
                  // Remove duplicates and sort names alphabetically
                  for (Tree t : catStateNames) {
                     uniqueNames.add(t.getName());
                  }
                  // Print out names format
                  System.out.printf("%s %s %s ", queryType, category1, state1);
                  printFormat(uniqueNames);
               }
               case "GetNamesWithMultipleStates" -> {
                  Set<String> multipleStateNames = new HashSet<>();
                  // Iterate down to state_name level of tree
                  for (Tree c : hurricaneTree.getChildren()) {
                     for (Tree s : c.getChildren()) {
                        for (Tree n : s.getChildren()) {
                           String name = n.getName();
                           // If name is under multiple states add
                           if (uniqueNames.contains(name)) {
                              multipleStateNames.add(name);
                           }
                           // else add to set, keep looking
                           uniqueNames.add(name);
                        }
                     }
                  }
                  // Print
                  System.out.println("GetNamesWithMultipleStates");
                  printFormat(multipleStateNames);
               }
               case "GetNamesWithMultipleCategories" -> {
                  Set<String> multipleCategoryNames = new HashSet<>();
                  // Iterate down to the state_name level of the tree
                  for (Tree c : hurricaneTree.getChildren()) {
                     for (Tree s : c.getChildren()) {
                        for (Tree n : s.getChildren()) {
                           String name = n.getName();
                           // If the name is repeated, add to the list to print
                           if (uniqueNames.contains(name)) {
                              multipleCategoryNames.add(name);
                           }
                           // else add to set
                           uniqueNames.add(name);
                        }
                     }
                  }
                  // Print
                  System.out.println("GetNamesWithMultipleCategories");
                  printFormat(multipleCategoryNames);
               }
               case "GetCategory" -> {
                  final String categoryName = queryParts[1];
                  // Iterate through each category
                  for (Tree c : hurricaneTree.getChildren()) {
                     if (findNode(categoryName, c) != null) {
                        uniqueNames.add(c.getName());  // Add name if in subtree of category
                     }
                  }
                  System.out.printf("%s %s ", queryType, queryParts[1]);
                  printFormat(uniqueNames);
               }
               case "GetState" -> {
                  final String stateName = queryParts[1];
                  // Iterate through each state looking for name
                  for (Tree c : hurricaneTree.getChildren()) {
                     for (Tree s : c.getChildren()) {
                        // If the name child of the state, add to list
                        if (findNode(stateName, s) != null) {
                           uniqueNames.add(s.getName());
                        }
                     }
                  }
                  // Print
                  System.out.printf("%s %s ", queryType, queryParts[1]);
                  printFormat(uniqueNames);
               }
               default -> System.out.printf("%s%n", "Invalid query type: " + queryType + Arrays.toString(queryParts));
            }
         }
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   /**
    * Uses a queue to pre-order search
    * check if the first node in queue = name,
    * otherwise add all children of current node and remove first
    * @param name name of node were looking for
    * @param root Root of the hurricane tree
    * @return the found node or  null
    */
   public static Tree findNode(String name, Tree root) {
      Queue<Tree> queue = new LinkedList<>();
      queue.add(root);
      // Until queue is empty node is found
      while (!queue.isEmpty()) {
         Tree currentNode = queue.poll();
         // If found node, return
         if (Objects.equals(name, currentNode.getName())) {
            return currentNode;
         }
         // else add children nodes and keep searching
         queue.addAll(currentNode.getChildren());
         queue.remove(currentNode); // Current node name != name
      }
      return null; // Node not found
   }

   /**
    * Sorts a given list of names
    * prints out names in alphabetical order
    * @param treeSet set of names to be printed
    */
   public static void printFormat(Set<String> treeSet) {
      // Sort list alphabetically
      List<String> sortedList = new LinkedList<>(treeSet);
      Collections.sort(sortedList);
      // print out list
      for (String name : sortedList) {
         System.out.printf("%s ", name);
      }
      System.out.println();
   }
}
