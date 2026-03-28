package com.vinay.quizengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class QuizController {

    @Autowired
    private QuizResultRepository quizResultRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/quiz")
    public String quiz(@RequestParam String name, Model model) {
        model.addAttribute("name", name);
        return "Quiz";
    }

    @PostMapping("/result")
    public String result(@RequestParam String name,
                         @RequestParam String q1,
                         @RequestParam String q2,
                         @RequestParam String q3,
                         @RequestParam String q4,
                         @RequestParam String q5,
                         @RequestParam String q6,
                         @RequestParam String q7,
                         @RequestParam String q8,
                         @RequestParam String q9,
                         @RequestParam String q10,
                         @RequestParam String q11,
                         @RequestParam String q12,
                         Model model) {

        int score = 0;

        // Each correct answer = 1 mark
        if (q1.equals("4")) score++;
        if (q2.equals("Paris")) score++;
        if (q3.equals("Shakespeare")) score++;
        if (q4.equals("Mars")) score++;
        if (q5.equals("Pacific")) score++;
        if (q6.equals("New Delhi")) score++;
        if (q7.equals("Tokyo")) score++;
        if (q8.equals("Beijing")) score++;
        if (q9.equals("Cascading Style Sheets")) score++;
        if (q10.equals("JavaScript Object Notation")) score++;
        if (q11.equals("Document Object Model")) score++;
        if (q12.equals("Extensible Markup Language")) score++;

        // Pass = 6 or more out of 12 (50%)
        boolean passed = score >= 6;
        String status = passed ? "PASS" : "FAIL";
        String message;
        if (score == 12) {
            message = "Perfect Score! Outstanding performance! 🏆";
        } else if (score >= 10) {
            message = "Excellent! You did great! 🌟";
        } else if (score >= 6) {
            message = "Good job! You passed the quiz! ✅";
        } else if (score >= 4) {
            message = "So close! Better luck next time. 📚";
        } else {
            message = "Keep practicing and try again! 💪";
        }

        // Save to database
        QuizResult result = new QuizResult(name, score, LocalDate.now());
        quizResultRepository.save(result);

        model.addAttribute("name", name);
        model.addAttribute("score", score);
        model.addAttribute("passed", passed);
        model.addAttribute("status", status);
        model.addAttribute("message", message);

        return "result";
    }

    @GetMapping("/generate-certificate")
    public void generateCertificate(
            @RequestParam String name,
            @RequestParam int score,
            HttpServletResponse response) {

        try {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=certificate.pdf");

            Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
            Document document = new Document(pageSize, 60, 60, 60, 60);
            PdfWriter writer = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            PdfContentByte canvas = writer.getDirectContent();
            float w = pageSize.getWidth();
            float h = pageSize.getHeight();

            // Background
            canvas.setColorFill(new BaseColor(10, 10, 15));
            canvas.rectangle(0, 0, w, h);
            canvas.fill();

            // Grid pattern
            canvas.setColorStroke(new BaseColor(200, 255, 0, 18));
            canvas.setLineWidth(0.3f);
            for (int x = 60; x < w; x += 60) {
                canvas.moveTo(x, 0); canvas.lineTo(x, h); canvas.stroke();
            }
            for (int y = 60; y < h; y += 60) {
                canvas.moveTo(0, y); canvas.lineTo(w, y); canvas.stroke();
            }

            // Outer border
            canvas.setColorStroke(new BaseColor(200, 255, 0, 160));
            canvas.setLineWidth(1.5f);
            canvas.rectangle(24, 24, w - 48, h - 48);
            canvas.stroke();

            // Inner border
            canvas.setColorStroke(new BaseColor(200, 255, 0, 60));
            canvas.setLineWidth(0.5f);
            canvas.rectangle(34, 34, w - 68, h - 68);
            canvas.stroke();

            // Corner brackets
            canvas.setColorStroke(new BaseColor(200, 255, 0));
            canvas.setLineWidth(2f);
            int cSize = 22;
            float[][] corners = {
                {42, h - 42}, {w - 42, h - 42}, {42, 42}, {w - 42, 42}
            };
            for (float[] c : corners) {
                float cx = c[0], cy = c[1];
                float dx = (cx < w / 2) ? 1 : -1;
                float dy = (cy > h / 2) ? -1 : 1;
                canvas.moveTo(cx, cy);
                canvas.lineTo(cx + dx * cSize, cy);
                canvas.stroke();
                canvas.moveTo(cx, cy);
                canvas.lineTo(cx, cy + dy * cSize);
                canvas.stroke();
            }

            // Top accent line
            canvas.setColorStroke(new BaseColor(200, 255, 0));
            canvas.setLineWidth(2f);
            canvas.moveTo(w / 2 - 160, h - 50);
            canvas.lineTo(w / 2 + 160, h - 50);
            canvas.stroke();

            // Badge pill
            float badgeW = 200, badgeH = 26, badgeX = (w - badgeW) / 2, badgeY = h - 88;
            canvas.setColorFill(new BaseColor(200, 255, 0, 30));
            canvas.roundRectangle(badgeX, badgeY, badgeW, badgeH, 13);
            canvas.fill();
            canvas.setColorStroke(new BaseColor(200, 255, 0, 180));
            canvas.setLineWidth(0.8f);
            canvas.roundRectangle(badgeX, badgeY, badgeW, badgeH, 13);
            canvas.stroke();
            canvas.setColorFill(new BaseColor(200, 255, 0));
            canvas.circle(badgeX + 16, badgeY + 13, 4);
            canvas.fill();

            Font badgeFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, new BaseColor(200, 255, 0));
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("QUIZ ENGINE  ·  CERTIFICATE OF ACHIEVEMENT", badgeFont), w / 2 + 8, badgeY + 9, 0);

            // Main Title
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 38, Font.BOLD, new BaseColor(240, 240, 245));
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("Certificate of Achievement", titleFont), w / 2, h - 142, 0);

            // Divider
            canvas.setColorStroke(new BaseColor(255, 255, 255, 30));
            canvas.setLineWidth(0.5f);
            canvas.moveTo(w / 2 - 220, h - 160);
            canvas.lineTo(w / 2 + 220, h - 160);
            canvas.stroke();

            // This is to certify that
            Font subFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, new BaseColor(160, 160, 180));
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("This is to certify that", subFont), w / 2, h - 192, 0);

            // Name
            Font nameFont = new Font(Font.FontFamily.HELVETICA, 46, Font.BOLD, new BaseColor(200, 255, 0));
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase(name, nameFont), w / 2, h - 255, 0);

            // Underline under name
            canvas.setColorStroke(new BaseColor(200, 255, 0, 120));
            canvas.setLineWidth(1.2f);
            canvas.moveTo(w / 2 - 200, h - 268);
            canvas.lineTo(w / 2 + 200, h - 268);
            canvas.stroke();

            // Completion text
            Font compFont = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, new BaseColor(200, 200, 215));
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("has successfully completed the QuizEngine Assessment", compFont), w / 2, h - 295, 0);

            // ── Score + Result box side by side ──
            // Score box
            float boxW = 200, boxH = 74, boxX = w / 2 - boxW - 10, boxY = h - 400;
            canvas.setColorFill(new BaseColor(22, 22, 31));
            canvas.roundRectangle(boxX, boxY, boxW, boxH, 14);
            canvas.fill();
            canvas.setColorStroke(new BaseColor(200, 255, 0, 140));
            canvas.setLineWidth(1.2f);
            canvas.roundRectangle(boxX, boxY, boxW, boxH, 14);
            canvas.stroke();

            Font scoreLabelFont = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, new BaseColor(150, 150, 165));
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("MARKS SCORED", scoreLabelFont), boxX + boxW / 2, boxY + boxH - 18, 0);

            Font scoreFont = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD, new BaseColor(200, 255, 0));
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase(score + "  /  12", scoreFont), boxX + boxW / 2, boxY + 18, 0);

            // Result box (PASS / FAIL)
            boolean passed = score >= 6;
            float rboxX = w / 2 + 10;
            BaseColor resultColor = passed ? new BaseColor(200, 255, 0) : new BaseColor(255, 77, 109);
            BaseColor resultBg    = passed ? new BaseColor(200, 255, 0, 25) : new BaseColor(255, 77, 109, 25);
            BaseColor resultBorder = passed ? new BaseColor(200, 255, 0, 140) : new BaseColor(255, 77, 109, 140);

            canvas.setColorFill(resultBg);
            canvas.roundRectangle(rboxX, boxY, boxW, boxH, 14);
            canvas.fill();
            canvas.setColorStroke(resultBorder);
            canvas.setLineWidth(1.2f);
            canvas.roundRectangle(rboxX, boxY, boxW, boxH, 14);
            canvas.stroke();

            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("RESULT", scoreLabelFont), rboxX + boxW / 2, boxY + boxH - 18, 0);

            Font resultFont = new Font(Font.FontFamily.HELVETICA, 28, Font.BOLD, resultColor);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase(passed ? "PASS" : "FAIL", resultFont), rboxX + boxW / 2, boxY + 18, 0);

            // Score bar
            float barW = 320, barH = 6, barX = (w - barW) / 2, barY = boxY - 26;
            canvas.setColorFill(new BaseColor(30, 30, 40));
            canvas.roundRectangle(barX, barY, barW, barH, 3);
            canvas.fill();
            float fillW = (float) score / 12 * barW;
            canvas.setColorFill(new BaseColor(200, 255, 0));
            canvas.roundRectangle(barX, barY, fillW, barH, 3);
            canvas.fill();

            // Percentage
            int pct = score * 100 / 12;
            Font pctFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, new BaseColor(200, 255, 0));
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase(pct + "% — " + (passed ? "Passed with " + score + " correct answers"
                    : "Need 6+ to pass. Got " + score + " correct answers"), pctFont),
                w / 2, barY - 16, 0);

            // Bottom divider
            canvas.setColorStroke(new BaseColor(255, 255, 255, 20));
            canvas.setLineWidth(0.5f);
            canvas.moveTo(80, 118);
            canvas.lineTo(w - 80, 118);
            canvas.stroke();

            // Bottom info row
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
            Font infoLabelFont = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, new BaseColor(107, 107, 128));
            Font infoValFont   = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, new BaseColor(220, 220, 230));

            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase("DATE ISSUED", infoLabelFont), 80, 106, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                new Phrase(dateStr, infoValFont), 80, 88, 0);

            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("ISSUED BY", infoLabelFont), w / 2, 106, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_CENTER,
                new Phrase("QuizEngine Platform", infoValFont), w / 2, 88, 0);

            Font statusFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, resultColor);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,
                new Phrase("STATUS", infoLabelFont), w - 80, 106, 0);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT,
                new Phrase(passed ? "PASSED" : "FAILED", statusFont), w - 80, 88, 0);

            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}