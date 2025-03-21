package botrix.telegram;

import org.slf4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import botrix.internal.logging.LoggerFactory;
import lombok.SneakyThrows;

public class TelegramHelper implements AutoCloseable {
	private static final Logger LOGGER = LoggerFactory.getLogger(TelegramHelper.class);

	private TelegramLongPollingBot bot;
	private static final TelegramBotsApi botsApi;
	private static DefaultBotSession session = null;

	public TelegramHelper(TelegramLongPollingBot bot) throws TelegramApiException {
		this.bot = bot;
		session = (DefaultBotSession) botsApi.registerBot(bot);
	}

	static {
		try {
			botsApi = new TelegramBotsApi(DefaultBotSession.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SneakyThrows
	public void sendMessage(String channelId, String text) {
		SendMessage msg = new SendMessage(channelId, text);
		msg.enableHtml(true);
		bot.execute(msg);
	}

	public void close() {
		try {
			bot.clearWebhook();
			session.stop();
		} catch (TelegramApiRequestException e) {
			e.printStackTrace();
		}
	}
}
