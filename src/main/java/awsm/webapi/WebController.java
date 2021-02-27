package awsm.webapi;

import an.awesome.pipelinr.Pipeline;
import awsm.commands.ApplyForBankAccountCommand;
import awsm.commands.RegisterCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class WebController {

  @Autowired Pipeline pipeline;

  @PostMapping("/registrations")
  RegisterCommand.Response register(@RequestBody RegisterCommand command) {
    return command.execute(pipeline);
  }

  @PostMapping("/bank-accounts")
  ApplyForBankAccountCommand.Response applyForBankAccount(
      @RequestBody ApplyForBankAccountCommand command) {
    return command.execute(pipeline);
  }
}
