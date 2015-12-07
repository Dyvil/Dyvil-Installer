package dyvil.tools.installer.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;

public class Downloader
{
	public static String download(String urlString) throws IOException
	{
		URL url = new URL(urlString);
		InputStream inputStream = url.openStream();
		ReadableByteChannel rbc = Channels.newChannel(inputStream);

		ByteBuffer bytebuf = ByteBuffer.allocate(1024);

		rbc.read(bytebuf);
		return new String(bytebuf.array(), 0, bytebuf.position(), StandardCharsets.UTF_8);
	}
}
