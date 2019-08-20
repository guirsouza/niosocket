package org.guirsouza;

import java.util.Map;

import java.util.WeakHashMap;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChannelMapper {

	private static final ChannelMapper INSTANCE = new ChannelMapper();

	public static ChannelMapper getInstance() {
		return INSTANCE;
	}

    private final ChannelGroup allChannels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	private final Map<String, Channel> channels = new WeakHashMap<>();

	public boolean add(Channel channel) {
		System.out.println(channel.id().asLongText() + " Added to list");

		channels.put(channel.id().asLongText(), channel);
		channel.closeFuture().addListener(__ -> remove(channel));
		return allChannels.add(channel);
	}

	public ChannelGroupFuture close() {
		System.out.println("Closing all channels");
		return allChannels.close();
	}

	public void remove(Channel channel) {
        System.out.println(channel.id().asLongText() + " Removed from list");
		channels.remove(channel.id().asLongText());
	}

	ChannelGroup getChannelGroup() {
		return allChannels;
	}

}
