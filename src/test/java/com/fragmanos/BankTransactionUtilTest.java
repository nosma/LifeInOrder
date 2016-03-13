package com.fragmanos;

import java.util.*;

import com.fragmanos.database.model.BankTransaction;
import com.fragmanos.util.BankTransactionUtil;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BankTransactionUtilTest {

  @Autowired
  BankTransactionUtil bankTransactionUtil;

  List<BankTransaction> bankTransactionList;
  List<BankTransaction> databaseTransactionList;

  @BeforeMethod
  public void setUp() throws Exception {
    bankTransactionList = new ArrayList<>();
    databaseTransactionList = new ArrayList<>();
    bankTransactionUtil = new BankTransactionUtil();
    setBankTransactionsList();
    setDatabaseTransactionsList();
  }

  @Test
  public void testDuplicationAvoidanceWorksAsExpected() throws Exception {
    assertEquals(4, bankTransactionUtil.unionOfBankTransactions(bankTransactionList, databaseTransactionList).size());
  }

  @Test
  public void testListDifference() throws Exception {
    assertEquals(1, bankTransactionUtil.differenceOfBankTransactions(bankTransactionList,databaseTransactionList).size());
  }

  private void setDatabaseTransactionsList() {
    databaseTransactionList.add(new BankTransaction(new LocalDate(2016, 3, 11), "Transaction #1", 100.0));
    databaseTransactionList.add(new BankTransaction(new LocalDate(2016, 3, 12), "Transaction #2", 200.0));
    databaseTransactionList.add(new BankTransaction(new LocalDate(2015, 5, 5), "Transaction #3", 555.5));
  }

  private void setBankTransactionsList() {
    bankTransactionList.add(new BankTransaction(new LocalDate(2016, 3, 11), "Transaction #1", 100.0));
    bankTransactionList.add(new BankTransaction(new LocalDate(2016, 3, 11), "Transaction #1", 100.0));
    bankTransactionList.add(new BankTransaction(new LocalDate(2016, 3, 12), "Transaction #2", 200.0));
    bankTransactionList.add(new BankTransaction(new LocalDate(2015, 4, 10), "Transaction #1000", 1000.0));
  }

}