package com.movieticketbooking.utility;

public class Constants {
	
	public enum UserRole {
		ROLE_ADMIN("Admin"),
		ROLE_THEATRE("Theatre"), 
		ROLE_CUSTOMER("Customer");

		private String role;

		private UserRole(String role) {
			this.role = role;
		}

		public String value() {
			return this.role;
		}
	}
	
	public enum ActiveStatus {
		ACTIVE("Active"),
		DEACTIVATED("Deactivated");
		
		
		private String status;

	    private ActiveStatus(String status) {
	      this.status = status;
	    }

	    public String value() {
	      return this.status;
	    }    
	}
	
	public enum TheatreStatus {
		ACTIVE("Active"),
		DEACTIVATED("Deactivated"),
		PENDNING("Pending"),
		REJECTED("Rejected");

		private String status;

		private TheatreStatus(String status) {
			this.status = status;
		}

		public String value() {
			return this.status;
		}
	}
	
	public enum ScreenSeatPosition {
		LEFT("Left"),
		RIGHT("Right"),
		MIDDLE("Middle");
		
		private String position;

	    private ScreenSeatPosition(String position) {
	      this.position = position;
	    }

	    public String value() {
	      return this.position;
	    }    
	}
	
	public enum SeatType {
		REGULAR("Regular"),
		PREMIUM("Premium"),
		GOLD("Gold");
		
		private String type;

	    private SeatType(String type) {
	      this.type = type;
	    }

	    public String value() {
	      return this.type;
	    }    
	}
	
	public enum MovieCertification {
		UA("UA"),
		A("A"),
		U("U"),
		S("S");
		
		private String certification;

	    private MovieCertification(String certification) {
	      this.certification = certification;
	    }

	    public String value() {
	      return this.certification;
	    }    
	}
	
	public enum MovieFormat {
		TWO_D("2D"),
		THREE_D("3D"),
		IMAX("IMAX");
		
		private String format;

	    private MovieFormat(String format) {
	      this.format = format;
	    }

	    public String value() {
	      return this.format;
	    }    
	}
	
	public enum MovieLanguage {
	    HINDI("Hindi"),
	    ENGLISH("English"),
	    TAMIL("Tamil"),
	    TELUGU("Telugu"),
	    KANNADA("Kannada"),
	    MALAYALAM("Malayalam"),
	    MARATHI("Marathi"),
	    BENGALI("Bengali"),
	    GUJARATI("Gujarati"),
	    PUNJABI("Punjabi"),
	    OTHER("Other");

	    private String language;

	    private MovieLanguage(String language) {
	        this.language = language;
	    }

	    public String value() {
	        return this.language;
	    }
	}
	
	public enum MovieGenre {
	    ACTION("Action"),
	    COMEDY("Comedy"),
	    DRAMA("Drama"),
	    HORROR("Horror"),
	    THRILLER("Thriller"),
	    ROMANCE("Romance"),
	    FANTASY("Fantasy"),
	    SCI_FI("Sci-Fi"),
	    ANIMATION("Animation"),
	    DOCUMENTARY("Documentary"),
	    ADVENTURE("Adventure"),
	    MYSTERY("Mystery"),
	    CRIME("Crime"),
	    MUSICAL("Musical"),
	    HISTORICAL("Historical"),
	    BIOGRAPHY("Biography"),
	    FAMILY("Family"),
	    OTHER("Other");

	    private final String genre;

	    private MovieGenre(String genre) {
	        this.genre = genre;
	    }

	    public String value() {
	        return this.genre;
	    }
	}


	public enum ShowStatus {
	    ACTIVE("Active"),
	    CANCELLED("Cancelled"),
	    COMPLETED("Completed"),
	    UPCOMING("Upcoming");

	    private String status;

	    private ShowStatus(String status) {
	        this.status = status;
	    }

	    public String value() {
	        return this.status;
	    }
	}
	
	public enum BookingStatus {
		AVAILABLE("Available"),
	    BOOKED("Booked"),
	    CANCELLED("Cancelled");

	    private String status;

	    private BookingStatus(String status) {
	        this.status = status;
	    }

	    public String value() {
	        return this.status;
	    }
	}

	public enum PaymentGatewayTxnType {
		CREATE_ORDER("Create Order"), PAYMENT("Payment");

		private String type;

		private PaymentGatewayTxnType(String type) {
			this.type = type;
		}

		public String value() {
			return this.type;
		}
	}

	public enum PaymentGatewayTxnStatus {
		SUCCESS("Success"), FAILED("Failed");

		private String type;

		private PaymentGatewayTxnStatus(String type) {
			this.type = type;
		}

		public String value() {
			return this.type;
		}
	}
	
}
