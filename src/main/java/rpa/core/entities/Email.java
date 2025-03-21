package rpa.core.entities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeBodyPart;
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.OrTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;

import botrix.internal.logging.LoggerFactory;
import rpa.core.driver.G;

public class Email {
	private String emailId = StringUtils.EMPTY;
	private String fileExtension = StringUtils.EMPTY;
	private List<String> fromEmails = new ArrayList<>();
	private String lookBackHours = StringUtils.EMPTY;
	private String uploadPath = StringUtils.EMPTY;
	private List<String> subjects = new ArrayList<>();

	private static Store store = null;
	private static Folder emailFolder = null;
	public static final String IP_EMAIL = "ipa@br.iq";
	public static final String IPNOTIFICATIONS_EMAIL = "ipa-notifications@br.iq";
	public static final String IPNOTIFICATIONS_APP_PWD = "wzpqpmebffwjhtqj";
	private static String host = "pop.gmail.com";
	private static Logger LOGGER = LoggerFactory.getLogger(Email.class);

	public static String get5DigitCode(String text) {
		LOGGER.info("Getting 5 digit verification code");
		Pattern pattern = Pattern.compile(" \\b\\d{5}\\b");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			return StringUtils.trim(matcher.group(0));
		}

		return StringUtils.EMPTY;

	}

	public static String getLatestMessage(String user, String password, String from, String subjectPhrase,
			Date dateReceived, int minutesToWait) throws IOException, MessagingException {
		int retries = 1;
		String emailContent = "";
		do {
			emailContent = Email.getLatestMessage(user, password, from, subjectPhrase, dateReceived);
//      emailContent = Email.getLatestEmail(user, password, from, subjectPhrase, dateReceived);
			if (StringUtils.isBlank(emailContent)) {
				LOGGER.info("Waiting for 15 secs for email");
				G.wait.sleep(15);
			}
			retries++;
		} while (retries <= minutesToWait * 4 && StringUtils.isBlank(emailContent));
		return emailContent;
	}

	public static String getLatestMessage(String user, String password, String from, String subjectPhrase,
			Date dateReceived) throws IOException, MessagingException {
		return getLatestEmail(user, password, from, subjectPhrase, dateReceived);
	}

	public static String getLatestEmail(String user, String password, String from, String subjectPhrase,
			Date dateReceived) throws IOException, MessagingException {
		Message email = getLatestFullEmail(user, password, from, subjectPhrase, dateReceived);
		if (email != null) {
			return getMailContent(email);

		} else {
			return StringUtils.EMPTY;
		}
	}

	public static String getMailContent(Message message) throws MessagingException, IOException {
		String contentType;
		StringBuilder partContent = new StringBuilder();
		if (message != null) {
			try {
				LOGGER.info("Looking into mail content");
				contentType = message.getContentType();
				if (contentType.contains("multipart")) {
					Multipart multiPart = (Multipart) message.getContent();

					for (int i = 0; i < multiPart.getCount(); i++) {
						MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(i);

						if (part.getContentType().toString().contains("TEXT/")) {
							partContent.append(part.getContent().toString()).append(System.lineSeparator());
						}
					}
				} else {
					return message.getContent().toString();
				}
			} catch (MessagingException e) {
				LOGGER.error("Unable to read mail content", e);
				throw e;
			} catch (IOException e) {
				LOGGER.error("Unable to download attachment", e);
				throw e;
			}
		}
		return partContent.toString();
	}

	public static Message getLatestFullEmail(String user, String password, String from, String subjectPhrase,
			Date dateReceived, int minutesToWait) throws IOException, MessagingException {
		int retries = 1;
		Message emailContent = null;
		do {
			emailContent = Email.getLatestFullEmail(user, password, from, subjectPhrase, dateReceived);
			if (emailContent == null) {
				LOGGER.info("Waiting for 30 secs for email");
				G.wait.sleep(30);
			}
			retries++;
		} while (retries <= minutesToWait * 2 && emailContent == null);
		return emailContent;
	}

	public static Message getLatestFullEmail(String user, String password, String from, String subjectPhrase,
			Date dateReceived) throws MessagingException, IOException {
		Message[] messages = getAllEmails(user, password, from, subjectPhrase, dateReceived);
		if (messages != null && messages.length > 0) {
			Message latestMessage = messages[messages.length - 1];
			if (latestMessage.getReceivedDate().after(dateReceived)) {
				LOGGER.info(String.format("Latest email received on %s. Email: %s",
						latestMessage.getReceivedDate().toString(), latestMessage.getContent().toString()));
				return latestMessage;
			}
		}
		return null;
	}

	public static Message[] getAllEmails(String user, String password, String from, String subjectPhrase,
			Date dateReceived) {
		List<String> fromEmails = new ArrayList<>();
		List<String> subjectPhrases = new ArrayList<>();
		fromEmails.add(from);
		subjectPhrases.add(subjectPhrase);
		return getAllEmails(user, password, fromEmails, subjectPhrases, dateReceived);
	}

	public static Message[] getAllEmails(String user, String password, List<String> fromEmails,
			List<String> subjectPhrases, Date dateReceived) {
		try {
			LOGGER.info("Retrieving email");
			Properties properties = new Properties();
			properties.put("mail.smtp.host", "smtp.gmail.com");
			properties.put("mail.smtp.socketFactory.port", "465");
			properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.port", "465");
			Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, password);
				}
			});
			// SearchTerm sender = new FromTerm(new InternetAddress(from));
			List<SearchTerm> searchTerms = new ArrayList<>();
			if (CollectionUtils.isNotEmpty(fromEmails) && StringUtils.isNotBlank(fromEmails.get(0))) {
				List<FromStringTerm> emailTerms = new ArrayList<>();
				for (String subject : fromEmails) {
					if (StringUtils.isNotBlank(subject))
						emailTerms.add(new FromStringTerm(subject));
				}
				if (CollectionUtils.isNotEmpty(emailTerms)) {
					SearchTerm orEmailTerm = new OrTerm(emailTerms.toArray(new SearchTerm[emailTerms.size()]));
					searchTerms.add(orEmailTerm);
				}
			}
			if (dateReceived != null) {
				searchTerms.add(new ReceivedDateTerm(ComparisonTerm.GE, dateReceived));
			}
			if (CollectionUtils.isNotEmpty(subjectPhrases) && StringUtils.isNotBlank(subjectPhrases.get(0))) {
				List<SubjectTerm> subjectTerms = new ArrayList<>();
				for (String subject : subjectPhrases) {
					if (StringUtils.isNotBlank(subject))
						subjectTerms.add(new SubjectTerm(subject));
				}
				if (CollectionUtils.isNotEmpty(subjectTerms)) {
					SearchTerm orSubjectTerm = new OrTerm(subjectTerms.toArray(new SearchTerm[subjectTerms.size()]));
					searchTerms.add(orSubjectTerm);
				}
			}
			SearchTerm andTerm = new AndTerm(searchTerms.toArray(new SearchTerm[searchTerms.size()]));

			// create the POP3 store object and connect with the pop server
			store = session.getStore("imaps");

			store.connect(host, user, password);

			emailFolder = store.getFolder("INBOX");
			emailFolder.open(Folder.READ_ONLY);

			// Message[] messages = emailFolder.getMessages();
			Message[] messages = emailFolder.search(andTerm);
			LOGGER.info(messages.length + " emails found in inbox.");
			List<Message> filteredMessages = new ArrayList<>();
			for (Message message : messages) {
				if (message.getReceivedDate().after(dateReceived) || message.getReceivedDate().equals(dateReceived)) {
					filteredMessages.add(message);
				}
			}
			LOGGER.info(CollectionUtils.size(filteredMessages) + " filtered emails found after " + dateReceived);
			return filteredMessages.toArray(new Message[filteredMessages.size()]);
			// close the store and folder objects
			// emailFolder.close(false);
			// store.close();

		} catch (NoSuchProviderException e) {
			LOGGER.error("", e);
		} catch (MessagingException e) {
			LOGGER.error("", e);
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return null;
	}

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getFileExtension() {
		return fileExtension;
	}

	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getLookBackHours() {
		return lookBackHours;
	}

	public void setLookBackHours(String lookBackHours) {
		this.lookBackHours = lookBackHours;
	}

	public List<String> getFromEmails() {
		return fromEmails;
	}

	public void setFromEmails(List<String> fromEmails) {
		this.fromEmails = fromEmails;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<String> subjects) {
		this.subjects = subjects;
	}

	public String getUploadPath() {
		return uploadPath;
	}

	public void setUploadPath(String uploadPath) {
		this.uploadPath = uploadPath;
	}
}