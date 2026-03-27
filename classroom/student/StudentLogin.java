package classroom.student;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon; // เพิ่มการนำเข้า ImageIcon
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import classroom.database.DatabaseManager;

public class StudentLogin extends JFrame {
    private JTextField idField;

    public StudentLogin() {
        setTitle("Student Login");
        setSize(1280, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- โหลดรูปภาพพื้นหลัง ---
        // เปลี่ยน path ไปยังไฟล์รูปภาพของคุณ เช่น "images/login_bg.jpg"
        ImageIcon icon = new ImageIcon("E:\\--- Drive E ---\\Documents\\Java Projects\\Visual\\OOP_Final\\OOP_submitwork\\src\\Background.jpg"); 
  

        // --- สร้าง BackgroundPanel พร้อมรูปภาพ ---
        BackgroundPanel bg = new BackgroundPanel(icon.getImage()); 
        setContentPane(bg);

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(400, 300));
        card.setBackground(new Color(39, 39, 42)); 
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Leelawadee UI", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        idField = new JTextField();
        idField.setMaximumSize(new Dimension(300, 40));
        idField.setFont(new Font("Leelawadee UI", Font.PLAIN, 18));
        idField.setBackground(new Color(63, 63, 70));
        idField.setForeground(Color.WHITE);
        idField.setCaretColor(Color.WHITE);

        JButton loginBtn = new JButton("Login");
        loginBtn.setMaximumSize(new Dimension(300, 45));
        loginBtn.setFont(new Font("Leelawadee UI", Font.BOLD, 18));
        loginBtn.setBackground(new Color(59, 130, 246));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);

        loginBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                if (!id.isEmpty()) {
                    new StudentDashboard(id).setVisible(true);
                    dispose();
                }
            }
        });

        card.add(title);
        card.add(Box.createVerticalStrut(30));
        card.add(idField);
        card.add(Box.createVerticalStrut(20));
        card.add(loginBtn);

        bg.add(card);
    }

    public static void main(String[] args) {
        DatabaseManager.initialize();
        new StudentLogin().setVisible(true);
    }
}