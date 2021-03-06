package com.learnwithted.kidbank.adapter.sms;

import com.learnwithted.kidbank.adapter.ScaledDecimals;
import com.learnwithted.kidbank.domain.Account;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandParser {

  private final Account account;

  public CommandParser(Account account) {
    this.account = account;
  }

  public TransactionCommand parse(String commandText) {
    commandText = commandText.trim();

    String[] tokens = commandText.split(" ");
    String commandToken = tokens[0].toLowerCase();

    try {
      switch (commandToken) {
        case "balance":
          return new BalanceCommand(account);

        case "goals":
          return new GoalsCommand(account);

        case "deposit":
          return new DepositCommand(account, parseAmount(tokens), descriptionFrom(tokens));

        case "spend":
          return new SpendCommand(account, parseAmount(tokens), descriptionFrom(tokens));
      }

      return new InvalidCommand(commandText);

    } catch (InvalidCommandException e) {
      return new InvalidCommand(commandText);
    } catch (IllegalArgumentException e) {
      return new InvalidCommand(e.getMessage());
    }
  }

  private String descriptionFrom(String[] tokens) {
    return Arrays.stream(tokens)
                 .skip(2) // skip command and amount
                 .collect(Collectors.joining(" "));
  }

  private int parseAmount(String[] parsed) {
    try {
      String stringAmount = parsed[1].replaceAll("\\$", "");
      return ScaledDecimals.decimalToPennies(stringAmount);
    } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
      throw new InvalidCommandException();
    }
  }
}
