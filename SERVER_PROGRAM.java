import java.net.*;
import java.io.*;
import java.util.*;

public class SERVER_PROGRAM {
    public static void main (String [] args) {
        DatagramSocket aSocket = null;

        try 
        {
            // Program must user port 32710
            aSocket = new DatagramSocket(32710);

            while (true) 
            {
                byte [] buffer = new byte [1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);

                aSocket.receive(request);

                System.out.println("Request received from: "+request.getAddress().toString());

                // Call to build the leaf set and the routing table
                request.setData(pastryTable(new String(request.getData(), "UTF-8")).getBytes());
                DatagramPacket reply = new DatagramPacket(request.getData(),request.getLength(), request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        }

        catch (SocketException e) { System.out.println("Socket Exception: "+e.getMessage());}
        catch (IOException io) {System.out.println("IO Exception: "+io.getMessage());}

        finally 
        {
            if(aSocket != null)
            {
                System.out.println("Socket failed");
                aSocket.close();
            }
        }
    }


    public static String pastryTable (String args) 
    {
        // Need to trim out the white spaces due to program requirement
        args = args.trim();
        args = args.replace(" ","");
        boolean valid = true;

        try 
        {
            // Loop to ensure that the input is numerical digits.
            // Check by parsing each character to an Integer. If this fails, then it's not a valid input
            for (char digits : args.toCharArray())
            {
                int value = Integer.parseInt(Character.toString(digits));

                if (value > 3)
                {
                    // If the value that is read in is >3, this is an invalid case
                    // because Pastry ID digits only range from 0 - 3
                    valid = false;
                    break;
                }
            }
        }

        catch (Exception e)
        {
            System.out.println("Invalid request: "+ args);
            return ("Invalid Request");
        }

        // The inputs must be quartenary values so they must come in the format of 
        // XXXX where each X is an integer from 0 - 3
        if (args.length() > 4 || args.length() < 1 || !valid )
        {
            System.out.println("Invalid request: "+ args);
            return ("Invalid Request");
        }

        // HashMap to hold the leaf set values
        HashMap<String, String > leafSet = new HashMap<String , String>();
        // "Smaller" values in the leaf set
        leafSet.put("3111", "18.217.11.51");
        leafSet.put("3023", "18.144.55.5");

        // "Larger" values in the leaf set
        leafSet.put("3120", "18.220.214.117");
        leafSet.put("3122", "54.67.80.237");

        HashMap<String,String> routingTable = new HashMap<String,String>();

        // Initial row (Row 0 ) of routing table. No prefixes
        routingTable.put("0" , "-032:18.188.30.228");
        routingTable.put("1", "-100:18.217.157.190");
        routingTable.put("2", "-022:18.218.254.202");
        routingTable.put("3","-112:54.241.121.103");

        // Row 1. Common prefix is '3'
        routingTable.put("30", "-01:13.57.5.20");
        routingTable.put("31", "-12:54.241.121.103");
        routingTable.put("32", "-10:18.22.0.214");
        routingTable.put("33", "-10:54.177.53.21");

        // Row 2. Common prefix is '31'
        routingTable.put("310","-x:null");
        routingTable.put("311", "-2:54.241.121.103");
        routingTable.put("312", "-3:18.216.172.183");
        routingTable.put("313","-0:18.219.111.103");

        // Row 3. Common prefix is '311'
        routingTable.put("3110",":null");
        routingTable.put("3111", ":18.217.11.51");
        routingTable.put("3112",":54.241.121.103");
        routingTable.put("3113",":null");

        String input = args.trim();

        // This is my Pastry ID. Return the IP address associated with my Pastry ID
        if (input.equals("3112")) 
        {
            String reply = input + ":54.241.121.103";
            return reply;
        }

        // Check if the user input is in the leaf set
        else if (leafSet.containsKey(input))
        {
            String leafSetVal = leafSet.get(input);
            String reply = input+":"+leafSetVal;
            return reply;
            
        }

        // If the value doesn't exist in the leaf set, then we must run the Pastry algorithm.
        else 
        {
            // Pass the routing table and the quartenary value that the user input
            return pastryRoute (routingTable, input);
        }
    }

    public static String pastryRoute(HashMap<String, String> routingTable , String input)
    {
        // Determine the length of the user input. Use that value as loop control.
        int iterations = input.length();
        // 'i' will be used as a counter variable
        int i = 0;
        // 
        String temp = "";
        while (i < iterations)
        {
            // Read in each individual character from the user input
            temp += Character.toString(input.charAt(i));

            // If the routing table contains a key that is equal to the temp string,
            // we will either keep reading in the string input, or find the
            // associating PastryID and IP 
            if(routingTable.containsKey(temp))
            {
                // Increment the value of i and keep reading
                if (iterations > (i + 1))
                {
                    i += 1;
                }
                else 
                {
                    // Find the corresponding row in the routing table
                    String found = routingTable.get(temp);
                    String reply = input + found;
                    reply = reply.replace("-", "");
                    return reply;
                }
            }
            else 
            {
                input = temp.substring(0 , i);
                return pastryRoute(routingTable, input);
            }
        }

        return input +": null";

    }

}