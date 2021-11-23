package com.fufumasi.AdGameServer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootTest
class AdGameServerApplicationTests {
	@Inject
	private DataSource ds;
	@Value("${jwt.issuer}")
	private String issuer;

	@Test
	public void testConnection() throws Exception {
		try (Connection con = ds.getConnection()) {
			System.out.println(con);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
