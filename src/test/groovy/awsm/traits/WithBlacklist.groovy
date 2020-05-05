package awsm.traits

import awsm.stubs.BlacklistStub
import org.junit.After
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired

trait WithBlacklist {

    @Autowired
    private BlacklistStub blacklist

    @Before
    def startBlacklist() {
        println "Starting blacklist"
        blacklist.start()
    }

    @After
    def stopBlacklist() {
        println "Stopping blacklist"
        blacklist.stop()
    }

    BlacklistStub blacklist() {
        blacklist
    }

}