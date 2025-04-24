import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.Random;

public class EventCalander {
    public static void main(String[] args) {
        SkipList eventTimeline = new SkipList();
       
        // Try statement to catch exceptions 
        try {
            // Reads and stores input
            Scanner fileScanner = new Scanner(new File(args[0]));
            while (fileScanner.hasNextLine()) {
                String inputLine = fileScanner.nextLine();

                System.out.print(inputLine + " ");
                 
                String[] tokens = inputLine.split(" ");

                // Prints appropriate response for every case 
                switch (tokens[0]) {
                    case "DisplayEvent":
                        System.out.println(eventTimeline.getEvent(Integer.parseInt(tokens[1])));
                        break;
                    case "AddEvent":
                        System.out.println(eventTimeline.addEvent(Integer.parseInt(tokens[1]), tokens[2]));
                        break;
                    case "DeleteEvent":
                        System.out.println(eventTimeline.deleteEvent(Integer.parseInt(tokens[1])));
                        break;
                    case "DisplayEventsBetweenDates":
                        System.out.println(eventTimeline.getEventsInRange(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2])));
                        break;
                    case "DisplayEventsFromStartDate":
                        System.out.println(eventTimeline.getEventsInRange(Integer.parseInt(tokens[1]), 1231));
                        break;
                    case "DisplayEventsToEndDate":
                        System.out.println(eventTimeline.getEventsInRange(101, Integer.parseInt(tokens[1])));
                        break;
                    case "DisplayAllEvents":
                        System.out.println(eventTimeline.getEventsInRange(101, 1231));
                        break;
                    case "PrintSkipList":
                        System.out.println(" ");
                        eventTimeline.printSkipList();
                        break;
                    default:
                        System.out.print(" ");
                        break;
                }
            }
            fileScanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}

// SkipList class to manage timeline events
class SkipList {
    private static final int MAX_LEVEL = 10;
    private SkipListNode headNode;
    private int currentLevel;
    private Random randomGen;

    public SkipList() {
        headNode = new SkipListNode(-1, null, MAX_LEVEL);
        currentLevel = 0;
        randomGen = new Random();
    }
    // Generates random level for nodes, with a 50% probability of increasing height
    private int getRandomHeight() {
        int height = 0;
        while (randomGen.nextInt(2) == 0 && height < MAX_LEVEL) {
            height++;
        }
        return height;
    }

    public String getEvent(int eventDate) {
        SkipListNode currentNode = headNode;
        for (int i = currentLevel; i >= 0; i--) {
            while (currentNode.forwardNodes[i] != null && currentNode.forwardNodes[i].key < eventDate) {
                currentNode = currentNode.forwardNodes[i];
            }
        }
        currentNode = currentNode.forwardNodes[0];
        if (currentNode != null && currentNode.key == eventDate) {
            return currentNode.value;
        }
        return "none";
    }

    public String addEvent(int eventDate, String eventDescription) {
        SkipListNode[] updateNodes = new SkipListNode[MAX_LEVEL + 1];
        SkipListNode currentNode = headNode;
        
        // Traverse the skip list from the highest level to the lowest
        for (int i = currentLevel; i >= 0; i--) {
            while (currentNode.forwardNodes[i] != null && currentNode.forwardNodes[i].key < eventDate) {
                currentNode = currentNode.forwardNodes[i];
            }
            updateNodes[i] = currentNode;
        }
    
        currentNode = currentNode.forwardNodes[0];
    
        // If the key already exists, replace the value
        if (currentNode != null && currentNode.key == eventDate) {
            String oldValue = currentNode.value;
            currentNode.value = eventDescription;
            return oldValue;
        } else {
            // Generate a random level for the new node
            int newNodeLevel = getRandomHeight();
    
            // If the new node has a higher level than the current highest level, update the level of the skip list
            if (newNodeLevel > currentLevel) {
                for (int i = currentLevel + 1; i <= newNodeLevel; i++) {
                    updateNodes[i] = headNode;
                }
                currentLevel = newNodeLevel;
            }
    
            // Create a new node with the random level
            currentNode = new SkipListNode(eventDate, eventDescription, newNodeLevel);
    
            // Link the new node with the appropriate forward references
            for (int i = 0; i <= newNodeLevel; i++) {
                currentNode.forwardNodes[i] = updateNodes[i].forwardNodes[i];
                updateNodes[i].forwardNodes[i] = currentNode;
            }
    
            return "success";
        }
    }
  
