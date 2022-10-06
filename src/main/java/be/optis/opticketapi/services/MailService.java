package be.optis.opticketapi.services;

import be.optis.opticketapi.models.Account;
import be.optis.opticketapi.models.ticket.Ticket;
import be.optis.opticketapi.models.ticket.TicketState;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceAsync;
import com.amazonaws.services.simpleemail.model.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static be.optis.opticketapi.util.MailTemplates.NEW_TICKET_TEMPLATE;
import static be.optis.opticketapi.util.MailTemplates.STATUS_CHANGE_MAIL_TEMPLATE;

@Service
@AllArgsConstructor
@Log4j2
public class MailService {

    private final AmazonSimpleEmailServiceAsync asyncSESClient;

    public void sendStateChangeEmail(Ticket ticket, TicketState previousState) {
        var subject = String.format("%s: Status veranderd naar \"%s\"",
                ticket.getTitle(), ticket.getState().getReadable());
        var textBody =
                String.format("Statuswijziging in ticket \"%s\"%n", ticket.getTitle()) +
                String.format("%s --> %s%n", previousState.getReadable(), ticket.getState().getReadable());
        var htmlBody = STATUS_CHANGE_MAIL_TEMPLATE
                .replace("{ticket_title}", ticket.getTitle())
                .replace("{ticket_id}", String.valueOf(ticket.getTicketId()))
                .replace("{previous_state}", previousState.getReadable())
                .replace("{new_state}", ticket.getState().getReadable());
        var toAddresses = new ArrayList<String>();
        toAddresses.add(ticket.getCreator().getEmail());
        toAddresses.addAll(ticket.getFollowers().stream().map(Account::getEmail).toList());

        createAndSendRequest(subject, textBody, htmlBody, toAddresses);
    }

    public void sendNewTicketEmail(Ticket ticket, List<Account> accounts) {
        var subject = String.format("Nieuw open ticket: \"%s\"", ticket.getTitle());
        var textBody = String.format("Er werd een nieuw ticket aangemaakt met titel \"%s\".", ticket.getTitle());
        var htmlBody = NEW_TICKET_TEMPLATE
                .replace("{ticket_title}", ticket.getTitle())
                .replace("{ticket_id}", String.valueOf(ticket.getTicketId()));
        var toAddresses = accounts.stream().map(Account::getEmail).toList();

        createAndSendRequest(subject, textBody, htmlBody, toAddresses);
    }

    private void createAndSendRequest(String subject, String textBody, String htmlBody, List<String> toAddresses) {
        var request = new SendEmailRequest()
                .withDestination(
                        new Destination().withToAddresses(toAddresses))
                .withMessage(
                        new Message()
                                .withSubject(new Content().withCharset("UTF-8").withData(subject))
                                .withBody(new Body()
                                        .withText(new Content().withCharset("UTF-8").withData(textBody))
                                        .withHtml(new Content().withCharset("UTF-8").withData(htmlBody))))
                .withSource("opticket@optis.cloud");

        try {
            asyncSESClient.sendEmail(request);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
