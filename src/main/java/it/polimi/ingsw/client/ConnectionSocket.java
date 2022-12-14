package it.polimi.ingsw.client;

import it.polimi.ingsw.client.messages.Action;
import it.polimi.ingsw.client.messages.LoginMessage;
import it.polimi.ingsw.client.messages.Message;
import it.polimi.ingsw.client.messages.SerializedMessage;
import it.polimi.ingsw.constants.CLIColors;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.exceptions.DuplicateNicknameException;
import it.polimi.ingsw.exceptions.InvalidNicknameException;
import it.polimi.ingsw.server.answers.ConnectionMessage;
import it.polimi.ingsw.constants.ErrorType;
import it.polimi.ingsw.server.answers.GameError;
import it.polimi.ingsw.server.answers.SerializedAnswer;

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
 */
public class ConnectionSocket {

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String serverAddress;
    private final int serverPort;
    SocketListener listener;
    private ObjectOutputStream outputStream;

    /**
     * Constructor ConnectionSocket creates a new ConnectionSocket instance.
     */
    public ConnectionSocket() {
        this.serverAddress = Constants.getAddress();
        this.serverPort = Constants.getPort();
    }

    /**
     * Method setup initializes a new socket connection and handles the nickname-choice response. It
     * loops until the server confirms the successful connection (with no nickname duplication and
     * with a correctly configured match lobby).
     *
     * @param nickname             of type String - the username chosen by the user.
     * @param modelView            of type ModelView - the structure, stored into the client, containing simple
     *                             logic of the model.
     * @param serverMessageHandler of type ServerMessageHandler - the class handling the answers.
     * @return boolean true if connection is successful, false otherwise.
     * @throws DuplicateNicknameException when the nickname is already in use.
     * @throws InvalidNicknameException   when the nickname contains illegal characters (like "-").
     */
    public boolean setup(String nickname, ModelView modelView, ServerMessageHandler serverMessageHandler) throws DuplicateNicknameException, InvalidNicknameException {
        try {
            System.out.println(
                    CLIColors.ANSI_YELLOW + "Configuring socket connection..." + CLIColors.RESET);
            System.out.println(
                    CLIColors.ANSI_YELLOW
                            + "Opening a socket server communication on port "
                            + serverPort
                            + "..."
                            + CLIColors.RESET);
            Socket socket;
            try {
                socket = new Socket(serverAddress, serverPort);
            } catch (SocketException | UnknownHostException e) {
                return false;
            }
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
            while (true) {
                if (readSetupInput(nickname, input)) {
                    break;
                }
            }
            listener = new SocketListener(socket, modelView, input, serverMessageHandler);
            Thread thread = new Thread(listener);
            thread.start();

            System.out.println(CLIColors.ANSI_GREEN + "Socket Connection setup completed!" + CLIColors.RESET);
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
     * @param input    of type ObjectInputStream - the input socket stream.
     * @return boolean true if nickname is available and set, false otherwise.
     * @throws DuplicateNicknameException when the nickname has already been chosen.
     * @throws InvalidNicknameException   when the nickname contains illegal characters (like "-").
     */
    private boolean readSetupInput(String nickname, ObjectInputStream input)
            throws DuplicateNicknameException, InvalidNicknameException {
        try {
            send(new LoginMessage(nickname));
            if (nicknameChecker(input.readObject())) {
                return true;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Method send forwards a new message to the server, encapsulating the object in a SerializedMessage
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
     * Method send forwards a new action to the server, encapsulating the object in a SerializedMessage
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

    /**
     * Method nicknameChecker remotely checks if a name is invalid or already taken.
     *
     * @param input the client's inserted nickname.
     * @return True if successful, false otherwise
     * @throws DuplicateNicknameException thrown if the nickname is already taken.
     * @throws InvalidNicknameException thrown if the nickname is not valid.
     */
    public boolean nicknameChecker(Object input) throws DuplicateNicknameException, InvalidNicknameException {
        SerializedAnswer answer = (SerializedAnswer) input;
        if (answer.getServerAnswer() instanceof ConnectionMessage && ((ConnectionMessage) answer.getServerAnswer()).isValid()) {
            return true;
        } else if (answer.getServerAnswer() instanceof GameError) {
            if (((GameError) answer.getServerAnswer()).getError().equals(ErrorType.DUPLICATE_NICKNAME)) {
                System.err.println("This nickname is already in use! Please choose one other.");
                throw new DuplicateNicknameException();
            }
            if (((GameError) answer.getServerAnswer()).getError().equals(ErrorType.INVALID_NICKNAME)) {
                System.err.println("Nickname can't contain - character");
                throw new InvalidNicknameException();
            } else if (((GameError) answer.getServerAnswer()).getError().equals(ErrorType.FULL_LOBBY)) {
                System.err.println(
                        "This match is already full, please try again later!\nApplication will now close...");
                System.exit(0);
            }
        }
        return false;
    }
}