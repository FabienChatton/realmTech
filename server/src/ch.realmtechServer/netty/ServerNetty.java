package ch.realmtechServer.netty;

import ch.realmtechServer.ServerContext;
import com.artemis.World;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.ServerSocket;

public class ServerNetty {
    public final static int PREFERRED_PORT = 25533;
    private final static Logger logger = LoggerFactory.getLogger(ServerContext.class);
    private final World world;
    private Channel channel;
    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    public ServerNetty(ConnectionBuilder connectionBuilder, World world) throws Exception {
        this.world = world;
        prepareSocket(connectionBuilder);
    }

    public static boolean isPortAvailable(int port) {
        boolean ret = false;
        try {
            (new ServerSocket(port)).close();
            ret = true;
        } catch (Exception ignored) {
        }
        return ret;
    }

    private void prepareSocket(ConnectionBuilder connectionBuilder) throws Exception {
        this.boss = new NioEventLoopGroup();
        this.worker = new NioEventLoopGroup();

        ServerBootstrap sb = new ServerBootstrap();
        sb.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new PacketDecoder());
                        ch.pipeline().addLast(new PacketEncoder());
                        ch.pipeline().addLast(new ServerHandler(world));
                    }
                });
        channel = sb.bind(connectionBuilder.getPort()).sync().channel();
        logger.info("Le serveur à ouvert sur le port {}", connectionBuilder.getPort());
        channel.closeFuture().addListener(ChannelFutureListener.CLOSE);
    }

    public ChannelFuture close() {
        boss.shutdownGracefully();
        worker.shutdownGracefully();
        return channel.close();
    }
}
