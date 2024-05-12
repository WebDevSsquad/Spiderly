package Socket;

import com.google.gson.Gson;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketClient {
    private static final int PORT = 8050;

    public static void main(String[] args) {
        try {
            URI uri = new URI(STR."http://localhost:\{PORT}");
            IO.Options options = new IO.Options();
            Socket socket = IO.socket(uri, options);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Connected to server");
                }
            }).on("createdSocketClientServer", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    System.out.println("Node JS connected to JAVA with socket: " + args[0]);
                }
            })
                    .on("searchQuery", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            System.out.println(args[0]);
                            //here do the ranking process and send the data
                            Gson gson = new Gson();
                            String messageJson = gson.toJson(args);

                            socket.emit("response",args[0].toString().toUpperCase());
                        }
                    });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
