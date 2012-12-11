package de.doridian.yiffbukkit.yiffpoints;

import de.doridian.yiffbukkit.main.StateContainer;
import de.doridian.yiffbukkit.main.util.Ini;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class YBBank extends StateContainer {
	Map<String, YBAccount> accounts = new HashMap<String, YBAccount>();

	private YBAccount getAccount(String playerName) {
		YBAccount account = accounts.get(playerName.toLowerCase());

		if (account == null) {
			accounts.put(playerName.toLowerCase(), account = new YBAccount(this));
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

	/**
	 * Checks if the {@code commandSender} has the given {@code permission}.
	 * If not, use the given amount of {@code cents} for the given {@code purpose}.
	 *
	 * @param commandSender the user to be checked
	 * @param permission the permission to be checked
	 * @param cents the amount of YP to use
	 * @param purpose the purpose to log
	 * @return true if funds were used, false if no funds were needed
	 * @throws NotEnoughFundsException if there were not enough funds
	 */
	public boolean checkPermissionsOrUseFunds(CommandSender commandSender, String permission, double cents, String purpose) throws NotEnoughFundsException {
		if (commandSender.hasPermission(permission))
			return false;

		useFunds(commandSender.getName(), cents, purpose);
		return true;
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
