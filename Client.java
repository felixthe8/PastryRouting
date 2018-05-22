import java.net.*;
import java.io.*;
import java.util.*;

/* 
    Compile Command: javac Client.java

    Execution Commands (run in specific order) :
    java Client 
*/

public class Client 
{
    public static void main(String args[])
    {
        ArrayList<Integer> hopCounter = new ArrayList<Integer>();
        for(int i = 0; i < 1000; i++) {
            int returnedHops = findAddress();
            if(returnedHops == -1) {
                // Do not increment the counter. Keep i the same.
                i--;
            }
            else {
                hopCounter.add(returnedHops);
            }
        }

        HashMap <Integer, Integer > countMap = new HashMap<Integer, Integer>();
        for (int elem : hopCounter) {
            if(countMap.containsKey(elem)) {
                countMap.put(elem, countMap.get(elem) + 1) ;
            } else {
                countMap.put(elem , 1);
            }
        }
        
        HashMap <Integer, Double> percentageMap = new HashMap<Integer, Double> ();
       for (int key : countMap.keySet()) {
           // Compute the percentage of hops for the iterations
           double percentage = countMap.get(key);
           percentage = percentage / hopCounter.size();

           percentageMap.put(key, percentage);
       }
       
        System.out.println("");
       
        for(int key : percentageMap.keySet()) {
            System.out.println("Hop(s) "+key+":\t"+percentageMap.get(key)*100+"%");
        }
    }

    // Function to create a base-4 pasty ID
    public static String createPastry () {
        Random rand = new Random();
        String id = "";
        // Create the Pastry ID by random generating digits from 0 - 3
        for (int i = 0; i < 4; i++) {
            int digit = rand.nextInt(3);
            id += digit;
        }

        return id;
    }

    // Function to find the address associated with a Pastry
    public static int findAddress() {
        DatagramSocket aSocket = null;

        // Counter variable to hold the number of hops
        int count = 1;
        try
        {
            // Create a random pastry ID
            String pastryID = createPastry();
            System.out.println("\n**********\n\nInput: "+pastryID );

            // My AWS ID. Loop will start at this IP
            String ip = "54.241.121.103";

            // Flag to determine if the value could be found or not
            boolean found = false;

            // Keep track of the previous pastryID. 
            ArrayList<String> pastryContainer = new ArrayList<String>();

            while (!found) {

                aSocket = new DatagramSocket();
                byte[] m = pastryID.getBytes();
                
                // Second argument is the address of the AWS
                InetAddress aHost = InetAddress.getByName(ip);

                // Communication over port 32710
                int serverPort = 32710;
                DatagramPacket request = new DatagramPacket(m , m.length, aHost, serverPort);
                
                // Set a .5 second timeout for the request
                aSocket.setSoTimeout(500);
                aSocket.send(request);

                byte[] buffer = new byte [1000];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(response);
                
                System.out.println(count+"  Returned\t"+new String(response.getData())+"\n");

                // Split the input 
                String [] vals = new String(response.getData()).split(":");
                // If the pastry ID returned is the same as the one that was generated, then we have our result
                if(vals[0].equals(pastryID) ) {
                    System.out.println("Found !");
                    found = true;
                }  else if(vals[0].trim().equalsIgnoreCase("null") || vals[1].trim().equalsIgnoreCase("null") ) 
                    { 
                        System.out.println("Could not be found");
                        found = true; 
                    } else if (pastryContainer.contains(vals[0].trim())) {
                        // If the pastry ID returned is the same as the one prior to it, break out of this loop
                        // Exclude this from the data set.
                        System.out.println("Matching consecutive values. Excluded");
                        return -1;
                    } else {
                    // Set the new IP to the one that is in the response
                    ip = vals[1];

                    // Sets the value of the previous IP to the one that just came up
                    pastryContainer.add(vals[0].trim());

                    // Increment the counter variable
                    count++;
                }
            }
        }

        // Return -1 if the there is an issue
        catch (SocketException e)
        {
           System.out.println("Socket Timeout: "+e.getMessage());
           System.out.println("Excluded");
           return -1;
        }

        catch (IOException e)
        {
            System.out.println("IO: "+e.getMessage());
            System.out.println("Excluded");
            return -1;

        }
        catch (ArrayIndexOutOfBoundsException e) 
        {
            System.out.println(e.getMessage());
            System.out.println("Excluded");
            return -1;

        }

        finally
        {
            if(aSocket != null)
            {
                aSocket.close();
            }

        }
        // Return the number of hops to find the Pastry ID - IP combination
        return count;
    }
}
