package com.smartserve.api.claims;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String toEmail, String foodTitle, String otp, Integer qty) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("your-email@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject("Your SmartServe Claim Successful! Thanks for making a difference today 🎁");

            // --- HTML EMAIL TEMPLATE ---
            String htmlContent = 
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; border-radius: 10px; overflow: hidden;'>" +
                    "<div style='background-color: #903030; padding: 20px; text-align: center;'>" +
                        "<h1 style='color: white; margin: 0; font-size: 24px;'>SmartServe</h1>" +
                    "</div>" +
                    "<div style='padding: 30px; color: #333; line-height: 1.6;'>" +
                        "<h2 style='color: #2D1B1B;'>Booking Confirmed!</h2>" +
                        "<p>Hi there,</p>" +
                        "<p>Your request to rescue <b>" + qty + "x " + foodTitle + "</b> has been successfully processed.</p>" +
                        
                        "<div style='background-color: #fff5f5; border: 2px dashed #E57373; padding: 20px; text-align: center; margin: 25px 0; border-radius: 15px;'>" +
                            "<p style='margin: 0; color: #666; font-size: 14px; text-transform: uppercase; font-weight: bold;'>Your Secure Handshake OTP</p>" +
                            "<h1 style='margin: 10px 0; color: #E57373; font-size: 48px; letter-spacing: 10px;'>" + otp + "</h1>" +
                        "</div>" +

                        "<p style='font-size: 14px; color: #777;'>Please present this code to the donor at the pickup location to complete the verification.</p>" +
                    "</div>" +
                    "<div style='background-color: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #999;'>" +
                        "<p>Thank you for helping us reduce food waste! 🌱</p>" +
                        "<p>&copy; 2026 SmartServe Project | Thiruvananthapuram</p>" +
                    "</div>" +
                "</div>";

            helper.setText(htmlContent, true); // The 'true' tells Spring this is HTML!
            mailSender.send(message);
            
            System.out.println("HTML Email sent successfully to " + toEmail);

        } catch (Exception e) {
            System.err.println("Failed to send HTML email: " + e.getMessage());
        }
    }
    
    @Async
    public void sendCancellationEmail(String toEmail, String foodTitle, Integer qty) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("your-email@gmail.com"); // Make sure this matches your properties file
            helper.setTo(toEmail);
            helper.setSubject("Order Cancelled: SmartServe Rescue Update");

            // --- HTML EMAIL TEMPLATE FOR CANCELLATION ---
            String htmlContent = 
                "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; border-radius: 10px; overflow: hidden;'>" +
                    "<div style='background-color: #64748B; padding: 20px; text-align: center;'>" + // Gray header
                        "<h1 style='color: white; margin: 0; font-size: 24px;'>SmartServe</h1>" +
                    "</div>" +
                    "<div style='padding: 30px; color: #333; line-height: 1.6;'>" +
                        "<h2 style='color: #2D1B1B;'>Booking Cancelled</h2>" +
                        "<p>Hi there,</p>" +
                        "<p>As requested, your food rescue order has been successfully cancelled.</p>" +
                        
                        "<div style='background-color: #F8FAFC; border: 1px solid #E2E8F0; padding: 15px; margin: 20px 0; border-radius: 10px;'>" +
                            "<p style='margin: 0; color: #475569;'><b>Cancelled Item:</b> " + qty + "x " + foodTitle + "</p>" +
                        "</div>" +

                        "<p style='font-size: 14px; color: #777;'>The food has been returned to the marketplace so another community member can claim it. We hope to see you again soon!</p>" +
                    "</div>" +
                    "<div style='background-color: #f9f9f9; padding: 20px; text-align: center; font-size: 12px; color: #999;'>" +
                        "<p>&copy; 2026 SmartServe Project | Thiruvananthapuram</p>" +
                    "</div>" +
                "</div>";

            helper.setText(htmlContent, true);
            mailSender.send(message);
            
            System.out.println("Cancellation Email sent successfully to " + toEmail);

        } catch (Exception e) {
            System.err.println("Failed to send cancellation email: " + e.getMessage());
        }
    }
}