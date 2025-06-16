package pl.lodz.p.backend.common.exception;

public class ErrorMessages {
    public static final String USER_NOT_FOUND = "exception.user.not_found";
    public static final String USER_EXIST_BY_NAME = "exception.user.exist_by_name";
    public static final String USER_EXIST_BY_EMAIL = "exception.user.exist_by_email";
    public static final String APPOINTMENT_NOT_FOUND = "exception.appointment.not_found";
    public static final String APPOINTMENT_EXIST = "exception.appointment.exist";
    public static final String HAIR_OFFER_NOT_FOUND = "exception.hair_offer.not_found";
    public static final String HAIR_OFFER_EXIST_BY_NAME = "exception.hair_offer.exist_by_name";
    public static final String APPOINTMENT_RESCHEDULED_DATE_NOT_FOUD = "exception.appointment.reschedule.not_found";
    public static final String WALLET_ALREADY_EXISTS = "Wallet for this account already exists.";
    public static final String WALLET_NOT_FOUND = "Wallet not found.";
    public static final String AMOUNT_MUST_BE_POSITIVE = "Amount must be positive.";
    public static final String INSUFFICIENT_FUNDS = "Insufficient funds in wallet.";
    public static final String CANNOT_TRANSFER_TO_SELF = "Cannot transfer funds to the same wallet.";
    public static final String APPOINTMENT_NOT_COMPLETED = "Appointment must be in COMPLETED status to be paid.";
    public static final String NOT_APPOINTMENT_CUSTOMER = "Only the customer associated with this appointment can pay for it.";
    public static final String CUSTOMER_WALLET_NOT_FOUND = "Customer's wallet not found.";
    public static final String HAIRDRESSER_WALLET_NOT_FOUND = "Hairdresser's wallet not found.";

    private ErrorMessages(){

    }

}
