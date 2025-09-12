package com.openclassrooms.PayMyBuddyAPIWeb;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PayMyBuddyApiWebApplicationTests {

	@Test
	void contextLoads() {
		// ce test reste utile pour vérifier le démarrage du contexte Spring

	}

	@Test
	void mainMethodExecutes() {
		PayMyBuddyApiWebApplication.main(new String[]{});
	}

}
