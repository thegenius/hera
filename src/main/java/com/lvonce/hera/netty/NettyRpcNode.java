package com.lvonce.hera.netty;

import com.lvonce.hera.logger.RpcLogger;
import com.lvonce.hera.future.RpcFuture;
import com.lvonce.hera.context.RpcRequest;
import com.lvonce.hera.context.RpcMessage;
import com.lvonce.hera.context.RpcMessageContext;
import com.lvonce.hera.worker.RpcWorkerService;
import com.lvonce.hera.worker.RpcWorkerThreadPool;
import com.lvonce.hera.serializer.SerializerType;
import com.lvonce.hera.exception.RpcDisconnectedException;

import com.lvonce.hera.netty.AliveChecker;
import com.lvonce.hera.netty.NettyKryoDecoder;
import com.lvonce.hera.netty.NettyKryoEncoder;
import com.lvonce.hera.netty.NettyRpcNode;
import com.lvonce.hera.netty.RpcDispatchHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;

import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ChannelHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


import java.util.Map;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class NettyRpcNode {
	public enum Type{ NONE, ONLY_SERVER, ONLY_CLIENT, BOTH };
	public static final int ALL_IDEL_TIME_OUT = 45; 
	public static final	int READ_IDEL_TIME_OUT = 0; 
	public static final int WRITE_IDEL_TIME_OUT = 0;
	
	private static final RpcWorkerService workerService;
	private static final ConcurrentMap<String, Channel> channelMap; 

	private static Bootstrap clientBoot;
	private static ServerBootstrap serverBoot;
	private static EventLoopGroup bossGroup; 
    private static EventLoopGroup workerGroup;
	private static AtomicInteger invokeIdGenerator;

	private static ChannelHandler aliveChecker;
	private static ChannelHandler dispatcher;
	private static ChannelHandler encoder;
	
	private static Map<Integer, Channel> serverChannels; // {port: channel}
	

	//public NettyRpcNode(NettyRpcNode.Type factoryType, RpcWorkerService workerService, ChannelHandler aliveChecker, ChannelHandler ... handlers) {
	
	private static void initClientBootstrap(Bootstrap clientBoot) {
		clientBoot
			.group(new NioEventLoopGroup())
			.channel(NioSocketChannel.class)
    	    .handler(
				new ChannelInitializer<Channel>(){
    	    	 	protected void initChannel(Channel ch) throws Exception {
    	    			ch.pipeline().addLast(
							//new NettyKryoDecoder(), 
							new NettyDecoder(), 
							dispatcher,
							encoder
						);
    	    	 	}
			 	}
			)
    		.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
    		.option(ChannelOption.TCP_NODELAY, true)
    		.option(ChannelOption.SO_KEEPALIVE, true);
	}

	private static void initServerBootstrap(ServerBootstrap serverBoot) {
    	serverBoot
			.group(bossGroup, workerGroup)
    		.channel(NioServerSocketChannel.class)
    		.childHandler(
			   new ChannelInitializer<SocketChannel>(){
			    protected void initChannel(SocketChannel ch) throws Exception {
			   	ch.pipeline().addLast(
					//new NettyKryoDecoder(), 
					new NettyDecoder(), 
					dispatcher,
					encoder
				);
			   	if (aliveChecker != null) {
			   		ch.pipeline().addLast(
			   			new IdleStateHandler(
			   				READ_IDEL_TIME_OUT,
			   				WRITE_IDEL_TIME_OUT, 
			   				ALL_IDEL_TIME_OUT, 
			   				TimeUnit.SECONDS),
			   			aliveChecker
			   		);
			   	}
			    }
			})
    		.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
    		.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_KEEPALIVE, true);
	}

	static { 
		int threads = Runtime.getRuntime().availableProcessors() * 2;
		ExecutorService threadPool = Executors.newFixedThreadPool(threads);
		workerService = new RpcWorkerThreadPool(threadPool);
		workerService.start(threads);

		aliveChecker = new AliveChecker();
		dispatcher = new RpcDispatchHandler(workerService);
		//encoder = new NettyKryoEncoder();
		encoder = new NettyEncoder();
	 	
		//clientBoot = new Bootstrap();
		//serverBoot = new ServerBootstrap();
		bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
	 	invokeIdGenerator = new AtomicInteger(0);
	 	channelMap = new ConcurrentHashMap<String, Channel>();

       // try {
	   // 	NettyRpcNode.Type factoryType = Type.BOTH;

	   // 	if ((factoryType.ordinal() & Type.ONLY_CLIENT.ordinal()) != 0) {
	   // 		RpcLogger.info(NettyRpcNode.class, "build client channel!");
	   // 		initClientBootstrap(clientBoot);
	   // 	}
	   // 	
	   // 	if ((factoryType.ordinal() & Type.ONLY_SERVER.ordinal()) != 0) {
	   // 		RpcLogger.info(NettyRpcNode.class, "build server channel!");
	   // 		initServerBootstrap(serverBoot);
	   // 	}
       //} catch (Exception e) {
       //		RpcLogger.warn(NettyRpcNode.class, "RpcNode init failed!");
       //		RpcLogger.warn(NettyRpcNode.class, e.getMessage());
	   // 	System.exit(1);
       //}
	}

	public static void start(int port) {
        try {
            RpcLogger.info(NettyRpcNode.class, "try to bind to " + port);
			ChannelFuture channelFuture = serverBoot.bind(port);
            channelFuture.sync();
            RpcLogger.info(NettyRpcNode.class, "RpcServer started ....");
			channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }        
	}


	public static void serve() {
		if (serverBoot == null) {
			serverBoot = new ServerBootstrap();
			initServerBootstrap(serverBoot);
		}
        try {
			for (Map.Entry<Integer, Channel> entry: serverChannels.entrySet()) {
				int port = entry.getKey();
            	RpcLogger.info(NettyRpcNode.class, "try to bind to " + port);
				ChannelFuture channelFuture = serverBoot.bind(port);
            	channelFuture.sync();
            	serverChannels.put(port, channelFuture.channel());
			}
            RpcLogger.info(NettyRpcNode.class, "RpcServer started ....");
        } catch (Exception e) {
        	e.printStackTrace();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }       
	}
	
	public static void sync() {
        try {
			for (Map.Entry<Integer, Channel> entry: serverChannels.entrySet()) {
            	Channel channel = entry.getValue();
            	channel.closeFuture().sync();
			}
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }   
	}


	public static Channel getChannel(String host, int port, Channel cacheChannel) {
		try {
			if (cacheChannel != null) {
				if (cacheChannel.isActive()) {
					return cacheChannel;
				} else {
					ChannelFuture future = clientBoot.connect(host, port).sync();
					if (future.isSuccess()) {
						Channel newChannel = future.channel();
						channelMap.put(host + ":" + port, newChannel);
						return newChannel;
					} else {
						return null;
					}
				}
			}

			Channel channel = channelMap.get(host + ":" + port);
			if (channel == null) {
				if (clientBoot == null) {
					clientBoot = new Bootstrap();
					initClientBootstrap(clientBoot);
				}
				ChannelFuture future = clientBoot.connect(host, port).sync();
				if (future.isSuccess()) {
					Channel newChannel = future.channel();
					channelMap.put(host + ":" + port, newChannel);
					return newChannel;
				} else {
					return null;
				}
			}		
		} catch (Exception e) {
			RpcLogger.debug(NettyRpcNode.class, e.getMessage());
			return null;
		}
		return null;
	}

	public synchronized static<T> void export(int port, T serviceProvider, Class<T> serviceInterface, Class<?> ... serviceAlias) {
		if (serverChannels == null) {
			serverChannels = new LinkedHashMap<Integer, Channel>();
		}
		serverChannels.put(port, null);
		workerService.export(serviceProvider, serviceInterface, serviceAlias);
	}

	public static RpcFuture call(Channel channel, SerializerType serializerType, String serviceName, String methodName, String sigName, Object ... args) {
		if (channel != null) {
			int id = invokeIdGenerator.addAndGet(1);
			RpcRequest rpcRequest = new RpcRequest(id, serviceName, methodName, sigName, args);
			RpcFuture rpcFuture = new RpcFuture(rpcRequest);
			workerService.register(id, rpcFuture);
			RpcMessage msg = new RpcMessage(0, rpcRequest);
			RpcMessageContext context = new RpcMessageContext(msg, null, serializerType);
			RpcLogger.debug(NettyRpcNode.class, "client remote call: " + context.toString());	
			channel.writeAndFlush(context);
			return rpcFuture;
		} else {
			return null;
		}
	}

}
