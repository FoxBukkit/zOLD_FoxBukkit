package de.doridian.yiffbukkit.yiffpoints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;

import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.util.Ini;

public class YBBank extends StateContainer {
	Map<String, YBAccount> accounts = new HashMap<String, YBAccount>();

	private YBAccount getAccount(String playerName) {
		YBAccount account = accounts.get(playerName.toLowerCase());

		if (account == null) {
			accounts.put(playerName, account = new YBAccount(this));
		}

		return account;
	}


	public double getBalance(String playerName) {
		return getAccount(playerName).getBalance();
	}

	public void setBalance(String playerName, double balance) {
		getAccount(playerName).setBalance(balance);
	}


	public void addFunds(String playerName, double cents) {
		getAccount(playerName).addFunds(cents);
	}

	public void useFunds(String playerName, double cents, String purpose) throws NotEnoughFundsException {
		System.out.println(String.format("Player %s is trying to use %.0f YP on '%s'.", playerName, cents, purpose));
		getAccount(playerName).useFunds(cents);
		System.out.println(String.format("Player %s used %.0f YP on '%s'.", playerName, cents, purpose));
	}

	public void checkPermissionsOrUseFunds(CommandSender commandSender, String permission, double cents, String purpose) throws NotEnoughFundsException {
		if (commandSender.hasPermission(permission))
			return;

		useFunds(commandSender.getName(), cents, purpose);
	}

	@Loader("bank")
	public void load() {
		accounts.clear();

		Map<String, List<Map<String, List<String>>>> sections = Ini.load("bank.txt");
		if (sections == null)
			return;

		for (Entry<String, List<Map<String, List<String>>>> entry : sections.entrySet()) {
			String playerName = entry.getKey();
			List<Map<String, List<String>>> namesakes = entry.getValue();

			accounts.put(playerName.toLowerCase(), new YBAccount(this, namesakes.get(0)));

			if (namesakes.size() > 1) {
				System.err.println("Duplicate section in bank.txt.");
			}
		}
	}

	@Saver("bank")
	public void save() {
		Map<String, List<Map<String, List<String>>>> sections = new TreeMap<String, List<Map<String, List<String>>>>();
		for (Entry<String, YBAccount> entry : accounts.entrySet()) {
			YBAccount account = entry.getValue();
			final Map<String, List<String>> section = account.save();
			if (section == null)
				continue;

			@SuppressWarnings("unchecked")
			final List<Map<String, List<String>>> wrappedSection = Arrays.asList(section);
			sections.put(entry.getKey(), wrappedSection);
		}

		Ini.save("bank.txt", sections);
	}
}
