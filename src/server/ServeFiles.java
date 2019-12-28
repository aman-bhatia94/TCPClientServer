package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

//The server runs on localhost
public class ServeFiles {

    private String directory;
    private String hostName;
    private int portNumber; //portNumber on which to run the server

    ServeFiles(String directory, int portNumber){
        this.directory = directory;
        this.portNumber = portNumber;
    }

    public void createServer(){
        try(ServerSocket serverSocket = new ServerSocket(portNumber)){
            while(true){
                try(Socket clientSocket = serverSocket.accept()){
                    System.out.println("client connected");
                    PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String inputFromClient;
                    while((inputFromClient = input.readLine())!= null){
                        System.out.println("input from the client: "+inputFromClient);
                        int noOfChars = inputFromClient.length();
                        if(noOfChars < 3){
                            output.println("stale request");
                            output.close();
                            clientSocket.close();
                        }
                        else if(inputFromClient.equals("index")){
                            System.out.println("client requested for index");
                            //return a list of all files present in the directory to the client

                            File folder = new File(directory);
                            File[] files = folder.listFiles();
                            StringBuilder listOfFiles = new StringBuilder();
                            for(File file: files){
                                listOfFiles.append(file.getName()+" ");
                            }
                            listOfFiles.append("\n");
                            listOfFiles.append("eof");
                            String finalFiles = listOfFiles.toString();

                            output.println(finalFiles);

                        }
                        else if(inputFromClient.substring(0,3).equals("get")){
                            if(inputFromClient.length() < 5){
                                output.println("no file specified");
                                output.close();
                                clientSocket.close();
                            }
                            String fileName = inputFromClient.substring(4);
                            System.out.println("client requested get "+fileName);
                            fileName = fileName.trim();
                            //check for this file, return its contents, if available, then return contents of the file
                            File file = new File(directory+"/"+fileName);

                            if(file.exists()){
                                BufferedReader reader = new BufferedReader(new FileReader(file));
                                StringBuilder sb = new StringBuilder();
                                sb.append("ok");
                                sb.append("\n");
                                String line;
                                while((line = reader.readLine()) != null){ //otherwise readline skips

                                    sb.append(line);
                                    sb.append("\n");
                                }
                                sb.append("eof"); //change
                                String toClient = sb.toString();

                                output.println(toClient);

                                output.close();
                                clientSocket.close();
                                serverSocket.close(); //closing server socket also here.

                            }
                            else{
                                System.out.println("error");
                                System.out.println("File does not exist on the server");
                                output.println("error");
                                output.close();
                                clientSocket.close();
                                serverSocket.close(); //closing server socket also here.
                            }
                        }
                        else{
                            output.println("stale request, enter another request");

                        }
                    }
                }
                catch (StringIndexOutOfBoundsException e){
                    System.out.println("Some problem with the get handler in server\n"+e);
                }
                catch (NullPointerException e){
                    System.out.println("Input from the client is null, or malformed \n"+e);

                }
                catch (IOException e){
                    System.out.println("The connection is closed \n");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to create a ServerSocket on port: "+portNumber+"\n"+e);
        }
    }

    public static void main(String[] args) {
        String directory = args[0];
        ServeFiles serveFiles = new ServeFiles(directory,4444);
        serveFiles.createServer();
    }
}
