import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.Scanner;
import java.awt.Color.*;

public class Client {
    public static void main(String[] args) {
		UserWindow connection = new UserWindow();
		connection.start();
	}
}

class UserWindow{
	JFrame jf = new JFrame("Finance");
	JPanel jp = new JPanel(new BorderLayout(5,5));
	Socket client;
	Scanner in;
	PrintWriter out;
    
    JButton deposit = new JButton("Deposit");
	JButton withdraw = new JButton("Withdraw");
    JTextField inputField = new JTextField();
    //Login
	JFrame login;
    JPanel loginjp;
    String username;
	JTextField user;
	JTextField pw;

    JLabel logintext;

	//Bank Account
    String balance;
    JLabel balAmount;
	JLabel bankingtext;

	UserWindow() {
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	// start the GUI
	public void start() {
		loginjp = new JPanel();
		login = new JFrame("Login");

		loginjp.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
		userLabel.setBounds(20, 20, 80,25);;
		loginjp.add(userLabel);
		user = new JTextField();
		user.setBounds(100,20,165,25);
		loginjp.add(user);

		JLabel pwLabel = new JLabel("Password:");
		pwLabel.setBounds(20,60,80,25);
		loginjp.add(pwLabel);
		pw = new JTextField();
		pw.setBounds(100,60,165,25);
		loginjp.add(pw);

		JButton submit = new JButton("Login");
		submit.setBounds(90, 100,100,25);
		submit.addActionListener(new LoginSubmit());
		loginjp.add(submit);

		JButton register = new JButton("Register");
		register.setBounds(90, 140,100,25);
        register.addActionListener(new RegisterSubmit());
		loginjp.add(register);

        logintext = new JLabel();
		logintext.setBounds(30,180,300,25);
        loginjp.add(logintext);

		login.add(loginjp);

        login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		login.setSize(300, 250);
		login.setLocationRelativeTo(null);
		login.setVisible(true);
	}
	
	class LoginSubmit implements ActionListener {
		@Override
        public void actionPerformed(ActionEvent e) {
			try {
				client = new Socket(InetAddress.getByName("localhost"), 5190);
				in = new Scanner(client.getInputStream());
				out = new PrintWriter(client.getOutputStream(), true);
                out.println("login");
				out.println(user.getText());
				out.println(pw.getText());
				String msg = in.nextLine();

				if (msg.equals("200")) { //User exists 
                    login.setVisible(false);

			        jp.setLayout(null);

                    JLabel balLabel = new JLabel("Balance:");
                    balLabel.setBounds(20, 20, 80,25);;
                    jp.add(balLabel);
                    balAmount = new JLabel("$" + in.nextLine());
                    balAmount.setBounds(100,20,165,25);
                    jp.add(balAmount);

                    inputField.setBounds(10,60,70,25);
                    jp.add(inputField);

                    deposit.setBounds(100,60,165,25);
                    jp.add(deposit);

					withdraw.setBounds(100,100,165,25);
                    jp.add(withdraw);

					box bx = new box();
					bx.setBounds(0,0,300,180);
					jp.add(bx);
					
                    deposit.addActionListener(new Deposit());
					withdraw.addActionListener(new Withdraw());

					bankingtext = new JLabel();
					bankingtext.setBounds(10,140,300,25);
					jp.add(bankingtext);

                    jf.add(jp);

                    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    jf.setSize(300, 250);
                    jf.setLocationRelativeTo(null);
                    jf.setVisible(true);

				} else {//User login error
                    logintext.setText("Incorrect credentials. Please Try again");
                    login.repaint();
				}
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}
	
    class RegisterSubmit implements ActionListener {
		@Override
        public void actionPerformed(ActionEvent e) {
			try {
				client = new Socket(InetAddress.getByName("localhost"), 5190);
				in = new Scanner(client.getInputStream());
				out = new PrintWriter(client.getOutputStream(), true);
                out.println("register");
				out.println(user.getText());
				out.println(pw.getText());
				String msg = in.nextLine();

				if (msg.equals("200")) { //User exists 
                    logintext.setText("User created. You can login.");
                    login.repaint();
				} else {//User login error
                    logintext.setText("Duplicate user found. Please try again.");
                    login.repaint();
				}

                client.close();
			} catch (IOException io) {
				io.printStackTrace();
			}
		}
	}

	class Deposit implements ActionListener {
		@Override
        public void actionPerformed(ActionEvent e) {
            String amount = inputField.getText();
            inputField.setText("");
            out.println("deposit");
            out.println(amount);
			String dep = in.nextLine();
			if(!dep.equals("failed")){
				balance = dep;
				balAmount.setText("$"+balance);
				bankingtext.setText("$" + amount + " deposited");
				jf.repaint();
			}else{
				bankingtext.setText("Deposit failed");
				jf.repaint();
			}
        }
    }

	class Withdraw implements ActionListener {
		@Override
        public void actionPerformed(ActionEvent e) {
            String amount = inputField.getText();
            inputField.setText("");
            out.println("withdraw");
            out.println(amount);
			String wd = in.nextLine();
			if(!wd.equals("failed")){
				balance = wd;
				balAmount.setText("$"+balance);
				bankingtext.setText("$" + amount + " withdrew");
				jf.repaint();
			}else{
				bankingtext.setText("Withdraw failed");
				jf.repaint();
			}
        }
    }
	
	public void paint(Graphics g){
		g.setColor(new Color(100,50,150));
		g.drawRect(100, 100, 50, 50);
	}

	class box extends JComponent{
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);  
			g.drawRect(10,10,70,40);  

			g.drawRect(90,10,70,40);    

			g.drawRect(0,55,300,80);  
			g.setColor(new Color(173,216,230));  
			g.fillRect(0,55,300,80);  
		}
		
		public Dimension getPreferredSize() {
			return new Dimension(200,200); // appropriate constants
		}
		
	}

}

