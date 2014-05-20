/**
 * This file is part of YiffBukkit.
 *
 * YiffBukkit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * YiffBukkit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with YiffBukkit.  If not, see <http://www.gnu.org/licenses/>.
 */
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
import java.util.UUID;

public class YBBank extends StateContainer {
	Map<UUID, YBAccount> accounts = new HashMap<>();

	private YBAccount getAccount(UUID playerName) {
		YBAccount account = accounts.get(playerName);

		if (account == null) {
			accounts.put(playerName, account = new YBAccount(this));
		}

		return account;
	}


	public double getBalance(UUID playerName) {
		return getAccount(playerName).getBalance();
	}

	public void setBalance(UUID playerName, double balance) {
		getAccount(playerName).setBalance(balance);
	}


	public void addFunds(UUID playerName, double cents) {
		getAccount(playerName).addFunds(cents);
	}

	public void useFunds(UUID playerName, double cents, String purpose) throws NotEnoughFundsException, ItemHasNoPriceException {
		if(cents <= 0)
			throw new ItemHasNoPriceException();
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
	public boolean checkPermissionsOrUseFunds(CommandSender commandSender, String permission, double cents, String purpose) throws NotEnoughFundsException, ItemHasNoPriceException {
		if (commandSender.hasPermission(permission))
			return false;

		useFunds(commandSender.getUniqueId(), cents, purpose);
		return true;
	}

	@Loader("bank")
	public void load() {
		accounts.clear();

		Map<String, List<Map<String, List<String>>>> sections = Ini.load("bank.txt");
		if (sections == null)
			return;

		for (Entry<String, List<Map<String, List<String>>>> entry : sections.entrySet()) {
			UUID playerName = UUID.fromString(entry.getKey());
			List<Map<String, List<String>>> namesakes = entry.getValue();

			accounts.put(playerName, new YBAccount(this, namesakes.get(0)));

			if (namesakes.size() > 1) {
				System.err.println("Duplicate section in bank.txt.");
			}
		}
	}

	@Saver("bank")
	public void save() {
		Map<String, List<Map<String, List<String>>>> sections = new TreeMap<>();
		for (Entry<UUID, YBAccount> entry : accounts.entrySet()) {
			YBAccount account = entry.getValue();
			final Map<String, List<String>> section = account.save();
			if (section == null)
				continue;

			sections.put(entry.getKey().toString(), Arrays.asList(section));
		}

		Ini.save("bank.txt", sections);
	}
}
