import java.net.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

public class Server {
    private ArrayList<UserConnection> connections;
	private ServerSocket server;
	private boolean isRunning;
	private DbConnect db = new DbConnect();

    public Server() {
		connections = new ArrayList<>();
		isRunning = true;
		db.connectToDatabase();
	}
	
	public void run() {
		try {  //New thread for user
			server = new ServerSocket(5190);

			while (isRunning) {
				Socket client = server.accept();
				UserConnection conn = new UserConnection(client);
				connections.add(conn);
				new Thread(conn).start();
			}
		} catch (IOException e) {
			shutdown();
		}
	}

    //Turn off server and close all user connections
    public void shutdown() {
		try {
			isRunning = false;
			if (!server.isClosed()) {
				server.close();
			}
			for (UserConnection conn : connections) {
				conn.shutdown();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    //User Class
    class UserConnection implements Runnable {
		private Socket client;
		private Scanner in;
		private PrintWriter out;
		private DbConnect db;

		private String username;
		private String password;
		double balance;
		double goal;
		
		public UserConnection(Socket client) {
			this.client = client;
			db = new DbConnect();
			db.connectToDatabase();
		}
		
		@Override
		public void run() {
            try {
                out = new PrintWriter(client.getOutputStream(), true);
                in = new Scanner(client.getInputStream());
				String type = in.nextLine();
                username = in.nextLine();
                password = in.nextLine();

				if(type.equals("login")){
					if (db.checkAccount(username, password)) {
						out.println(200);//Print 200 if user exists in db and can connect
					} else {
						out.println(500);
					}
				}else if(type.equals("register")){
					if(db.makeAccount(username, password)){
						out.println(200);
					}else{
						out.println(500);
					}
				}
				balance = db.getBalance(username, password);
				out.println(String.format( "%.2f", balance ));

				String message;
				while ((message = in.nextLine()) != null) {
                    if(message.equals("deposit")){
						String amt = in.nextLine(); 
						if(!amt.isEmpty() && isValid_USCurrency(amt)&& strToDouble(amt) != -0.001){
							System.out.println(amt);
							double newAmt= strToDouble(amt);
							db.deposit(username, password, newAmt);
							balance = db.getBalance(username, password);
							out.println(String.format( "%.2f", balance ));
						}else{
							out.println("failed");
						}
					}else if(message.equals("withdraw")){
						String amt = in.nextLine(); 
						if(!amt.isEmpty() && isValid_USCurrency(amt) && strToDouble(amt) != -0.001){
							System.out.println(amt);
							double newAmt= strToDouble(amt);
							db.withdraw(username, password, newAmt);
							balance = db.getBalance(username, password);
							out.println(String.format( "%.2f", balance ));
						}else{
							out.println("failed");
						}
					}
                }
            } catch (IOException e) {
                    shutdown();
			}
		}

		public void shutdown() {
			try {
				in.close();
				out.close();
				if (!client.isClosed()) {
					client.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public static boolean isValid_USCurrency(String str){
			if(str.charAt(0) != '$'){
				str = "$" + str;
			}
			Number number = null;
			try {
				number = NumberFormat.getCurrencyInstance(Locale.US).parse(str);
			} catch(ParseException pe) {
			}

			if (number != null) {
				return true;
			}
			else {
				return false;
			}
		}

		public static double strToDouble(String str){
			try { 
				return Double.parseDouble(str); 
			} 
			catch (Exception e) { 
				return -0.001;
			} 

		}
	}

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
