package classroom.professor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import classroom.database.DatabaseManager;

public class SubmissionListView extends JFrame {

    private JPanel contentPanel;
    private String topic;
    private Font thaiFont = new Font("Leelawadee UI", Font.PLAIN, 14);
    private Font boldThaiFont = new Font("Leelawadee UI", Font.BOLD, 14);

    public SubmissionListView(String topic) {
        this.topic = topic;

        // กำหนดฟอนต์ภาษาไทยให้ JOptionPane ทั่วทั้งคลาส
        UIManager.put("OptionPane.messageFont", thaiFont);
        UIManager.put("OptionPane.buttonFont", thaiFont);

        setTitle("Submissions: " + topic);
        setSize(750, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(24, 24, 27));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(750, 60));
        topBar.setBackground(new Color(40, 40, 40));
        topBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel("รายชื่อนิสิตและไฟล์ที่ส่ง");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Leelawadee UI", Font.BOLD, 18));
        topBar.add(titleLabel, BorderLayout.WEST);

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn, new Color(70, 70, 70), new Color(90, 90, 90));
        closeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        topBar.add(closeBtn, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(24, 24, 27));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(24, 24, 27));

        add(scrollPane, BorderLayout.CENTER);

        loadSubmissions();
    }

    private void loadSubmissions() {
        contentPanel.removeAll();
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                 "SELECT student_id, file_path FROM submissions WHERE topic = ? ORDER BY student_id ASC")) {
            
            pstmt.setString(1, topic);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                addSubmissionRow(rs.getString("student_id"), rs.getString("file_path"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void addSubmissionRow(final String studentId, final String filePath) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(new Color(30, 30, 30));
        row.setMaximumSize(new Dimension(710, 60));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(63, 63, 70)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        File file = new File(filePath);
        String fileName = file.getName();

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(new Color(30, 30, 30));

        JLabel idLabel = new JLabel("ID: " + studentId);
        idLabel.setForeground(Color.WHITE);
        idLabel.setFont(boldThaiFont);

        JLabel fileLabel = new JLabel("File: " + fileName);
        fileLabel.setForeground(Color.LIGHT_GRAY);
        fileLabel.setFont(new Font("Leelawadee UI", Font.PLAIN, 13));

        leftPanel.add(idLabel);
        leftPanel.add(fileLabel);

        JButton dlBtn = new JButton("Download");
        styleButton(dlBtn, new Color(34, 197, 94), new Color(22, 163, 74));
        dlBtn.setPreferredSize(new Dimension(120, 30));
        dlBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startDownload(filePath);
            }
        });

        row.add(leftPanel, BorderLayout.WEST);
        row.add(dlBtn, BorderLayout.EAST);

        contentPanel.add(row);
        contentPanel.add(Box.createVerticalStrut(5));
    }

    private void startDownload(String sourcePath) {
        File sourceFile = new File(sourcePath);
        if (!sourceFile.exists()) {
            JOptionPane.showMessageDialog(this, "ไม่พบไฟล์ต้นฉบับในระบบ");
            return;
        }

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        JFileChooser saver = new JFileChooser();
        saver.setSelectedFile(new File(sourceFile.getName()));
        saver.setDialogTitle("บันทึกไฟล์งาน");

        int result = saver.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File destFile = saver.getSelectedFile();
            try {
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                // คืนค่า Look and Feel เพื่อให้ JOptionPane ใช้ฟอนต์ที่ตั้งไว้
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                
                JOptionPane.showMessageDialog(this, "ดาวน์โหลดสำเร็จ");
                Desktop.getDesktop().open(destFile.getParentFile());
            } catch (Exception e) {
                try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception ex) {}
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการบันทึกไฟล์");
                e.printStackTrace();
            }
        } else {
            try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (Exception e) {}
        }
    }

    private void styleButton(final JButton btn, final Color normal, final Color hover) {
        btn.setBackground(normal);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Leelawadee UI", Font.BOLD, 12));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(hover); }
            public void mouseExited(MouseEvent e) { btn.setBackground(normal); }
        });
    }
}