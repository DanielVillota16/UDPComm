import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

	public static void main(String[] args) throws IOException, InterruptedException {
		Scanner sc = new Scanner(System.in);
		String ip = "127.0.0.1";
		if (args.length == 1) ip = args[0];
		System.out.print("Enter the packets size [Bytes]: ");
		int size = Integer.parseInt(sc.nextLine());
		size = size > 3 ? size: 4;
		System.out.print("Enter Tx rate [datagrams/s]: ");
		int rate = Integer.parseInt(sc.nextLine());

		DatagramSocket socket = new DatagramSocket();
		InetAddress address = InetAddress.getByName(ip);

		int count = 0;
		while(count <= 100) {
			String str = format(count);
			for (int i = 0; i < size-4; i++) str += "A";
			byte[] buf = str.getBytes();
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
			socket.send(packet);
			buf = new byte[7];
			packet.setData(buf);
			try {
				socket.setSoTimeout(1000/rate);
				socket.receive(packet);
				buf = packet.getData();
				String ans = new String(buf, 0, buf.length);
				if(ans.equals("SUCCESS")) {
					count++;
				}
			} catch(SocketTimeoutException e) {
				System.out.printf("Timeout after packet number %d%n", count);
			}
		}
		socket.close();
		sc.close();
	}

	public static String format(int n) {
		int count = 0, n1 = n;
		while(n1 > 0) {
			n1/=10;
			count++;
		}
		String ans = "";
		for (int i = 0; i < 4-count; i++) ans += "0";
		return n>0 ? ans+n : ans;
	}
}