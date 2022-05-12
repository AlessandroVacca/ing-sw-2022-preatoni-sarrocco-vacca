package it.polimi.ingsw.Client;

import it.polimi.ingsw.Client.messages.Action;
import it.polimi.ingsw.Client.messages.LoginMessage;
import it.polimi.ingsw.Client.messages.Message;
import it.polimi.ingsw.Client.messages.SerializedMessage;
import it.polimi.ingsw.Constants.Constants;
import it.polimi.ingsw.Constants.Exceptions.DuplicateNicknameException;
import it.polimi.ingsw.Constants.Exceptions.InvalidNicknameException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ConnectionSocket class handles the connection between the client and the server.
 *
 * @author Federico Sarrocco
 */
public class ConnectionSocket {
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String serverAddress;
    private final int serverPort;
    SocketListener listener;
    private ObjectOutputStream outputStream;

    /** Constructor ConnectionSocket creates a new ConnectionSocket instance. */
    public ConnectionSocket() {
        this.serverAddress = "localhost";//Constants.getAddress();
        this.serverPort =   8080;//Constants.getPort();
    }

    /**
     * Method setup initializes a new socket connection and handles the nickname-choice response. It
     * loops until the server confirms the successful connection (with no nickname duplication and
     * with a correctly configured match lobby).
     *
     * @param nickname of type String - the username chosen by the user.
     * @param modelView of type ModelView - the structure, stored into the client, containing simple
     *     logic of the model.
     * @param serverMessageHandler of type ServerMessageHandler - the class handling the answers.
     * @return boolean true if connection is successful, false otherwise.
     * @throws DuplicateNicknameException when the nickname is already in use.
     * @throws InvalidNicknameException when the nickname contains illegal characters (like "-").
     */
    public boolean setup(String nickname, ModelView modelView, ServerMessageHandler serverMessageHandler)
            throws DuplicateNicknameException, InvalidNicknameException {
        try {
            System.out.println(
                    Constants.ANSI_YELLOW + "Configuring socket connection..." + Constants.ANSI_RESET);
            System.out.println(
                    Constants.ANSI_YELLOW
                            + "Opening a socket server communication on port "
                            + serverPort
                            + "..."
                            + Constants.ANSI_RESET);
            Socket socket;
            try {
                socket = new Socket(serverAddress, serverPort);
            } catch (SocketException | UnknownHostException e) {
                return false;
            }
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            while (true) {
                if (readInput(nickname, input)) {
                    break;
                }
            }
            listener = new SocketListener(socket, modelView, input, serverMessageHandler);
            Thread thread = new Thread(listener);
            thread.start();
            return true;
        } catch (IOException e) {
            System.err.println("Error during socket configuration! Application will now close.");
            logger.log(Level.SEVERE, e.getMessage(), e);
            System.exit(0);
            return false;
        }
    }

    /**
     * Method readInput handles the input reading in order to reduce the setup complexity.
     *
     * @param nickname of type String - the chosen nickname.
     * @param input of type ObjectInputStream - the input socket stream.
     * @return boolean true if nickname is available and set, false otherwise.
     * @throws DuplicateNicknameException when the nickname has already been chosen.
     * @throws InvalidNicknameException when the nickname contains illegal characters (like "-").
     */
    private boolean readInput(String nickname, ObjectInputStream input)
            throws DuplicateNicknameException, InvalidNicknameException {
        //try {
            send(new LoginMessage(nickname));
            //if (nicknameChecker(input.readObject())) {
                return true;
            //}
//        } catch (IOException | ClassNotFoundException e) {
//            System.err.println(e.getMessage());
//            return false;
//        }
//        return true;
    }

    /**
     * Method send sends a new message to the server, encapsulating the object in a SerializedMessage
     * type unpacked and read later by the server.
     *
     * @param message of type Message - the message to be sent to the server.
     */
    public void send(Message message) {
        SerializedMessage output = new SerializedMessage(message);
        try {
            outputStream.reset();
            outputStream.writeObject(output);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error during send process.");
            System.err.println(e.getMessage());
        }
    }

    /**
     * Method send sends a new action to the server, encapsulating the object in a SerializedMessage
     * type unpacked and read later by the server.
     *
     * @param action of type UserAction - the action to be sent to the server.
     */
    public void send(Action action) {
        SerializedMessage output = new SerializedMessage(action);
        try {
            outputStream.reset();
            outputStream.writeObject(output);
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("Error during send process.");
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }
}