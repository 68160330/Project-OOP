package classroom.student;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage; // เพิ่มการนำเข้า
import java.awt.image.RescaleOp; // เพิ่มการนำเข้า
import javax.swing.JPanel;

public class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(Image background) {
        this.backgroundImage = background;
        setLayout(new GridBagLayout());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 1. วาดสีพื้นหลังเดิมเป็นสีพื้นฐาน (สีเข้ม) เหมือนเดิม
        g.setColor(new Color(24, 24, 27));
        g.fillRect(0, 0, getWidth(), getHeight());

        // 2. ถ้ามีรูปภาพพื้นหลัง ให้วาดด้วย Opacity ที่กำหนด
        if (backgroundImage != null) {
            // สร้าง BufferedImage เพื่อนำมาปรับ Opacity
            BufferedImage bufferedImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics g2d = bufferedImage.getGraphics();
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            g2d.dispose();

            // สร้าง RescaleOp เพื่อปรับ Opacity
            // พารามิเตอร์คือ {R, G, B, A} โดย A (Alpha) คือ Opacity
            // ในที่นี้คือ 0.5f (50% Opacity)
            RescaleOp rescaleOp = new RescaleOp(new float[]{1f, 1f, 1f, 0.5f}, new float[]{0f, 0f, 0f, 0f}, null);

            // ใช้ RescaleOp กับ BufferedImage
            bufferedImage = rescaleOp.filter(bufferedImage, null);

            // วาด BufferedImage ที่ได้รับการปรับ Opacity แล้ว
            g.drawImage(bufferedImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}