package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client  {

    String hostName;   //hostname of the server
    int portNumber; // port number of the server

    Client(String hostName, int portNumber){
        this.hostName = hostName;
        this.portNumber = portNumber;
    }

    public void connect(){
        try(Socket connection = new Socket(hostName,portNumber)){
            PrintWriter output = new PrintWriter(connection.getOutputStream(),true);
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            BufferedReader standardInput = new BufferedReader(new InputStreamReader(System.in));
            String userCommand;

            while(true) {
                System.out.println("Enter your request for the server");
                userCommand = standardInput.readLine();
                output.println(userCommand);
                if(userCommand == null){
                    break;
                }
                else{
                    try {
                        while (true) {

                            String s = input.readLine();
                            if (s.equals("eof") && userCommand.equals("index")) {
                                break;
                            }
                            else if(s.equals("eof") && !userCommand.equals("index")){
                                //we dont want to print eof for this case, and by this time the server would have closed the connection
                                //so we also close the connection here
                                //when we continue, for the next iteration, it will throw a null pointer exception and close
                                continue;
                            }
                            System.out.println(s);

                        }
                    }catch (NullPointerException e){
                        System.out.println("Server has closed the connection. Exiting.....");
                        connection.close();
                        break;
                    }
                }
            }

        } catch (UnknownHostException e) {
           System.out.println("Unable to identify host"+"\n"+e);
        }catch (ConnectException e){
            System.out.println("Server maybe disconnected \n"+ e);
        }
        catch (IOException e) {
            System.out.println("Connection closed!!"+"\n"+e);
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost",4444);
        client.connect();
    }
}
