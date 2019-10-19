package awsm.application.trading.impl;

import static awsm.application.trading.impl.$.$;
import static awsm.application.trading.impl.Offer.Status.PENDING;

import com.google.common.base.Preconditions;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.annotation.Nullable;

public class Offer {

  enum Status {
    PENDING, ACCEPTED
  }

  @Nullable
  private Long id;

  private Status status;

  private $ price;

  public Offer($ price) {
    this.price = price;
    this.status = PENDING;
  }

  Offer(ResultSet rs) throws SQLException {
    this.price = $(rs.getBigDecimal("price"));
    this.status = Status.valueOf(rs.getString("status"));
  }

  public void raiseBy($ margin) {
    this.price = this.price.add(margin);
  }

  public void accept() {
    Preconditions.checkState(status == PENDING, "Cannot accept %s offer. Must be %s.", status, PENDING);
    this.status = Status.ACCEPTED;
  }

  public $ price() {
    return price;
  }

  Status status() {
    return status;
  }

}
