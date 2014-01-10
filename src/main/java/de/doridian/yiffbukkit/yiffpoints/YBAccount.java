package de.doridian.yiffbukkit.yiffpoints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YBAccount {
	private final YBBank bank;

	private double balance;

	public YBAccount(YBBank bank, Map<String, List<String>> section) {
		this(bank);
		balance = Double.parseDouble(section.get("balance").get(0));
	}


	public YBAccount(YBBank bank) {
		this.bank = bank;
	}


	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
		bank.save();
	}


	public void addFunds(double cents) {
		balance += cents;
		bank.save();
	}

	public void useFunds(double cents) throws NotEnoughFundsException {
		if (balance < cents)
			throw new NotEnoughFundsException(cents - balance);

		balance -= cents;
		bank.save();
	}


	public Map<String, List<String>> save() {
		if (balance == 0.0)
			return null;

		Map<String, List<String>> section = new HashMap<>();
		section.put("balance", Arrays.asList(""+balance));
		return section;
	}
}
