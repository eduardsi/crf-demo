package awsm.api

import com.github.javafaker.Faker

trait WithFaker {

    Faker fake() {
        return new Faker();
    }

}