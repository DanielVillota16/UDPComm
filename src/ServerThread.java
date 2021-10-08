import java.io.*;
import java.net.*;
import java.util.LinkedList;

public class ServerThread extends Thread {

	protected DatagramSocket socket;
	
	public ServerThread() throws IOException {
		this("QuoteServerThread");
	}

	public ServerThread(String name) throws IOException {
		super(name);
		socket = new DatagramSocket(4445);
	}

	public void run() {
		int lost = 0;
		int count = -1;
		LinkedList<Integer> lostPackets = new LinkedList<>();
		while (count < 100) {
			try {
				byte[] buf = new byte[256];
				// receive request
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				// figure out datagram's number
				buf = packet.getData();
				String dString = new String(buf, 0, buf.length);
				int number = Integer.parseInt(dString.substring(0, 4));
				System.out.println(dString);
				if (number <= count+1) {
					String conf = "SUCCESS";
					buf = conf.getBytes();
					packet.setData(buf);
					socket.send(packet);
					lost++;
					lostPackets.offer(count);
					if (number == count+1) {
						count++;
						lost--;
						lostPackets.removeLast();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.printf("Number of lost packets: %d%n", lost);
		System.out.printf("Lost packets: %s%n", lostPackets);
		socket.close();
	}
}
