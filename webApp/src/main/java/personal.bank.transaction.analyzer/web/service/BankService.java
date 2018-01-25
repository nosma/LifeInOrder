package personal.bank.transaction.analyzer.web.service;

import org.springframework.stereotype.Component;
import personal.bank.transaction.analyzer.database.dao.BankTransactionDao;
import personal.bank.transaction.analyzer.database.dao.MidataTransactionDao;
import personal.bank.transaction.analyzer.database.dao.MonthStatDao;
import personal.bank.transaction.analyzer.database.model.BankTransaction;
import personal.bank.transaction.analyzer.util.BankTransactionUtil;
import personal.bank.transaction.analyzer.web.controller.BankInterface;
import personal.bank.transaction.analyzer.web.controller.TableObject;
import personal.bank.transaction.analyzer.web.controller.TagObject;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BankService implements BankInterface {

  private static final String UNTAGGED = "Untagged";
  private MonthStatDao monthStatDao;
  private BankTransactionDao bankTransactionDao;
  private MidataTransactionDao midataTransactionDao;
  private BankTransactionUtil bankTransactionUtil;

  public BankService() {
  }

  @Inject
  public BankService(MonthStatDao monthStatDao, BankTransactionDao bankTransactionDao, MidataTransactionDao midataTransactionDao) {
    this.monthStatDao = monthStatDao;
    this.bankTransactionDao = bankTransactionDao;
    this.midataTransactionDao = midataTransactionDao;
    bankTransactionUtil = new BankTransactionUtil();
  }

  @Override
  public List<TableObject> getTableObjects() {
    List<BankTransaction> allBankTransactions = bankTransactionDao.findAllByOrderByTransactiondateDesc();
    return bankTransactionUtil.getTableObjectList(allBankTransactions);
  }

  @Override
  public List<TableObject> getMonthlyExpensesList(int monthNumber, int yearNumber) {
    List<BankTransaction> bankTransactionList = new ArrayList<>();
    for (BankTransaction bankTransaction : bankTransactionDao.findAllByOrderByTransactiondateDesc()) {
      if ((bankTransaction.getTransactiondate().getMonthValue() == monthNumber) &&
          (bankTransaction.getTransactiondate().getYear() == yearNumber) &&
          (bankTransaction.getCost() < 0)) {
        bankTransactionList.add(bankTransaction);
      }
    }
    return bankTransactionUtil.getTableObjectList(bankTransactionList);
  }

  @Override
  public List<TableObject> getMonthlyIncomeList(int monthNumber, int yearNumber) {
    List<BankTransaction> bankTransactionList = new ArrayList<>();
    for (BankTransaction bankTransaction : bankTransactionDao.findAllByOrderByTransactiondateDesc()) {
      if ((bankTransaction.getTransactiondate().getMonthValue() == monthNumber) &&
          (bankTransaction.getTransactiondate().getYear() == yearNumber) &&
          (bankTransaction.getCost() > 0)) {
        bankTransactionList.add(bankTransaction);
      }
    }
    return bankTransactionUtil.getTableObjectList(bankTransactionList);
  }

  @Override
  public List<TagObject> getMonthlyTagsGroupedByTag(int month, int year) {
    final Map<String, Double> collect = bankTransactionDao.findAllByOrderByTransactiondateDesc().stream()
    .filter(t -> t.getTransactiondate().getYear() == year)
    .filter(t -> t.getTransactiondate().getMonthValue() == month)
    .map(t -> t.getTagRule() != null ?
        new TagObject(t.getTagRule().getDescription(), t.getCost()) :
        new TagObject(UNTAGGED, t.getCost()))
    .collect(Collectors.groupingBy(TagObject::getTagName, Collectors.summingDouble(TagObject::getAmount)));
 // todo fix the below
    List<TagObject> tagObjects = new ArrayList<>();
    for(String key : collect.keySet()) {
      tagObjects.add(new TagObject(key, collect.get(key)));
    }
    return tagObjects;


  }

}


