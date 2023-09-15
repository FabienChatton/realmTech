package ch.realmtech.game.netty;

import ch.realmtech.RealmTech;
import ch.realmtechCommuns.packet.Packet;
import ch.realmtechServer.netty.ConnectionBuilder;
import ch.realmtechServer.ServerContext;
import ch.realmtechServer.netty.ServerNetty;

import java.io.Closeable;
import java.io.IOException;
import java.util.Random;

public class RealmtechClientConnectionHandler implements Closeable {
    private ServerContext server;
    private final RealmtechClient client;

    /**
     * Pour une connection en multi
     */
    public RealmtechClientConnectionHandler(ConnectionBuilder connectionBuilder, RealmTech context) throws IOException {
        try {
            client = new RealmtechClient(connectionBuilder, context);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    /**
     * Destiner pour une connection en solo
     */
    public RealmtechClientConnectionHandler(RealmTech context) throws IOException {
        try {
            ConnectionBuilder connectionBuilder = ServerContext.builder();
            if (!ServerNetty.isPortAvailable(connectionBuilder.getPort())) {
                byte limite = 0;
                do {
                    connectionBuilder.setPort(new Random().nextInt(1024, 65565));
                } while (!ServerNetty.isPortAvailable(connectionBuilder.getPort()) || ++limite < 10);
            }
            server = new ServerContext(connectionBuilder);
            client = new RealmtechClient(connectionBuilder, context);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            client.close();
            if (server != null) server.close();
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    public void sendAndFlushPacketToServer(Packet packet) {
        client.getChannel().writeAndFlush(packet);
    }
}
