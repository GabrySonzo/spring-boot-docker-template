package it.paleocapa.mastroiannim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.*;

@Service
public class JavaBossBot extends TelegramLongPollingBot {
	public class Prodotto{
		String nome;
		double prezzo;
		public Prodotto(String nome, double prezzo){
			this.nome = nome;
			this.prezzo = prezzo;
		}

		@Override
		public Prodotto clone(){
			return new Prodotto(nome, prezzo);
		}
	}
	LinkedList<Prodotto> carrello = new LinkedList<Prodotto>();
	boolean ordine = false;
	
	private static final Logger LOG = LoggerFactory.getLogger(JavaBossBot.class);
	
	private String botUsername;
	private static String botToken;
	private static JavaBossBot instance;
	public static JavaBossBot getJavaBossBotInstance(String botUsername, String botToken){
		if(instance == null) {
			instance = new JavaBossBot();
			instance.botUsername = botUsername;
			JavaBossBot.botToken = botToken;
		}
		return instance;
	}
	
	private JavaBossBot(){
		super(botToken);
	}
	
	@Override
	public String getBotToken() {
		return botToken;
	}
	
	@Override
	public String getBotUsername() {
		return botUsername;
	}
	
	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			
			long chatId = update.getMessage().getChatId();
			String command = update.getMessage().getText();
			SendMessage message = new SendMessage();
			message.setChatId(chatId);
			
			if(command.equals("/start")){
				message.setText("Ciao, sono HAL9000");
			}
			
			LinkedList<Prodotto> foodList = new LinkedList<Prodotto>();
			foodList.add(new Prodotto("Pane e cotoletta", 2));
			foodList.add(new Prodotto("Piadina cotoletta patatine e maionese", 2.80));
			foodList.add(new Prodotto("Pizza piegata", 1.50));
		   
			LinkedList<Prodotto> drinkList = new LinkedList<Prodotto>();
			drinkList.add(new Prodotto("Acqua", 0.40));
			drinkList.add(new Prodotto("Pepsi", 0.70));
			drinkList.add(new Prodotto("The", 0.60));
			
			if(command.equals("/listaCibo")){
				String s = foodList.stream().reduce("", (String part, Prodotto act) -> {part += act.nome + ": " + act.prezzo + "\n"; return part;}, (String s1, String s2) -> s1);
				ordine = false;
				message.setText(s);
			}
			
			if(command.equals("/listaBibite")){
				String s = drinkList.stream().reduce("", (String part, Prodotto act) -> {part += act.nome + ": " + act.prezzo + "\n"; return part;}, (String s1, String s2) -> s1);
				ordine = false;
				message.setText(s);
			}

			if(command.equals("/carrello")){
				String s = carrello.stream().reduce("", (String part, Prodotto act) -> {part += act.nome + ": " + act.prezzo + "\n"; return part;}, (String s1, String s2) -> s1);
				ordine = false;
				message.setText(s);
			}

			if(command.equals("/ordine")){
				ordine = true;
				message.setText("Digita il nome del prodotto che vuoi ordinare");
			}

			if(command.equals("/totale")){
				double t = carrello.stream().reduce((double)0, (p,a) -> p=p+a.prezzo, Double::sum);
				ordine = false;
				message.setText("Devi pagare in tutto" + t);
				message.setText("suca");
			}

			if(ordine){
				foodList.stream().forEach( x -> {if(command.equals(x.nome)){
					carrello.add(x.clone());
					message.setText("Prodotto aggiunto al carrello");
				}});

				drinkList.stream().forEach( x -> {if(command.equals(x.nome)){
					carrello.add(x.clone());
					message.setText("Prodotto aggiunto al carrello");
				}});
			}

			try {
				execute(message);
			} catch (TelegramApiException e) {
				LOG.error(e.getMessage());
			}
		}
	}
}

