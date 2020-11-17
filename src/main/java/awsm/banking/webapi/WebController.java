package awsm.banking.webapi;

import an.awesome.pipelinr.Pipeline;
import awsm.banking.application.ApplyForBankAccount;
import awsm.banking.application.Register;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class WebController {

    @Autowired
    Pipeline pipeline;

    @PostMapping("/registrations")
    Register.Response register(@RequestBody Register command) {
        return pipeline.send(command);
    }

    @PostMapping("/bank-accounts")
    ApplyForBankAccount.Response applyForBankAccount(@RequestBody ApplyForBankAccount command) {
        return pipeline.send(command);
    }
}