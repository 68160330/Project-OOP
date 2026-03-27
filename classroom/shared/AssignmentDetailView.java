package classroom.shared;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import classroom.database.DatabaseManager;
import classroom.student.UIHelper;

public class AssignmentDetailView extends JFrame {
    private String topic;
    private String studentId;
    private File selectedFile;
    private JLabel fileNameLabel;

    public AssignmentDetailView(String topic, String details, String studentId, String status, boolean isPastDue) {
        this.topic = topic;
        this.studentId = studentId;
        this.selectedFile = null;

        // Set Thai font for JOptionPane
        UIManager.put("OptionPane.messageFont", new Font("Leelawadee UI", Font.PLAIN, 14));
        UIManager.put("OptionPane.buttonFont", new Font("Leelawadee UI", Font.PLAIN, 14));

        setTitle("Assignment Details");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(24, 24, 27));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(24, 24, 27));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Topic: " + topic);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Leelawadee UI", Font.BOLD, 24));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JTextArea detailArea = new JTextArea(details);
        detailArea.setFont(new Font("Leelawadee UI", Font.PLAIN, 16));
        detailArea.setBackground(new Color(39, 39, 42));
        detailArea.setForeground(new Color(228, 228, 231));
        detailArea.setLineWrap(true);
        detailArea.setWrapStyleWord(true);
        detailArea.setEditable(false);
        detailArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(detailArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(63, 63, 70), 1));
        mainPanel.add(scrollPane);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(24, 24, 27));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        if (!studentId.isEmpty()) {
            JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            actionPanel.setBackground(new Color(24, 24, 27));

            if (status.equalsIgnoreCase("PENDING")) {
                JButton browseBtn = new JButton("Browse File");
                UIHelper.styleButton(browseBtn, new Color(59, 130, 246), new Color(37, 99, 235));
                browseBtn.addActionListener(e -> chooseFile());

                fileNameLabel = new JLabel("No file selected");
                fileNameLabel.setForeground(Color.LIGHT_GRAY);
                fileNameLabel.setFont(new Font("Leelawadee UI", Font.PLAIN, 14));

                JButton submitBtn = new JButton("Submit");
                UIHelper.styleButton(submitBtn, new Color(16, 185, 129), new Color(5, 150, 105));
                submitBtn.addActionListener(e -> submitFile());

                actionPanel.add(browseBtn);
                actionPanel.add(fileNameLabel);
                actionPanel.add(submitBtn);
            } else if (status.equalsIgnoreCase("SUBMITTED")) {
                JLabel msg = new JLabel("สถานะ: ส่งงานเรียบร้อยแล้ว");
                msg.setForeground(new Color(16, 185, 129));
                msg.setFont(new Font("Leelawadee UI", Font.BOLD, 14));
                actionPanel.add(msg);
                
                if (!isPastDue) {
                    JButton cancelBtn = new JButton("Cancel Submission");
                    UIHelper.styleButton(cancelBtn, new Color(239, 68, 68), new Color(220, 38, 38));
                    cancelBtn.addActionListener(e -> cancelSubmission());
                    actionPanel.add(cancelBtn);
                }
            } else if (status.equalsIgnoreCase("LATE")) {
                JLabel lateLabel = new JLabel("สถานะ: เลยกำหนดส่ง (ปิดรับการส่งงาน)");
                lateLabel.setForeground(new Color(239, 68, 68));
                lateLabel.setFont(new Font("Leelawadee UI", Font.BOLD, 14));
                actionPanel.add(lateLabel);
            }
            bottomPanel.add(actionPanel, BorderLayout.WEST);
        }

        JButton closeBtn = new JButton("Close");
        UIHelper.styleButton(closeBtn, new Color(70, 70, 70), new Color(90, 90, 90));
        closeBtn.addActionListener(e -> dispose());
        bottomPanel.add(closeBtn, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void chooseFile() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }

        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Allowed Files", "pdf", "docx", "doc", "jpg", "png");
        fileChooser.setFileFilter(filter);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            fileNameLabel.setText(selectedFile.getName());
        }

        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void submitFile() {
        if (selectedFile == null) {
            JOptionPane.showMessageDialog(this, "กรุณาเลือกไฟล์ก่อนส่ง");
            return;
        }
        DatabaseManager.submitWork(topic, studentId, selectedFile.getAbsolutePath());
        JOptionPane.showMessageDialog(this, "ส่งงานสำเร็จแล้ว");
        dispose();
    }

    private void cancelSubmission() {
        int confirm = JOptionPane.showConfirmDialog(this, "ต้องการยกเลิกการส่งงานเพื่อส่งใหม่ใช่หรือไม่?", "ยืนยันการยกเลิก", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DatabaseManager.cancelSubmission(topic, studentId);
            JOptionPane.showMessageDialog(this, "ยกเลิกการส่งงานแล้ว คุณสามารถส่งงานใหม่ได้ในหมวด Pending");
            dispose();
        }
    }
}