    // Removes a node with the specified key from the list
    public String deleteEvent(int eventDate) {
        SkipListNode[] updateNodes = new SkipListNode[MAX_LEVEL + 1];
        SkipListNode currentNode = headNode;
        
        // Traverse levels from top to bottom to locate the position of the target key
        for (int i = currentLevel; i >= 0; i--) {
            while (currentNode.forwardNodes[i] != null && currentNode.forwardNodes[i].key < eventDate) {
                currentNode = currentNode.forwardNodes[i];
            }
            updateNodes[i] = currentNode;
        }
        
        // Move to the next node at level 0 to check if it’s the target key.
        currentNode = currentNode.forwardNodes[0];
        if (currentNode != null && currentNode.key == eventDate) {
            for (int i = 0; i <= currentLevel; i++) {
                if (updateNodes[i].forwardNodes[i] != currentNode) break;
                updateNodes[i].forwardNodes[i] = currentNode.forwardNodes[i];
            }
            while (currentLevel > 0 && headNode.forwardNodes[currentLevel] == null) {
                currentLevel--;
            }
            return "success";
        }
        return "noDateError";
    }

    // Finds the entry with the largest key less than or equal to the specified key.
    public String floorEvent(int eventDate) {
        SkipListNode currentNode = headNode;
        for (int i = currentLevel; i >= 0; i--) {
            while (currentNode.forwardNodes[i] != null && currentNode.forwardNodes[i].key <= eventDate) {
                currentNode = currentNode.forwardNodes[i];
            }
        }
        if (currentNode != headNode) {
            return formatDate(currentNode.key) + ":" + currentNode.value;
        }
        return "none";
    }

    // Finds the entry with the smallest key greater than or equal to the specified key.
    public String ceilingEvent(int eventDate) {
        SkipListNode currentNode = headNode;
        for (int i = currentLevel; i >= 0; i--) {
            while (currentNode.forwardNodes[i] != null && currentNode.forwardNodes[i].key < eventDate) {
                currentNode = currentNode.forwardNodes[i];
            }
        }
        currentNode = currentNode.forwardNodes[0];
        if (currentNode != null) {
            return formatDate(currentNode.key) + ":" + currentNode.value;
        }
        return "none";
    }

    // Returns a range of entries between the specified startKey and endKey
    public String getEventsInRange(int startDate, int endDate) {
        SkipListNode currentNode = headNode;
        for (int i = currentLevel; i >= 0; i--) {
            while (currentNode.forwardNodes[i] != null && currentNode.forwardNodes[i].key < startDate) {
                currentNode = currentNode.forwardNodes[i];
            }
        }
        currentNode = currentNode.forwardNodes[0];
        StringBuilder result = new StringBuilder();
        while (currentNode != null && currentNode.key <= endDate) {
            result.append(formatDate(currentNode.key)).append(":").append(currentNode.value).append(" ");
            currentNode = currentNode.forwardNodes[0];
        }
        return result.length() > 0 ? result.toString().trim() : "none";
      }
// prints entire skiplist + levels
      public void printSkipList() {
         for (int i = currentLevel; i >= 0; i--) {
             SkipListNode currentNode = headNode.forwardNodes[i];
             
             
             System.out.print("(S" + i + ") ");
             
             if (currentNode == null) {
                 System.out.println("empty");
             } else {
               
                 while (currentNode != null) {
                     System.out.print(formatDate(currentNode.key) + ":" + currentNode.value + " ");
                     currentNode = currentNode.forwardNodes[i];  
                 }
                 System.out.println(); 
             }
         }
     }
    
    private String formatDate(int key) {
        return String.format("%04d", key);
    }
}

// Node structure for each element in the skip list
class SkipListNode {
    int key;
    String value;
    SkipListNode[] forwardNodes;

    public SkipListNode(int key, String value, int level) {
        this.key = key;
        this.value = value;
        forwardNodes = new SkipListNode[level + 1];
    }
}

 


