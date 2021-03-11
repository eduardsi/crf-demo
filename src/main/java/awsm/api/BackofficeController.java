package awsm.api;

import awsm.domain.banking.aml.TransactionsForReviewRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BackofficeController {

  private final TransactionsForReviewRepository txForReviewRepository;

  BackofficeController(TransactionsForReviewRepository txForReviewRepository) {
    this.txForReviewRepository = txForReviewRepository;
  }

  @GetMapping("/pending-reviews/count")
  long transactionsForReview() {
    return txForReviewRepository.count();
  }
}
