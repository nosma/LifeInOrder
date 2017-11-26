package life.file;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public interface DateUtils {

  LocalDate getStatementDate(String date);

  LocalDate getMidataDate(String date);

}
