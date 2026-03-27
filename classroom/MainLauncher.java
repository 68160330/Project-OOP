package classroom;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import classroom.database.DatabaseManager;
import classroom.professor.TeacherDashboard;
import classroom.student.StudentLogin;

public class MainLauncher extends JFrame {

public MainLauncher() {
    // ตั้งค่าฟอนต์ภาษาไทยสำหรับหน้าจอเลือก
    Font thaiFont = new Font("Leelawadee UI", Font.BOLD, 18);
    UIManager.put("Button.font", thaiFont);
    UIManager.put("Label.font", thaiFont);

    setTitle("Classroom System Launcher");
    setSize(500, 300);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);
    setLayout(new BorderLayout());

    JPanel panel = new JPanel(new GridLayout(1, 2, 20, 0));
    panel.setBackground(new Color(24, 24, 27));
    panel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

    JButton teacherBtn = new JButton("Professor");
    teacherBtn.setBackground(new Color(139, 92, 246));
    teacherBtn.setForeground(Color.WHITE);
    teacherBtn.setFocusPainted(false);
    teacherBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            new TeacherDashboard().setVisible(true);
            dispose();
        }
    });

    JButton studentBtn = new JButton("Student");
    studentBtn.setBackground(new Color(59, 130, 246));
    studentBtn.setForeground(Color.WHITE);
    studentBtn.setFocusPainted(false);
    studentBtn.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            new StudentLogin().setVisible(true);
            dispose();
        }
    });

    panel.add(teacherBtn);
    panel.add(studentBtn);

    JLabel label = new JLabel("เลือกสถานะเพื่อเข้าสู่ระบบ", SwingConstants.CENTER);
    label.setForeground(Color.WHITE);
    label.setOpaque(true);
    label.setBackground(new Color(24, 24, 27));
    label.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

    add(label, BorderLayout.NORTH);
    add(panel, BorderLayout.CENTER);
}

public static void main(String[] args) {
    DatabaseManager.initialize();
    new MainLauncher().setVisible(true);
}
}