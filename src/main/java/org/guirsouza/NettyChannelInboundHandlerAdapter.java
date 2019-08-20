package org.guirsouza;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyChannelInboundHandlerAdapter extends ChannelInboundHandlerAdapter {

    private ChannelMapper channelMapper() {
		return ChannelMapper.getInstance();
	}

    private String byteBufferToString(Object msg) {
        ByteBuf inBuffer = (ByteBuf) msg;
        return inBuffer.toString(CharsetUtil.UTF_8);
    }

    @Override
	public void channelActive(ChannelHandlerContext ctx) {
		channelMapper().add(ctx.channel());
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		channelMapper().remove(ctx.channel());
	}

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String received = byteBufferToString(msg);
        received = received.replace(System.getProperty("line.separator"), "");
        System.out.println(ctx.channel().id().asLongText() + " read: " + received);
        channelWrite(ctx, received);
    }

    public void channelWrite(ChannelHandlerContext ctx, String message) throws Exception {
        ctx.write(Unpooled.copiedBuffer("> " + message + "\n", CharsetUtil.UTF_8));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

}