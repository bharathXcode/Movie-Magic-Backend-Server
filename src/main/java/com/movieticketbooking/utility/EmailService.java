package com.movieticketbooking.utility;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.movieticketbooking.entity.Booking;
import com.movieticketbooking.entity.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private final JavaMailSender javaMailSender;

	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void sendEmail(String to, String subject, String body) {

		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
			e.getMessage();
			throw new RuntimeException(e);
		}

	}

	public String getMailBody(User customer, List<Booking> bookings, String bookingId) {

		Booking helperBooking = bookings.get(0);

		StringBuilder emailBody = new StringBuilder();
		emailBody.append("<html><body style='font-family: Arial, sans-serif; color: #333;'>");

		// Greeting
		emailBody.append("<h2 style='color: #2E86C1;'>Dear " + customer.getFirstName() + ",</h2>");
		emailBody.append(
				"<p>Thank you for booking with <strong>Movie Magic</strong>! We’re excited to confirm your show reservation. Below are your booking details:</p>");
		emailBody.append("<p><strong>Booking ID:</strong> <span style='color: #2E86C1;'>" + bookingId + "</span></p>");

		// Show Details
		emailBody.append("<h3 style='margin-top: 30px;'>Show Details</h3>");
		emailBody.append("<ul style='line-height: 1.6;'>");
		emailBody.append("<li><strong>Movie:</strong> " + helperBooking.getShow().getMovie().getTitle() + "</li>");
		emailBody.append("<li><strong>Date & Time:</strong> " + helperBooking.getShow().getShowDate() + " | "
				+ helperBooking.getShow().getStartTime() + " - " + helperBooking.getShow().getEndTime() + "</li>");
		emailBody.append("<li><strong>Theatre:</strong> " + helperBooking.getShow().getTheatre().getName() + "</li>");
		emailBody.append("<li><strong>Screen:</strong> " + helperBooking.getShow().getScreen().getName() + "</li>");
		emailBody
				.append("<li><strong>Address:</strong> " + helperBooking.getShow().getTheatre().getAddress() + "</li>");
		emailBody.append("</ul>");

		// Seat Table
		emailBody.append("<h3 style='margin-top: 30px;'>🪑 Seat Details</h3>");
		emailBody.append("<table style='width: 100%; border-collapse: collapse;'>");
		emailBody.append("<thead>");
		emailBody.append("<tr style='background-color: #f2f2f2;'>");
		emailBody.append("<th style='border: 1px solid #ddd; padding: 8px;'>Seat No</th>");
		emailBody.append("<th style='border: 1px solid #ddd; padding: 8px;'>Seat Type</th>");
		emailBody.append("<th style='border: 1px solid #ddd; padding: 8px;'>Price</th>");
		emailBody.append("</tr>");
		emailBody.append("</thead>");
		emailBody.append("<tbody>");

		BigDecimal totalPrice = BigDecimal.ZERO;

		for (Booking booking : bookings) {
			emailBody.append("<tr>");
			emailBody.append("<td style='border: 1px solid #ddd; padding: 8px;'>")
					.append(booking.getShowSeat().getSeatNumber()).append("</td>");
			emailBody.append("<td style='border: 1px solid #ddd; padding: 8px;'>")
					.append(booking.getShowSeat().getSeatType()).append("</td>");
			emailBody.append("<td style='border: 1px solid #ddd; padding: 8px;'>₹")
					.append(booking.getShowSeat().getPrice()).append("</td>");
			emailBody.append("</tr>");

			totalPrice = totalPrice.add(booking.getShowSeat().getPrice());
		}

		emailBody.append("</tbody>");
		emailBody.append("</table>");

		// Total Amount
		emailBody.append("<h3 style='margin-top: 20px;'>💰 Total Amount Paid: ₹" + totalPrice + "/-</h3>");

		// Closing
		emailBody.append(
				"<p style='margin-top: 30px;'>We look forward to seeing you at the show! If you have any questions or need assistance, feel free to contact our support team.</p>");
		emailBody.append("<p>Warm regards,<br/><strong>Movie Magic Team</strong></p>");

		emailBody.append("</body></html>");

		return emailBody.toString();
	}

}
