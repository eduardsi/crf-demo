package awsm.application.trading.impl;

import java.util.HashMap;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Component
public class Offers {

  private final DataSource dataSource;
  private final JdbcTemplate jdbc;

  public Offers(DataSource dataSource, JdbcTemplate jdbc) {
    this.dataSource = dataSource;
    this.jdbc = jdbc;
  }

  public Optional<Offer> singleById(long id) {
    var offers = jdbc.query("SELECT * FROM offer o WHERE o.id = ?",
        (rs, rowNum) -> new Offer(rs), id);

    return offers.stream().findFirst();
  }

  public long add(Offer offer) {
    var jdbcInsert = new SimpleJdbcInsert(dataSource)
        .withTableName("offer")
        .usingGeneratedKeyColumns("id");

    var args = new HashMap<String, Object>();
    args.put("price", offer.price().big());
    args.put("status", offer.status().name());

    return (long) jdbcInsert.executeAndReturnKey(args);
  }

  public void save(long id, Offer offer) {
    jdbc.update("update offer set price = ? and status = ? where id = ?",
        offer.price().big(),
        offer.status().name(),
        id);

  }
}
