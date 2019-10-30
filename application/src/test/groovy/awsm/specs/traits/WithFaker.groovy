package awsm.specs.traits

import com.github.javafaker.Faker

trait WithFaker {

    Faker fake() {
        new Faker()
    }

}