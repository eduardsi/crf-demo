package awsm.acceptance

import com.github.javafaker.Faker

trait WithFaker {

    Faker fake() {
        return new Faker();
    }

}