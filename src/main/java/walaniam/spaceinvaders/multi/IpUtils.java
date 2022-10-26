package walaniam.spaceinvaders.multi;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.DatagramSocket;
import java.net.InetAddress;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IpUtils {

    public static String detectLocalHostAddress() {
        try(var socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
