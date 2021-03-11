package awsm.domain.banking.aml;

import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

@Component
public interface TransactionsForReviewRepository extends Repository<TransactionForReview, Long> {

  void save(TransactionForReview txForReview);

  long count();
}
